package com.kaushalya.karnataka.ui.screens.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kaushalya.karnataka.data.model.HireRequest
import com.kaushalya.karnataka.ui.components.StatusBadge
import com.kaushalya.karnataka.ui.theme.*
import com.kaushalya.karnataka.viewmodel.DashboardViewModel

@Composable
fun DashboardScreen(viewModel: DashboardViewModel = hiltViewModel()) {
    val worker        by viewModel.worker.collectAsState()
    val hireRequests  by viewModel.hireRequests.collectAsState()
    val profileViews  by viewModel.profileViews.collectAsState()

    LazyColumn(
        modifier       = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 88.dp),
    ) {
        // ── Top bar ───────────────────────────────────────────────────
        item {
            Surface(color = RoyalBlue, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("My Dashboard", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("Welcome back, ${worker?.fullName ?: "…"}", color = PureWhite.copy(0.8f), fontSize = 12.sp)
                }
            }
        }

        // ── Availability toggle ───────────────────────────────────────
        item {
            worker?.let { w ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    shape    = RoundedCornerShape(14.dp),
                    colors   = CardDefaults.cardColors(PureWhite),
                    elevation = CardDefaults.cardElevation(2.dp),
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment     = Alignment.CenterVertically,
                    ) {
                        Column {
                            Text("Availability", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                            Text(
                                if (w.isAvailable) "You are visible to customers" else "Hidden from search results",
                                color = DarkGrey, fontSize = 12.sp,
                            )
                        }
                        Switch(
                            checked  = w.isAvailable,
                            onCheckedChange = { viewModel.toggleAvailability(it) },
                            colors   = SwitchDefaults.colors(checkedThumbColor = PureWhite, checkedTrackColor = ForestGreen),
                        )
                    }
                }
            }
        }

        // ── Stats row ─────────────────────────────────────────────────
        item {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                StatCard("👁️", profileViews.toString(),          "Profile Views", Modifier.weight(1f))
                StatCard("📲", hireRequests.size.toString(),     "Hire Requests", Modifier.weight(1f))
                StatCard("⭐", String.format("%.1f", worker?.avgRating ?: 0f), "Avg Rating",    Modifier.weight(1f))
            }
        }

        // ── Hire Requests ─────────────────────────────────────────────
        item {
            Text(
                "Recent Hire Requests",
                modifier = Modifier.padding(start = 16.dp, top = 20.dp, bottom = 8.dp),
                fontWeight = FontWeight.Bold,
                fontSize   = 15.sp,
                color      = NearBlack,
            )
        }

        if (hireRequests.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                    Text("No hire requests yet.", color = DarkGrey, fontSize = 14.sp)
                }
            }
        } else {
            items(hireRequests) { req ->
                HireRequestCard(req, onStatusChange = { viewModel.updateRequestStatus(req.requestId, it) })
            }
        }
    }
}

@Composable
private fun StatCard(emoji: String, value: String, label: String, modifier: Modifier = Modifier) {
    Card(
        modifier  = modifier,
        shape     = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors    = CardDefaults.cardColors(PureWhite),
    ) {
        Column(
            modifier              = Modifier.padding(14.dp).fillMaxWidth(),
            horizontalAlignment   = Alignment.CenterHorizontally,
        ) {
            Text(emoji, fontSize = 22.sp)
            Text(value, fontWeight = FontWeight.Bold, fontSize = 24.sp, color = RoyalBlue)
            Text(label, fontSize = 10.sp, color = DarkGrey, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun HireRequestCard(req: HireRequest, onStatusChange: (String) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 5.dp),
        shape    = RoundedCornerShape(12.dp),
        colors   = CardDefaults.cardColors(PureWhite),
        elevation = CardDefaults.cardElevation(2.dp),
    ) {
        Row(
            modifier              = Modifier.padding(14.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(req.customerName,  fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Text(req.serviceNeeded, color = DarkGrey, fontSize = 12.sp)
                Text(req.customerPhone, color = ElectricBlue, fontSize = 12.sp)
            }
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                StatusBadge(req.status)
                if (req.status == "Pending") {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        OutlinedButton(
                            onClick = { onStatusChange("Accepted") },
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(28.dp),
                        ) { Text("Accept", fontSize = 10.sp) }
                    }
                }
            }
        }
    }
}
