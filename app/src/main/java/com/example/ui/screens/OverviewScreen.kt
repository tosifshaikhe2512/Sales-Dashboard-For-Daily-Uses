package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.HierarchyLevel
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.SalesCommandViewModel

@Composable
fun OverviewScreen(
  viewModel: SalesCommandViewModel,
  onShowActionDetail: (actionKey: String) -> Unit,
  modifier: Modifier = Modifier
) {
  val filter by viewModel.scopeFilter.collectAsState()
  val drr by viewModel.drrMetrics.collectAsState()
  val records by viewModel.filteredRecords.collectAsState()
  val club80 by viewModel.performance80Club.collectAsState()
  val (noteTarget, noteAch, notePct) = viewModel.noteSeriesMetrics.collectAsState().value

  val availableZsm by viewModel.availableZsM.collectAsState()
  val availableCsm by viewModel.availableCsm.collectAsState()
  val availableTl by viewModel.availableTl.collectAsState()
  val availableParentCodes by viewModel.availableParentCodes.collectAsState()

  val achPct = if (drr.totalTarget > 0) drr.totalAchievement / drr.totalTarget else 0.0
  val statusPillType = when {
    achPct >= 0.80 -> PillType.GOOD
    achPct >= 0.50 -> PillType.WARN
    else -> PillType.BAD
  }
  val statusText = when {
    achPct >= 0.80 -> "ON TRACK"
    achPct >= 0.50 -> "AT RISK"
    else -> "CRITICAL"
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 14.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp),
    contentPadding = PaddingValues(top = 10.dp, bottom = 28.dp)
  ) {
    // 1. Hierarchy Level Tabs
    item {
      HierarchySelectorRow(
        activeLevel = filter.level,
        onSelectLevel = { viewModel.setHierarchyLevel(it) }
      )
    }

    // 2. Active Scope Dropdowns
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        when (filter.level) {
          HierarchyLevel.ZSM -> {
            FilterDropdown(
              label = "ZSM",
              options = availableZsm,
              selectedValue = filter.selectedZsm,
              onValueChange = { viewModel.setZsmFilter(it) },
              modifier = Modifier.weight(1f)
            )
          }
          HierarchyLevel.CSM -> {
            FilterDropdown(
              label = "CSM",
              options = availableCsm,
              selectedValue = filter.selectedCsm,
              onValueChange = { viewModel.setCsmFilter(it) },
              modifier = Modifier.weight(1f)
            )
          }
          HierarchyLevel.TL -> {
            FilterDropdown(
              label = "TL",
              options = availableTl,
              selectedValue = filter.selectedTl,
              onValueChange = { viewModel.setTlFilter(it) },
              modifier = Modifier.weight(1f)
            )
          }
          HierarchyLevel.PARENT_CODE -> {
            FilterDropdown(
              label = "Parent Code",
              options = availableParentCodes,
              selectedValue = filter.selectedParentCode,
              onValueChange = { viewModel.setParentCodeFilter(it) },
              modifier = Modifier.weight(1f)
            )
          }
          HierarchyLevel.PROMOTER -> {
            FilterDropdown(
              label = "TL",
              options = availableTl,
              selectedValue = filter.selectedTl,
              onValueChange = { viewModel.setTlFilter(it) },
              modifier = Modifier.weight(1f)
            )
          }
        }

        IconButton(
          onClick = { viewModel.resetFilters() },
          modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceElevated)
        ) {
          Icon(Icons.Default.Refresh, contentDescription = "Reset filter", tint = AccentMint)
        }
      }
    }

    // 3. Executive Overview Hero Card
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        border = CardDefaults.outlinedCardBorder().copy(
          brush = Brush.linearGradient(
            listOf(AccentViolet.copy(alpha = 0.5f), AccentMint.copy(alpha = 0.3f), SurfaceBorder)
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
                text = "📊 Executive Overview",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary
              )
              Text(
                text = "${filter.level.title} • ${drr.reportDateString}",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                fontSize = 11.sp
              )
            }
            PillBadge(text = statusText, type = statusPillType)
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Donut Gauge and Key Performance
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            DonutAchievementGauge(
              percentage = achPct.toFloat(),
              sizeDp = 120,
              strokeWidthDp = 12f
            ) {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                  text = "${(achPct * 100).toInt()}%",
                  style = MaterialTheme.typography.headlineSmall,
                  fontWeight = FontWeight.Black,
                  color = TextPrimary
                )
                Text(
                  text = "Achieved",
                  style = MaterialTheme.typography.labelSmall,
                  color = TextSecondary,
                  fontSize = 10.sp
                )
              }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
              modifier = Modifier.weight(1f),
              verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              ExecutiveStatRow(label = "🎯 Target", value = viewModel.formatMoney(drr.totalTarget))
              ExecutiveStatRow(label = "💰 MTD Sellout", value = viewModel.formatMoney(drr.totalAchievement), valueColor = AccentMint)
              ExecutiveStatRow(label = "📦 Volume", value = "${drr.totalVolume} Units")
              ExecutiveStatRow(label = "⚡ Required DRR", value = "${viewModel.formatMoney(drr.requiredDRR)}/day", valueColor = if (drr.paceStatus) AccentMint else StatusDanger)
            }
          }

          Spacer(modifier = Modifier.height(14.dp))
          Divider(color = SurfaceBorder)
          Spacer(modifier = Modifier.height(10.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = "Target Gap: ${viewModel.formatMoney(drr.totalGap)}",
              style = MaterialTheme.typography.labelSmall,
              color = StatusWarning,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "Current DRR: ${viewModel.formatMoney(drr.currentDRR)}/day",
              style = MaterialTheme.typography.labelSmall,
              color = TextSecondary
            )
          }
        }
      }
    }

    // 4. 80%+ Performance Tracker Card
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        border = CardDefaults.outlinedCardBorder().copy(
          brush = Brush.horizontalGradient(listOf(AccentMint.copy(alpha = 0.4f), SurfaceBorder))
        )
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          SectionHeader(
            title = "🏅 80%+ Performance Tracker",
            subtitle = "Promoters at or above target benchmark"
          )

          Spacer(modifier = Modifier.height(10.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            ClubStatCard(
              title = "🎯 Overall 80%+",
              value = "${club80.first} / ${club80.third}",
              subtitle = "Promoters qualifying",
              modifier = Modifier.weight(1f)
            )
            ClubStatCard(
              title = "📱 Note Series 80%+",
              value = "${club80.second} / ${club80.third}",
              subtitle = "RN14 / 15 / 17 qualifying",
              modifier = Modifier.weight(1f)
            )
          }
        }
      }
    }

    // 5. Grid of Metrics
    item {
      SectionHeader(title = "📈 MTD Command Board")
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          MetricCard(
            title = "Total Target",
            value = viewModel.formatMoney(drr.totalTarget),
            subtitle = "Overall Plan",
            modifier = Modifier.weight(1f)
          )
          MetricCard(
            title = "MTD Achievement",
            value = viewModel.formatMoney(drr.totalAchievement),
            subtitle = "${(achPct * 100).toInt()}% Converted",
            accentColor = AccentMint,
            modifier = Modifier.weight(1f)
          )
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          MetricCard(
            title = "Total Volume",
            value = "${drr.totalVolume}",
            subtitle = "Units Sold",
            secondaryMetric = "80%+ Club" to "${club80.first} / ${club80.third}",
            modifier = Modifier.weight(1f)
          )
          MetricCard(
            title = "Note Series",
            value = "${(notePct * 100).toInt()}%",
            subtitle = viewModel.formatMoney(noteAch),
            secondaryMetric = "Note 80%+" to "${club80.second} / ${club80.third}",
            accentColor = AccentViolet,
            modifier = Modifier.weight(1f)
          )
        }
      }
    }

    // 6. DRR Intelligence & Projections
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        border = CardDefaults.outlinedCardBorder().copy(
          brush = Brush.verticalGradient(listOf(SurfaceBorder, SurfaceBorder))
        )
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          SectionHeader(
            title = "⚡ Daily Run-Rate (DRR) Intelligence",
            subtitle = "${drr.daysPassed} days passed • ${drr.daysRemaining} days left"
          )

          Spacer(modifier = Modifier.height(10.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            MetricCard(
              title = "Current DRR",
              value = viewModel.formatMoney(drr.currentDRR),
              subtitle = "Achieved per day",
              modifier = Modifier.weight(1f)
            )
            MetricCard(
              title = "Required DRR",
              value = viewModel.formatMoney(drr.requiredDRR),
              subtitle = if (drr.paceStatus) "🟢 Pace Ahead" else "🔴 Deficit Pace",
              accentColor = if (drr.paceStatus) AccentMint else StatusDanger,
              modifier = Modifier.weight(1f)
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(10.dp))
              .background(SurfaceElevated)
              .padding(12.dp)
          ) {
            Column {
              Text(
                text = "🔮 Month-End Projection: ${viewModel.formatMoney(drr.projectedAchievement)}",
                style = MaterialTheme.typography.titleSmall,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
              )
              Spacer(modifier = Modifier.height(3.dp))
              Text(
                text = if (drr.projectedAchievement >= drr.totalTarget) {
                  "🟢 Target is achievable at current pace (+${viewModel.formatMoney(drr.projectedAchievement - drr.totalTarget)} surplus)"
                } else {
                  "🔴 Shortfall of ${viewModel.formatMoney(drr.totalTarget - drr.projectedAchievement)}. Required daily run-rate needs +${viewModel.formatMoney(drr.requiredDRR - drr.currentDRR)} boost."
                },
                style = MaterialTheme.typography.bodySmall,
                color = if (drr.projectedAchievement >= drr.totalTarget) AccentMint else StatusWarning,
                fontSize = 12.sp
              )
            }
          }
        }
      }
    }

    // 7. Action Center (Tap to view details)
    item {
      SectionHeader(
        title = "⚡ Action Center",
        subtitle = "Auto-detected • Tap card to view details"
      )

      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        val lowPerformers = records.count { it.achievementPct < 0.30 }
        val zeroVolume = records.count { it.mtdAchVolume == 0 }

        ActionAlertItem(
          title = "🔴 Attention Required (<30%)",
          description = "$lowPerformers team members require immediate sales intervention",
          pillText = "$lowPerformers Promoters",
          pillType = PillType.BAD,
          onClick = { onShowActionDetail("low30") }
        )

        ActionAlertItem(
          title = "📦 Zero Volume Alerts",
          description = "$zeroVolume points have recorded 0 units sold this MTD",
          pillText = "$zeroVolume Stores",
          pillType = PillType.WARN,
          onClick = { onShowActionDetail("zeroVolume") }
        )

        ActionAlertItem(
          title = "🏆 Current Leader Spotlight",
          description = "View the top performers dominating the current leaderboard",
          pillText = "Top 10",
          pillType = PillType.GOOD,
          onClick = { onShowActionDetail("leader") }
        )

        ActionAlertItem(
          title = "🎯 Target Gap Breakdown",
          description = "Stores and promoters with the largest absolute gap to target",
          pillText = "Largest Gaps",
          pillType = PillType.WARN,
          onClick = { onShowActionDetail("focusGap") }
        )
      }
    }

    // 8. Hierarchy Counts
    item {
      SectionHeader(title = "👥 Management Coverage")
      val activePromoters = records.count { it.status.equals("Active", ignoreCase = true) }
      val totalPromoters = records.size
      val totalRetailers = records.map { it.retailerName }.distinct().size

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        MetricCard(
          title = "Active Promoters",
          value = "$activePromoters",
          subtitle = "Total: $totalPromoters (${if (totalPromoters > 0) (activePromoters * 100) / totalPromoters else 0}%)",
          accentColor = AccentMint,
          modifier = Modifier.weight(1f)
        )
        MetricCard(
          title = "Retailers",
          value = "$totalRetailers",
          subtitle = "Active Outlets",
          modifier = Modifier.weight(1f)
        )
      }
    }
  }
}

@Composable
private fun ExecutiveStatRow(
  label: String,
  value: String,
  valueColor: Color = TextPrimary
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = label,
      style = MaterialTheme.typography.bodySmall,
      color = TextSecondary,
      fontSize = 12.sp
    )
    Text(
      text = value,
      style = MaterialTheme.typography.bodyMedium,
      color = valueColor,
      fontWeight = FontWeight.Bold
    )
  }
}

@Composable
private fun ClubStatCard(
  title: String,
  value: String,
  subtitle: String,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(12.dp))
      .background(SurfaceElevated)
      .border(1.dp, SurfaceBorder, RoundedCornerShape(12.dp))
      .padding(12.dp)
  ) {
    Column {
      Text(
        text = title,
        style = MaterialTheme.typography.labelSmall,
        color = TextSecondary,
        fontWeight = FontWeight.SemiBold,
        fontSize = 11.sp
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = value,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Black,
        color = AccentMint
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = subtitle,
        style = MaterialTheme.typography.bodySmall,
        color = TextTertiary,
        fontSize = 10.sp
      )
    }
  }
}
