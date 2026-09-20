package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.DonutAchievementGauge
import com.example.ui.components.MetricCard
import com.example.ui.components.PillBadge
import com.example.ui.components.PillType
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*
import com.example.ui.viewmodel.SalesCommandViewModel

@Composable
fun V5SearchScreen(
  viewModel: SalesCommandViewModel,
  modifier: Modifier = Modifier
) {
  val searchQuery by viewModel.searchQuery.collectAsState()
  val results by viewModel.searchResults.collectAsState()

  val hasSearch = searchQuery.isNotBlank()
  val totalTarget = results.sumOf { it.targetValue }
  val totalAch = results.sumOf { it.mtdAchValue }
  val achPct = if (totalTarget > 0) totalAch / totalTarget else 0.0

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 14.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp),
    contentPadding = PaddingValues(top = 10.dp, bottom = 30.dp)
  ) {
    // 1. Search Bar Card
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        border = CardDefaults.outlinedCardBorder().copy(
          brush = Brush.horizontalGradient(listOf(AccentViolet.copy(alpha = 0.5f), AccentMint.copy(alpha = 0.3f)))
        )
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text(
            text = "🚀 V5 Advanced Intelligence",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold,
            color = TextPrimary
          )
          Text(
            text = "Global search across Promoters, Retailers, Emp IDs, TLs, CSMs, Products",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
            fontSize = 11.sp
          )

          Spacer(modifier = Modifier.height(12.dp))

          OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.setSearchQuery(it) },
            placeholder = { Text("Search anything...", color = TextTertiary, fontSize = 13.sp) },
            leadingIcon = {
              Icon(Icons.Default.Search, contentDescription = "Search", tint = AccentMint)
            },
            trailingIcon = {
              if (searchQuery.isNotEmpty()) {
                IconButton(onClick = { viewModel.setSearchQuery("") }) {
                  Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextSecondary)
                }
              }
            },
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("v5_search_input"),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedContainerColor = BackgroundDark,
              unfocusedContainerColor = BackgroundDark,
              focusedBorderColor = AccentViolet,
              unfocusedBorderColor = SurfaceBorder,
              focusedTextColor = TextPrimary,
              unfocusedTextColor = TextPrimary
            )
          )
        }
      }
    }

    if (!hasSearch) {
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = SurfaceDark),
          border = CardDefaults.outlinedCardBorder().copy(brush = Brush.verticalGradient(listOf(SurfaceBorder, SurfaceBorder)))
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text(text = "🔎", fontSize = 36.sp)
            Spacer(modifier = Modifier.height(10.dp))
            Text(
              text = "Universal Search Required",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = TextPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = "Type any promoter name, retailer store, employee ID, team lead, or phone model to inspect live intelligence.",
              style = MaterialTheme.typography.bodySmall,
              color = TextSecondary,
              fontSize = 12.sp,
              textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
          }
        }
      }
    } else {
      // KPI row for Search results
      item {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            MetricCard(
              title = "Target",
              value = viewModel.formatMoney(totalTarget),
              subtitle = "Current search scope",
              modifier = Modifier.weight(1f)
            )
            MetricCard(
              title = "Achievement",
              value = viewModel.formatMoney(totalAch),
              subtitle = "${(achPct * 100).toInt()}% Achieved",
              accentColor = AccentMint,
              modifier = Modifier.weight(1f)
            )
          }
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            MetricCard(
              title = "Matching Records",
              value = "${results.size}",
              subtitle = "${results.count { it.status.equals("Active", ignoreCase = true) }} Active",
              modifier = Modifier.weight(1f)
            )
            MetricCard(
              title = "Total Volume",
              value = "${results.sumOf { it.mtdAchVolume }}",
              subtitle = "Units Sold",
              accentColor = AccentViolet,
              modifier = Modifier.weight(1f)
            )
          }
        }
      }

      // Search Details Header
      item {
        SectionHeader(
          title = "🔎 Search Results (${results.size})",
          subtitle = "Displaying matching records"
        )
      }

      items(results) { record ->
        val pillType = when {
          record.achievementPct >= 0.80 -> PillType.GOOD
          record.achievementPct >= 0.50 -> PillType.WARN
          else -> PillType.BAD
        }

        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = SurfaceDark),
          border = CardDefaults.outlinedCardBorder().copy(brush = Brush.verticalGradient(listOf(SurfaceBorder, SurfaceBorder)))
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = record.empName,
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.Bold,
                  color = TextPrimary
                )
                Text(
                  text = "${record.empId} • ${record.retailerName}",
                  style = MaterialTheme.typography.bodySmall,
                  color = TextSecondary,
                  fontSize = 11.sp,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
              }
              PillBadge(text = "${(record.achievementPct * 100).toInt()}%", type = pillType)
            }

            Spacer(modifier = Modifier.height(10.dp))
            Divider(color = SurfaceBorder)
            Spacer(modifier = Modifier.height(10.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Column {
                Text("TL", style = MaterialTheme.typography.labelSmall, color = TextTertiary)
                Text(record.tl, style = MaterialTheme.typography.bodySmall, color = TextPrimary, fontWeight = FontWeight.SemiBold)
              }
              Column {
                Text("CSM", style = MaterialTheme.typography.labelSmall, color = TextTertiary)
                Text(record.csm, style = MaterialTheme.typography.bodySmall, color = TextPrimary, fontWeight = FontWeight.SemiBold)
              }
              Column {
                Text("ZSM", style = MaterialTheme.typography.labelSmall, color = TextTertiary)
                Text(record.zsm, style = MaterialTheme.typography.bodySmall, color = TextPrimary, fontWeight = FontWeight.SemiBold)
              }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Column {
                Text("Target", style = MaterialTheme.typography.labelSmall, color = TextTertiary)
                Text(viewModel.formatMoney(record.targetValue), style = MaterialTheme.typography.bodySmall, color = TextPrimary)
              }
              Column {
                Text("Achievement", style = MaterialTheme.typography.labelSmall, color = TextTertiary)
                Text(viewModel.formatMoney(record.mtdAchValue), style = MaterialTheme.typography.bodySmall, color = AccentMint, fontWeight = FontWeight.Bold)
              }
              Column {
                Text("Volume", style = MaterialTheme.typography.labelSmall, color = TextTertiary)
                Text("${record.mtdAchVolume} units", style = MaterialTheme.typography.bodySmall, color = TextPrimary)
              }
            }
          }
        }
      }
    }
  }
}
