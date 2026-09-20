package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PerformanceItem
import com.example.ui.components.PillBadge
import com.example.ui.components.PillType
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*
import com.example.ui.viewmodel.SalesCommandViewModel

@Composable
fun HierarchyBoardsScreen(
  viewModel: SalesCommandViewModel,
  modifier: Modifier = Modifier
) {
  val csmItems by viewModel.csmPerformance.collectAsState()
  val tlItems by viewModel.tlPerformance.collectAsState()
  val retailerItems by viewModel.retailerPerformance.collectAsState()
  val promoterItems by viewModel.promoterPerformance.collectAsState()

  var selectedTab by remember { mutableStateOf(0) } // 0: CSM, 1: TL, 2: Retailers, 3: Promoters

  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 14.dp)
  ) {
    Spacer(modifier = Modifier.height(10.dp))

    // Segmented Tab Control
    TabRow(
      selectedTabIndex = selectedTab,
      containerColor = SurfaceDark,
      contentColor = AccentMint,
      divider = { Divider(color = SurfaceBorder) }
    ) {
      Tab(
        selected = selectedTab == 0,
        onClick = { selectedTab = 0 },
        text = { Text("CSM Board", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
      )
      Tab(
        selected = selectedTab == 1,
        onClick = { selectedTab = 1 },
        text = { Text("TL Board", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
      )
      Tab(
        selected = selectedTab == 2,
        onClick = { selectedTab = 2 },
        text = { Text("Retailers", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
      )
      Tab(
        selected = selectedTab == 3,
        onClick = { selectedTab = 3 },
        text = { Text("Promoters", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
      )
    }

    Spacer(modifier = Modifier.height(12.dp))

    when (selectedTab) {
      0 -> CommandTable(
        title = "🧑‍💼 CSM Command Board",
        subtitle = "Ranked by target achievement conversion",
        items = csmItems,
        viewModel = viewModel
      )
      1 -> CommandTable(
        title = "👔 TL Command Board",
        subtitle = "Team Lead performance & Required DRR",
        items = tlItems,
        viewModel = viewModel
      )
      2 -> CommandTable(
        title = "🏪 Retailer Intelligence",
        subtitle = "Retail outlet sellout & target gaps",
        items = retailerItems,
        viewModel = viewModel
      )
      3 -> CommandTable(
        title = "👤 Promoter Ranking",
        subtitle = "Individual promoter MTD sellout & volume",
        items = promoterItems,
        viewModel = viewModel
      )
    }
  }
}

@Composable
private fun CommandTable(
  title: String,
  subtitle: String,
  items: List<PerformanceItem>,
  viewModel: SalesCommandViewModel
) {
  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.spacedBy(10.dp),
    contentPadding = PaddingValues(bottom = 30.dp)
  ) {
    item {
      SectionHeader(title = title, subtitle = subtitle)
    }

    if (items.isEmpty()) {
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
          contentAlignment = Alignment.Center
        ) {
          Text("No records in current scope", color = TextSecondary)
        }
      }
    } else {
      itemsIndexed(items) { index, item ->
        PerformanceCardItem(
          rank = index + 1,
          item = item,
          viewModel = viewModel
        )
      }
    }
  }
}

@Composable
fun PerformanceCardItem(
  rank: Int,
  item: PerformanceItem,
  viewModel: SalesCommandViewModel
) {
  val pillType = when {
    item.pct >= 0.80 -> PillType.GOOD
    item.pct >= 0.50 -> PillType.WARN
    else -> PillType.BAD
  }

  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
    border = CardDefaults.outlinedCardBorder().copy(
      brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
        listOf(
          if (rank == 1) AccentMint.copy(alpha = 0.5f) else SurfaceBorder,
          SurfaceBorder
        )
      )
    )
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.weight(1f)
        ) {
          Box(
            modifier = Modifier
              .size(26.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(if (rank <= 3) AccentViolet else SurfaceElevated),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "$rank",
              color = Color.White,
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Bold
            )
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = item.name,
              style = MaterialTheme.typography.titleSmall,
              fontWeight = FontWeight.Bold,
              color = TextPrimary,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
            if (item.secondaryInfo.isNotEmpty()) {
              Text(
                text = item.secondaryInfo,
                style = MaterialTheme.typography.bodySmall,
                color = TextTertiary,
                fontSize = 11.sp
              )
            }
          }
        }

        PillBadge(text = "${(item.pct * 100).toInt()}%", type = pillType)
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Progress Track
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
            .fillMaxWidth(fraction = item.pct.coerceIn(0.0, 1.0).toFloat())
            .clip(RoundedCornerShape(99.dp))
            .background(
              androidx.compose.ui.graphics.Brush.horizontalGradient(
                listOf(AccentViolet, AccentMint)
              )
            )
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Metrics Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Column {
          Text("Target", style = MaterialTheme.typography.labelSmall, color = TextSecondary, fontSize = 10.sp)
          Text(viewModel.formatMoney(item.target), style = MaterialTheme.typography.bodySmall, color = TextPrimary, fontWeight = FontWeight.SemiBold)
        }
        Column {
          Text("MTD Ach.", style = MaterialTheme.typography.labelSmall, color = TextSecondary, fontSize = 10.sp)
          Text(viewModel.formatMoney(item.achievement), style = MaterialTheme.typography.bodySmall, color = AccentMint, fontWeight = FontWeight.Bold)
        }
        Column {
          Text("Volume", style = MaterialTheme.typography.labelSmall, color = TextSecondary, fontSize = 10.sp)
          Text("${item.volume} units", style = MaterialTheme.typography.bodySmall, color = TextPrimary, fontWeight = FontWeight.SemiBold)
        }
        if (item.requiredDrr > 0) {
          Column {
            Text("Req. DRR", style = MaterialTheme.typography.labelSmall, color = TextSecondary, fontSize = 10.sp)
            Text(viewModel.formatMoney(item.requiredDrr), style = MaterialTheme.typography.bodySmall, color = StatusWarning, fontWeight = FontWeight.SemiBold)
          }
        }
      }
    }
  }
}
