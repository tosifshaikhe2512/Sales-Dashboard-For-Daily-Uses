package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.sample.SampleSalesData
import com.example.ui.components.PillBadge
import com.example.ui.components.PillType
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*
import com.example.ui.viewmodel.SalesCommandViewModel

@Composable
fun ProductIntelligenceScreen(
  viewModel: SalesCommandViewModel,
  modifier: Modifier = Modifier
) {
  val productTotals by viewModel.productTotals.collectAsState()
  val records by viewModel.filteredRecords.collectAsState()
  val (noteTarget, noteAch, notePct) = viewModel.noteSeriesMetrics.collectAsState().value

  val totalUnits = productTotals.values.sum()

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 14.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp),
    contentPadding = PaddingValues(top = 10.dp, bottom = 30.dp)
  ) {
    // 1. Note Series Special Card
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        border = CardDefaults.outlinedCardBorder().copy(
          brush = Brush.horizontalGradient(
            listOf(AccentViolet.copy(alpha = 0.6f), AccentMint.copy(alpha = 0.4f))
          )
        )
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "📝 Note Series Performance",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary
              )
              Text(
                text = "RN14 / RN15 / RN17 Series Focus",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
              )
            }
            PillBadge(
              text = "${(notePct * 100).toInt()}% Achieved",
              type = if (notePct >= 0.80) PillType.GOOD else PillType.WARN
            )
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Progress Bar
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(8.dp)
              .clip(RoundedCornerShape(99.dp))
              .background(Color(0xFF252D3D))
          ) {
            Box(
              modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(fraction = notePct.coerceIn(0.0, 1.0).toFloat())
                .clip(RoundedCornerShape(99.dp))
                .background(
                  Brush.horizontalGradient(listOf(AccentViolet, AccentMint))
                )
            )
          }

          Spacer(modifier = Modifier.height(12.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Column {
              Text("Note Target", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
              Text(viewModel.formatMoney(noteTarget), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = TextPrimary)
            }
            Column {
              Text("Note Achievement", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
              Text(viewModel.formatMoney(noteAch), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = AccentMint)
            }
            Column {
              Text("Target Gap", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
              Text(viewModel.formatMoney((noteTarget - noteAch).coerceAtLeast(0.0)), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = StatusWarning)
            }
          }
        }
      }
    }

    // 2. All Models MTD SO (Leaderboard & Mix)
    item {
      SectionHeader(
        title = "📱 All Models MTD SO Intelligence",
        subtitle = "Unit sellout and product mix %"
      )
    }

    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.verticalGradient(listOf(SurfaceBorder, SurfaceBorder)))
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          productTotals.forEach { (model, units) ->
            val mixPct = if (totalUnits > 0) units.toDouble() / totalUnits else 0.0
            Column(modifier = Modifier.padding(vertical = 6.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = model,
                  style = MaterialTheme.typography.bodyMedium,
                  color = TextPrimary,
                  fontWeight = FontWeight.SemiBold
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                  Text(
                    text = "$units units",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AccentMint,
                    fontWeight = FontWeight.Bold
                  )
                  Text(
                    text = "(${(mixPct * 100).toInt()}%)",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                  )
                }
              }
              Spacer(modifier = Modifier.height(4.dp))
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .height(6.dp)
                  .clip(RoundedCornerShape(99.dp))
                  .background(Color(0xFF252D3D))
              ) {
                Box(
                  modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(fraction = mixPct.coerceIn(0.0, 1.0).toFloat())
                    .clip(RoundedCornerShape(99.dp))
                    .background(
                      Brush.horizontalGradient(listOf(AccentViolet, AccentMint))
                    )
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(8.dp))
          Divider(color = SurfaceBorder)
          Spacer(modifier = Modifier.height(8.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("Total Sellout Units", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text("$totalUnits Units", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.ExtraBold, color = AccentMint)
          }
        }
      }
    }

    // 3. Product Sellout Matrix (Horizontal Scrollable Table)
    item {
      SectionHeader(
        title = "📊 Product Sellout Matrix",
        subtitle = "Store / Promoter × Model Matrix"
      )
    }

    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.verticalGradient(listOf(SurfaceBorder, SurfaceBorder)))
      ) {
        val scrollState = rememberScrollState()
        Column(
          modifier = Modifier
            .padding(12.dp)
            .horizontalScroll(scrollState)
        ) {
          // Table Header
          Row(
            modifier = Modifier
              .background(SurfaceElevated, RoundedCornerShape(8.dp))
              .padding(vertical = 8.dp, horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("Promoter / Store", modifier = Modifier.width(160.dp), color = TextSecondary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            SampleSalesData.productsList.forEach { p ->
              Text(p, modifier = Modifier.width(90.dp), color = TextSecondary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
            Text("Total Units", modifier = Modifier.width(90.dp), color = AccentMint, fontWeight = FontWeight.Bold, fontSize = 12.sp)
          }

          Spacer(modifier = Modifier.height(6.dp))

          // Table Rows
          records.forEach { r ->
            Row(
              modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column(modifier = Modifier.width(160.dp)) {
                Text(
                  text = r.empName,
                  color = TextPrimary,
                  fontWeight = FontWeight.SemiBold,
                  fontSize = 12.sp,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
                Text(
                  text = r.retailerName,
                  color = TextTertiary,
                  fontSize = 10.sp,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
              }

              SampleSalesData.productsList.forEach { p ->
                val count = r.productSales[p] ?: 0
                Text(
                  text = if (count > 0) "$count" else "-",
                  modifier = Modifier.width(90.dp),
                  color = if (count > 0) TextPrimary else TextTertiary,
                  fontSize = 12.sp
                )
              }

              Text(
                text = "${r.mtdAchVolume}",
                modifier = Modifier.width(90.dp),
                color = AccentMint,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
              )
            }
            Divider(color = SurfaceBorder.copy(alpha = 0.5f))
          }
        }
      }
    }
  }
}
