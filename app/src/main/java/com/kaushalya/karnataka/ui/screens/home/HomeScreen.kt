package com.kaushalya.karnataka.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kaushalya.karnataka.data.model.SkillCategories
import com.kaushalya.karnataka.ui.components.WorkerCard
import com.kaushalya.karnataka.ui.theme.*
import com.kaushalya.karnataka.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onWorkerClick : (String) -> Unit,
    onHireMe      : (String) -> Unit,
    viewModel     : HomeViewModel = hiltViewModel(),
) {
    val workers          by viewModel.workers.collectAsState()
    val query            by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val isLoading        by viewModel.isLoading.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {

        Surface(color = RoyalBlue, shadowElevation = 4.dp) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                Text("Kaushalya Karnataka", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("Find skilled workers near you", color = PureWhite.copy(alpha = 0.8f), fontSize = 12.sp)
            }
        }

        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
            OutlinedTextField(
                value         = query,
                onValueChange = { viewModel.searchQuery.value = it },
                modifier      = Modifier.fillMaxWidth(),
                placeholder   = { Text("Search workers, skills…", color = DarkGrey) },
                leadingIcon   = { Icon(Icons.Filled.Search, null, tint = DarkGrey) },
                singleLine    = true,
                shape         = RoundedCornerShape(40.dp),
                colors        = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = ElectricBlue.copy(alpha = 0.3f),
                    focusedBorderColor   = ElectricBlue,
                ),
            )
        }

        LazyRow(
            contentPadding        = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(SkillCategories.all) { cat ->
                FilterChip(
                    selected = selectedCategory == cat,
                    onClick  = { viewModel.selectedCategory.value = cat },
                    label    = { Text(cat, fontSize = 12.sp) },
                    colors   = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ElectricBlue,
                        selectedLabelColor     = PureWhite,
                    ),
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = RoyalBlue)
            }
        } else if (workers.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No workers found.\nTry a different category.", color = DarkGrey, fontSize = 14.sp)
            }
        } else {
            val label = if (selectedCategory == "All") "Nearby Workers" else selectedCategory
            Text(
                text       = "$label (${workers.size})",
                modifier   = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                style      = MaterialTheme.typography.labelSmall,
                color      = DarkGrey,
                fontSize   = 11.sp,
                fontWeight = FontWeight.SemiBold,
            )
            LazyColumn(
                contentPadding      = PaddingValues(horizontal = 16.dp, vertical = 0.dp, ),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier            = Modifier.padding(bottom = 88.dp),
            ) {
                items(workers, key = { it.workerId }) { worker ->
                    WorkerCard(
                        worker   = worker,
                        onClick  = { onWorkerClick(worker.workerId) },
                        onHireMe = { onHireMe(worker.workerId) },
                    )
                }
            }
        }
    }
}
