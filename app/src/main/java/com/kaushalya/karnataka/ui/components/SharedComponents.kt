package com.kaushalya.karnataka.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaushalya.karnataka.data.model.ServiceCard
import com.kaushalya.karnataka.data.model.Worker
import com.kaushalya.karnataka.ui.theme.*

@Composable
fun StarRow(rating: Float, maxStars: Int = 5, size: Dp = 16.dp) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        repeat(maxStars) { i ->
            Icon(
                imageVector = if (i < rating.toInt()) Icons.Filled.Star else Icons.Outlined.StarOutline,
                contentDescription = null,
                tint     = WarmGold,
                modifier = Modifier.size(size),
            )
        }
        Spacer(Modifier.width(4.dp))
        Text(
            text     = String.format("%.1f", rating),
            style    = MaterialTheme.typography.labelSmall,
            color    = DarkGrey,
            fontSize = 11.sp,
        )
    }
}

@Composable
fun AvailabilityDot(available: Boolean, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(9.dp)
            .clip(CircleShape)
            .background(if (available) ForestGreen else DarkGrey)
    )
}

@Composable
fun WorkerCard(
    worker   : Worker,
    onClick  : () -> Unit,
    onHireMe : () -> Unit,
    modifier : Modifier = Modifier,
) {
    Card(
        onClick   = onClick,
        modifier  = modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors    = CardDefaults.cardColors(containerColor = PureWhite),
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(RoyalBlue.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text       = worker.fullName.take(1).uppercase(),
                    style      = MaterialTheme.typography.titleLarge,
                    color      = RoyalBlue,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 22.sp,
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(worker.fullName,       style = MaterialTheme.typography.titleMedium, maxLines = 1)
                Text(worker.skillCategory, style = MaterialTheme.typography.bodyMedium,  color = DarkGrey)
                Spacer(Modifier.height(4.dp))
                Row(
                    verticalAlignment      = Alignment.CenterVertically,
                    horizontalArrangement  = Arrangement.spacedBy(6.dp),
                ) {
                    AvailabilityDot(worker.isAvailable)
                    StarRow(worker.avgRating, size = 13.dp)
                    Text(worker.locationText, style = MaterialTheme.typography.labelSmall, color = DarkGrey, fontSize = 11.sp)
                }
            }
            Spacer(Modifier.width(8.dp))
            // Hire Me opens the profile screen — same as tapping the card
            Button(
                onClick        = onClick,   // navigate to profile, hire from there
                shape          = RoundedCornerShape(20.dp),
                colors         = ButtonDefaults.buttonColors(containerColor = SaffronOrange),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier       = Modifier.height(36.dp),
            ) {
                Text("Hire Me", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ServiceCardItem(card: ServiceCard, modifier: Modifier = Modifier) {
    Card(
        modifier  = modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors    = CardDefaults.cardColors(containerColor = PureWhite),
    ) {
        Row(
            modifier              = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.Top,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(card.title,       style = MaterialTheme.typography.titleMedium)
                if (card.description.isNotBlank())
                    Text(card.description, style = MaterialTheme.typography.bodyMedium, color = DarkGrey)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text       = "₹${card.price.toInt()}",
                    style      = MaterialTheme.typography.titleLarge,
                    color      = SaffronOrange,
                    fontWeight = FontWeight.Bold,
                )
                Text(card.priceType, style = MaterialTheme.typography.labelSmall, color = DarkGrey)
            }
        }
    }
}

@Composable
fun StatusBadge(status: String) {
    val (bg, fg) = when (status) {
        "Accepted"  -> androidx.compose.ui.graphics.Color(0xFFE8F5E9) to ForestGreen
        "Completed" -> androidx.compose.ui.graphics.Color(0xFFE3F2FD) to androidx.compose.ui.graphics.Color(0xFF1565C0)
        else        -> androidx.compose.ui.graphics.Color(0xFFFFF3E0) to androidx.compose.ui.graphics.Color(0xFFE65100)
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(bg)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(status, color = fg, fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
    }
}
