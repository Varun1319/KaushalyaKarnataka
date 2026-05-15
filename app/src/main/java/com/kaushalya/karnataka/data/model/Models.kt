package com.kaushalya.karnataka.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

data class Worker(
    val workerId        : String    = "",
    val fullName        : String    = "",
    val phoneNumber     : String    = "",
    val skillCategory   : String    = "",
    val locationText    : String    = "",
    val bio             : String    = "",
    val profilePhotoUrl : String    = "",
    val avgRating       : Float     = 0f,
    val totalReviews    : Int       = 0,
    @get:PropertyName("isAvailable")
    @set:PropertyName("isAvailable")
    var isAvailable     : Boolean   = true,
    val createdAt       : Timestamp = Timestamp.now(),
)

data class ServiceCard(
    val cardId      : String    = "",
    val workerId    : String    = "",
    val title       : String    = "",
    val description : String    = "",
    val price       : Double    = 0.0,
    val priceType   : String    = "Fixed",
    val imageUrl    : String    = "",
    val categoryTag : String    = "",
    val createdAt   : Timestamp = Timestamp.now(),
)

data class Review(
    val reviewId     : String    = "",
    val workerId     : String    = "",
    val customerName : String    = "",
    val rating       : Int       = 5,
    val reviewText   : String    = "",
    val workerReply  : String    = "",
    val createdAt    : Timestamp = Timestamp.now(),
)

data class HireRequest(
    val requestId     : String    = "",
    val workerId      : String    = "",
    val customerName  : String    = "",
    val customerPhone : String    = "",
    val serviceNeeded : String    = "",
    val status        : String    = "Pending",
    val createdAt     : Timestamp = Timestamp.now(),
)

data class WorkPhoto(
    val photoId    : String    = "",
    val workerId   : String    = "",
    val photoUrl   : String    = "",
    val caption    : String    = "",
    val uploadedAt : Timestamp = Timestamp.now(),
)

object SkillCategories {
    val all = listOf("All", "Electrician", "Plumber", "Carpenter", "Painter", "Tailor", "Beautician")
}
