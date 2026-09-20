package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.PromoterRecord
import com.example.ui.theme.*
import com.example.ui.viewmodel.SalesCommandViewModel

@Composable
fun ActionDetailModal(
  actionKey: String,
  records: List<PromoterRecord>,
  viewModel: SalesCommandViewModel,
  onDismiss: () -> Unit
) {
  val (title, subtitle, filteredList) = when (actionKey) {
    "low30" -> Triple(
      "🔴 Attention Required (<30%)",
      "Promoters below 30% achievement needing immediate activation",
      records.filter { it.achievementPct < 0.30 }.sortedBy { it.achievementPct }
    )
    "zeroVolume" -> Triple(
      "📦 Zero Volume Points",
      "Points with zero recorded sellout units this MTD",
      records.filter { it.mtdAchVolume == 0 }
    )
    "leader" -> Triple(
      "🏆 Current Leader Spotlight",
      "Top performers by target achievement %",
      records.sortedByDescending { it.achievementPct }.take(10)
    )
    "focusGap" -> Triple(
      "🎯 Focus Gap Breakdown",
      "Points with largest remaining target deficit",
      records.sortedByDescending { it.targetGap }.take(10)
    )
    else -> Triple("Action Details", "", emptyList())
  }

  Dialog(onDismissRequest = onDismiss) {
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight(0.82f),
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = SurfaceDark)
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(16.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = title,
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = TextPrimary
            )
            if (subtitle.isNotEmpty()) {
              Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                fontSize = 11.sp
              )
            }
          }
          IconButton(onClick = onDismiss) {
            Text("✕", color = TextSecondary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
          }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Divider(color = SurfaceBorder)
        Spacer(modifier = Modifier.height(10.dp))

        if (filteredList.isEmpty()) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .weight(1f),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "🟢 No records found under this action filter.",
              color = TextSecondary,
              style = MaterialTheme.typography.bodyMedium
            )
          }
        } else {
          LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            items(filteredList) { r ->
              val pillType = when {
                r.achievementPct >= 0.80 -> PillType.GOOD
                r.achievementPct >= 0.50 -> PillType.WARN
                else -> PillType.BAD
              }

              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(12.dp))
                  .background(SurfaceElevated)
                  .padding(12.dp)
              ) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Column(modifier = Modifier.weight(1f)) {
                    Text(
                      text = r.empName,
                      color = TextPrimary,
                      fontWeight = FontWeight.Bold,
                      style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                      text = "${r.retailerName} (${r.tl})",
                      color = TextSecondary,
                      fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                      text = "Target: ${viewModel.formatMoney(r.targetValue)} • Ach: ${viewModel.formatMoney(r.mtdAchValue)} • ${r.mtdAchVolume} units",
                      color = TextTertiary,
                      fontSize = 10.sp
                    )
                  }
                  PillBadge(text = "${(r.achievementPct * 100).toInt()}%", type = pillType)
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))
        Button(
          onClick = onDismiss,
          modifier = Modifier.fillMaxWidth(),
          colors = ButtonDefaults.buttonColors(containerColor = AccentViolet),
          shape = RoundedCornerShape(12.dp)
        ) {
          Text("Close", color = Color.White)
        }
      }
    }
  }
}
