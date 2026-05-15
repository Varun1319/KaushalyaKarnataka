package com.kaushalya.karnataka.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.kaushalya.karnataka.BuildConfig
import com.kaushalya.karnataka.data.model.*
import com.kaushalya.karnataka.data.remote.AiPrompt
import com.kaushalya.karnataka.data.remote.AiPrompt.extractText
import com.kaushalya.karnataka.data.remote.OpenAiApiService
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkerRepository @Inject constructor(
    private val firestore  : FirebaseFirestore,
    private val auth       : FirebaseAuth,
    private val openAiApi  : OpenAiApiService,
) {

    val currentUid get() = auth.currentUser?.uid ?: ""

    // ── Workers ──────────────────────────────────────────────────────

    fun getAllWorkers(): Flow<List<Worker>> = callbackFlow {
        val listener = firestore.collection("workers")
            
            .addSnapshotListener { snap, err ->
                if (err != null) { close(err); return@addSnapshotListener }
                val list = snap?.documents?.mapNotNull { it.toObject(Worker::class.java) } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    fun getWorker(workerId: String): Flow<Worker?> = callbackFlow {
        val listener = firestore.collection("workers").document(workerId)
            .addSnapshotListener { snap, err ->
                if (err != null) { close(err); return@addSnapshotListener }
                trySend(snap?.toObject(Worker::class.java))
            }
        awaitClose { listener.remove() }
    }

    suspend fun saveWorker(worker: Worker) {
        firestore.collection("workers").document(worker.workerId).set(worker).await()
    }

    suspend fun updateBio(workerId: String, bio: String) {
        firestore.collection("workers").document(workerId).update("bio", bio).await()
    }

    suspend fun toggleAvailability(workerId: String, available: Boolean) {
        firestore.collection("workers").document(workerId).update("isAvailable", available).await()
    }

    // ── Service Cards ────────────────────────────────────────────────

    fun getServiceCards(workerId: String): Flow<List<ServiceCard>> = callbackFlow {
        val listener = firestore.collection("service_cards")
            .whereEqualTo("workerId", workerId)
            .orderBy("created_at", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, err ->
                if (err != null) { close(err); return@addSnapshotListener }
                val list = snap?.documents?.mapNotNull { it.toObject(ServiceCard::class.java) } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    suspend fun saveServiceCard(card: ServiceCard) {
        val ref = if (card.cardId.isBlank()) firestore.collection("service_cards").document()
                  else firestore.collection("service_cards").document(card.cardId)
        ref.set(card.copy(cardId = ref.id)).await()
    }

    suspend fun deleteServiceCard(cardId: String) {
        firestore.collection("service_cards").document(cardId).delete().await()
    }

    // ── Reviews ──────────────────────────────────────────────────────

    fun getReviews(workerId: String): Flow<List<Review>> = callbackFlow {
        val listener = firestore.collection("reviews")
            .whereEqualTo("workerId", workerId)
            .orderBy("created_at", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, err ->
                if (err != null) { close(err); return@addSnapshotListener }
                val list = snap?.documents?.mapNotNull { it.toObject(Review::class.java) } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    suspend fun addReview(review: Review) {
        val ref   = firestore.collection("reviews").document()
        ref.set(review.copy(reviewId = ref.id)).await()

        // Recalculate avg_rating
        val all   = firestore.collection("reviews")
            .whereEqualTo("workerId", review.workerId).get().await()
            .documents.mapNotNull { it.toObject(Review::class.java) }
        val avg   = all.map { it.rating }.average().toFloat()
        firestore.collection("workers").document(review.workerId)
            .update(mapOf("avg_rating" to avg, "total_reviews" to all.size)).await()
    }

    // ── Hire Requests ────────────────────────────────────────────────

    suspend fun sendHireRequest(request: HireRequest) {
        val ref = firestore.collection("hire_requests").document()
        ref.set(request.copy(requestId = ref.id)).await()
    }

    fun getHireRequests(workerId: String): Flow<List<HireRequest>> = callbackFlow {
        val listener = firestore.collection("hire_requests")
            .whereEqualTo("workerId", workerId)
            .orderBy("created_at", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, err ->
                if (err != null) { close(err); return@addSnapshotListener }
                val list = snap?.documents?.mapNotNull { it.toObject(HireRequest::class.java) } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    suspend fun updateHireRequestStatus(requestId: String, status: String) {
        firestore.collection("hire_requests").document(requestId).update("status", status).await()
    }

    // ── Work Photos ──────────────────────────────────────────────────

    fun getWorkPhotos(workerId: String): Flow<List<WorkPhoto>> = callbackFlow {
        val listener = firestore.collection("work_photos")
            .whereEqualTo("workerId", workerId)
            .orderBy("uploaded_at", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, err ->
                if (err != null) { close(err); return@addSnapshotListener }
                val list = snap?.documents?.mapNotNull { it.toObject(WorkPhoto::class.java) } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    // ── OpenAI Bio Generation ─────────────────────────────────────────

    suspend fun generateBio(
        name: String, skill: String, experience: String, speciality: String, location: String,
    ): Result<String> = runCatching {
        val request  = AiPrompt.bioPrompt(name, skill, experience, speciality, location)
        val response = openAiApi.chatCompletion("Bearer ${BuildConfig.OPENAI_API_KEY}", request)
        response.extractText()
    }
}
