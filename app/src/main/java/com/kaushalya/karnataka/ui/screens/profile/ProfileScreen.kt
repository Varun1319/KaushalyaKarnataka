package com.kaushalya.karnataka.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kaushalya.karnataka.data.model.Review
import com.kaushalya.karnataka.ui.components.*
import com.kaushalya.karnataka.ui.theme.*
import com.kaushalya.karnataka.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    workerId  : String,
    onBack    : () -> Unit,
    viewModel : ProfileViewModel = hiltViewModel(),
) {
    LaunchedEffect(workerId) { viewModel.loadWorker(workerId) }

    val worker          by viewModel.worker.collectAsState()
    val cards           by viewModel.serviceCards.collectAsState()
    val reviews         by viewModel.reviews.collectAsState()
    val photos          by viewModel.workPhotos.collectAsState()
    val hireRequestSent by viewModel.hireRequestSent.collectAsState()

    var selectedTab      by remember { mutableIntStateOf(0) }
    var showHireDialog   by remember { mutableStateOf(false) }
    var showReviewDialog by remember { mutableStateOf(false) }
    val snackbarHost     = remember { SnackbarHostState() }

    LaunchedEffect(hireRequestSent) {
        if (hireRequestSent) snackbarHost.showSnackbar("✅ Hire request sent!")
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHost) }) { paddingValues ->
        if (worker == null) {
            Box(Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = RoyalBlue)
            }
            return@Scaffold
        }
        val w = worker!!

        LazyColumn(modifier = Modifier.fillMaxSize().padding(paddingValues)) {

            // ── Hero ──────────────────────────────────────────────────
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().background(RoyalBlue).padding(bottom = 16.dp)
                ) {
                    IconButton(onClick = onBack, modifier = Modifier.align(Alignment.TopStart).padding(8.dp)) {
                        Icon(Icons.Filled.ArrowBack, "Back", tint = PureWhite)
                    }
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Box(
                            modifier = Modifier.size(80.dp).clip(CircleShape)
                                .background(PureWhite.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(w.fullName.take(1).uppercase(), fontSize = 32.sp, color = PureWhite, fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.height(10.dp))
                        Text(w.fullName, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = PureWhite)
                        Surface(
                            shape    = RoundedCornerShape(20.dp),
                            color    = PureWhite.copy(alpha = 0.15f),
                            modifier = Modifier.padding(vertical = 6.dp),
                        ) {
                            Text(w.skillCategory, color = PureWhite, fontSize = 13.sp,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp))
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            StarRow(w.avgRating, size = 16.dp)
                            Text("· ${w.totalReviews} reviews", color = PureWhite.copy(alpha = 0.8f), fontSize = 12.sp)
                        }
                        Spacer(Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            AvailabilityDot(w.isAvailable)
                            Text(
                                if (w.isAvailable) "Available now" else "Currently busy",
                                color = PureWhite.copy(alpha = 0.8f), fontSize = 12.sp,
                            )
                            Text("· 📍 ${w.locationText}", color = PureWhite.copy(alpha = 0.8f), fontSize = 12.sp)
                        }
                    }
                }
            }

            // ── Hire Me ───────────────────────────────────────────────
            item {
                Button(
                    onClick  = { showHireDialog = true },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 14.dp).height(50.dp),
                    shape    = RoundedCornerShape(40.dp),
                    colors   = ButtonDefaults.buttonColors(containerColor = SaffronOrange),
                ) {
                    Text("⚡ Hire Me", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            // ── Bio ───────────────────────────────────────────────────
            if (w.bio.isNotBlank()) {
                item {
                    Surface(color = OffWhite, modifier = Modifier.fillMaxWidth()) {
                        Text(w.bio, modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                            style = MaterialTheme.typography.bodyMedium, color = DarkGrey)
                    }
                }
            }

            // ── Tabs ──────────────────────────────────────────────────
            item {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor   = PureWhite,
                    contentColor     = ElectricBlue,
                ) {
                    listOf("Services", "Gallery", "Reviews").forEachIndexed { i, title ->
                        Tab(
                            selected = selectedTab == i,
                            onClick  = { selectedTab = i },
                            text     = {
                                Text(title, fontWeight = if (selectedTab == i) FontWeight.Bold else FontWeight.Normal)
                            }
                        )
                    }
                }
            }

            // ── Tab Content ───────────────────────────────────────────
            when (selectedTab) {
                0 -> {
                    if (cards.isEmpty()) {
                        item { EmptyTabState("No services listed yet.") }
                    } else {
                        items(cards) { card ->
                            ServiceCardItem(card, modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp))
                        }
                    }
                }
                1 -> {
                    if (photos.isEmpty()) {
                        item { EmptyTabState("No photos uploaded yet.") }
                    } else {
                        item {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(3),
                                modifier = Modifier.height(300.dp).padding(horizontal = 12.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalArrangement   = Arrangement.spacedBy(4.dp),
                            ) {
                                items(photos) {
                                    Box(
                                        modifier = Modifier.aspectRatio(1f).clip(RoundedCornerShape(8.dp))
                                            .background(ElectricBlue.copy(alpha = 0.12f)),
                                        contentAlignment = Alignment.Center,
                                    ) { Text("📷", fontSize = 28.sp) }
                                }
                            }
                        }
                    }
                }
                2 -> {
                    item {
                        TextButton(
                            onClick  = { showReviewDialog = true },
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        ) {
                            Text("+ Leave a Review", color = ElectricBlue, fontWeight = FontWeight.SemiBold)
                        }
                    }
                    if (reviews.isEmpty()) {
                        item { EmptyTabState("No reviews yet. Be the first!") }
                    } else {
                        items(reviews) { review ->
                            ReviewCardItem(review, modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp))
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(88.dp)) }
        }
    }

    if (showHireDialog) {
        HireMeDialog(
            workerName = worker!!.fullName,
            onDismiss  = { showHireDialog = false },
            onConfirm  = { name, phone, service ->
                viewModel.sendHireRequest(workerId, name, phone, service)
                showHireDialog = false
            }
        )
    }

    if (showReviewDialog) {
        AddReviewDialog(
            onDismiss = { showReviewDialog = false },
            onSubmit  = { name, rating, text ->
                viewModel.addReview(workerId, name, rating, text)
                showReviewDialog = false
            }
        )
    }
}

@Composable
private fun EmptyTabState(message: String) {
    Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
        Text(message, color = DarkGrey, fontSize = 14.sp)
    }
}

@Composable
private fun ReviewCardItem(review: Review, modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(PureWhite)) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(review.customerName, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            }
            Row {
                repeat(5) { i ->
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint     = if (i < review.rating) WarmGold else DarkGrey.copy(alpha = 0.3f),
                        modifier = Modifier.size(14.dp),
                    )
                }
            }
            if (review.reviewText.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(review.reviewText, style = MaterialTheme.typography.bodyMedium, color = DarkGrey)
            }
            if (review.workerReply.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Surface(color = OffWhite, shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("Worker replied:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ElectricBlue)
                        Text(review.workerReply, fontSize = 12.sp, color = DarkGrey)
                    }
                }
            }
        }
    }
}

@Composable
private fun HireMeDialog(workerName: String, onDismiss: () -> Unit, onConfirm: (String, String, String) -> Unit) {
    var name    by remember { mutableStateOf("") }
    var phone   by remember { mutableStateOf("") }
    var service by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Hire $workerName", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name,    onValueChange = { name = it },    label = { Text("Your Name") },     modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(value = phone,   onValueChange = { phone = it },   label = { Text("Your Phone") },    modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(value = service, onValueChange = { service = it }, label = { Text("Service Needed") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            }
        },
        confirmButton = {
            Button(
                onClick = { if (name.isNotBlank() && phone.isNotBlank()) onConfirm(name, phone, service) },
                colors  = ButtonDefaults.buttonColors(containerColor = SaffronOrange),
            ) { Text("Send Request") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
private fun AddReviewDialog(onDismiss: () -> Unit, onSubmit: (String, Int, String) -> Unit) {
    var name   by remember { mutableStateOf("") }
    var rating by remember { mutableIntStateOf(5) }
    var text   by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Leave a Review", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Your Name") },
                    modifier = Modifier.fillMaxWidth(), singleLine = true)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Rating: ", fontWeight = FontWeight.SemiBold)
                    repeat(5) { i ->
                        IconButton(onClick = { rating = i + 1 }, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Filled.Star, null,
                                tint = if (i < rating) WarmGold else DarkGrey.copy(alpha = 0.3f))
                        }
                    }
                }
                OutlinedTextField(value = text, onValueChange = { text = it }, label = { Text("Review (optional)") },
                    modifier = Modifier.fillMaxWidth(), maxLines = 3)
            }
        },
        confirmButton = {
            Button(
                onClick = { if (name.isNotBlank()) onSubmit(name, rating, text) },
                colors  = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
            ) { Text("Submit") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
