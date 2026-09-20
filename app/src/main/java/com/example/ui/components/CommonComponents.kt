package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.HierarchyLevel
import com.example.ui.theme.*

@Composable
fun MetricCard(
  title: String,
  value: String,
  subtitle: String,
  modifier: Modifier = Modifier,
  accentColor: Color = AccentMint,
  secondaryMetric: Pair<String, String>? = null,
  testTag: String = "metric_card"
) {
  Card(
    modifier = modifier
      .testTag(testTag)
      .fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
    border = CardDefaults.outlinedCardBorder().copy(
      brush = Brush.verticalGradient(
        listOf(SurfaceBorder, SurfaceDark)
      )
    )
  ) {
    Column(
      modifier = Modifier.padding(14.dp)
    ) {
      Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = TextSecondary,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.8.sp,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = value,
        style = MaterialTheme.typography.headlineSmall,
        color = TextPrimary,
        fontWeight = FontWeight.ExtraBold,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = subtitle,
        style = MaterialTheme.typography.bodySmall,
        color = accentColor,
        fontWeight = FontWeight.SemiBold,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )

      if (secondaryMetric != null) {
        Spacer(modifier = Modifier.height(8.dp))
        Divider(color = SurfaceBorder, thickness = 1.dp)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = secondaryMetric.first,
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary,
            fontSize = 11.sp
          )
          Text(
            text = secondaryMetric.second,
            style = MaterialTheme.typography.labelMedium,
            color = AccentMint,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }
  }
}

@Composable
fun SectionHeader(
  title: String,
  subtitle: String? = null,
  modifier: Modifier = Modifier,
  trailing: @Composable (() -> Unit)? = null
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(vertical = 6.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = TextPrimary,
        fontWeight = FontWeight.Bold
      )
      if (subtitle != null) {
        Text(
          text = subtitle,
          style = MaterialTheme.typography.bodySmall,
          color = TextSecondary,
          fontSize = 12.sp
        )
      }
    }
    if (trailing != null) {
      trailing()
    }
  }
}

@Composable
fun DonutAchievementGauge(
  percentage: Float, // 0.0 to 1.0+
  modifier: Modifier = Modifier,
  sizeDp: Int = 130,
  strokeWidthDp: Float = 14f,
  primaryColor: Color = AccentMint,
  trackColor: Color = Color(0xFF252D3D),
  centerContent: @Composable () -> Unit
) {
  val animatedProgress by animateFloatAsState(
    targetValue = percentage.coerceIn(0f, 1.5f),
    animationSpec = tween(durationMillis = 800),
    label = "gaugeProgress"
  )

  Box(
    modifier = modifier.size(sizeDp.dp),
    contentAlignment = Alignment.Center
  ) {
    Canvas(modifier = Modifier.fillMaxSize()) {
      val strokePx = strokeWidthDp.dp.toPx()
      val radius = (size.minDimension - strokePx) / 2
      val center = Offset(size.width / 2, size.height / 2)

      // Background track
      drawCircle(
        color = trackColor,
        radius = radius,
        center = center,
        style = Stroke(width = strokePx)
      )

      // Progress arc
      val sweepAngle = (animatedProgress.coerceAtMost(1.0f) * 360f)
      drawArc(
        brush = Brush.sweepGradient(
          listOf(AccentViolet, AccentMint, AccentMintLight)
        ),
        startAngle = -90f,
        sweepAngle = sweepAngle,
        useCenter = false,
        topLeft = Offset(center.x - radius, center.y - radius),
        size = Size(radius * 2, radius * 2),
        style = Stroke(width = strokePx, cap = StrokeCap.Round)
      )
    }

    centerContent()
  }
}

@Composable
fun PillBadge(
  text: String,
  modifier: Modifier = Modifier,
  type: PillType = PillType.NEUTRAL
) {
  val (bgColor, textColor) = when (type) {
    PillType.GOOD -> ChipSuccessBg to StatusSuccess
    PillType.WARN -> ChipWarningBg to StatusWarning
    PillType.BAD -> ChipDangerBg to StatusDanger
    PillType.NEUTRAL -> SurfaceElevated to TextSecondary
  }

  Box(
    modifier = modifier
      .clip(RoundedCornerShape(999.dp))
      .background(bgColor)
      .padding(horizontal = 8.dp, vertical = 4.dp),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = text,
      color = textColor,
      style = MaterialTheme.typography.labelSmall,
      fontWeight = FontWeight.Bold,
      fontSize = 11.sp
    )
  }
}

enum class PillType { GOOD, WARN, BAD, NEUTRAL }

@Composable
fun HierarchySelectorRow(
  activeLevel: HierarchyLevel,
  onSelectLevel: (HierarchyLevel) -> Unit,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp),
    horizontalArrangement = Arrangement.spacedBy(6.dp)
  ) {
    HierarchyLevel.values().forEach { level ->
      val isSelected = level == activeLevel
      val bgGradient = if (isSelected) {
        Brush.horizontalGradient(listOf(AccentViolet, AccentBlue))
      } else {
        Brush.horizontalGradient(listOf(SurfaceDark, SurfaceDark))
      }

      Box(
        modifier = Modifier
          .weight(1f)
          .clip(RoundedCornerShape(12.dp))
          .background(bgGradient)
          .border(
            width = 1.dp,
            color = if (isSelected) Color.Transparent else SurfaceBorder,
            shape = RoundedCornerShape(12.dp)
          )
          .clickable { onSelectLevel(level) }
          .padding(vertical = 10.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(text = level.icon, fontSize = 16.sp)
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = level.title,
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) Color.White else TextSecondary,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterDropdown(
  label: String,
  options: List<String>,
  selectedValue: String,
  onValueChange: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  var expanded by remember { mutableStateOf(false) }

  ExposedDropdownMenuBox(
    expanded = expanded,
    onExpandedChange = { expanded = !expanded },
    modifier = modifier
  ) {
    OutlinedTextField(
      value = if (selectedValue.isEmpty()) "All $label" else selectedValue,
      onValueChange = {},
      readOnly = true,
      label = { Text(label, color = TextSecondary, fontSize = 11.sp) },
      trailingIcon = {
        Icon(Icons.Default.ArrowDropDown, contentDescription = "Open dropdown", tint = TextSecondary)
      },
      colors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = SurfaceDark,
        unfocusedContainerColor = SurfaceDark,
        focusedBorderColor = AccentViolet,
        unfocusedBorderColor = SurfaceBorder,
        focusedTextColor = TextPrimary,
        unfocusedTextColor = TextPrimary
      ),
      shape = RoundedCornerShape(12.dp),
      modifier = Modifier
        .menuAnchor()
        .fillMaxWidth()
    )

    ExposedDropdownMenu(
      expanded = expanded,
      onDismissRequest = { expanded = false },
      modifier = Modifier.background(SurfaceElevated)
    ) {
      DropdownMenuItem(
        text = { Text("All $label", color = TextPrimary) },
        onClick = {
          onValueChange("")
          expanded = false
        }
      )
      options.forEach { item ->
        DropdownMenuItem(
          text = { Text(item, color = TextPrimary) },
          onClick = {
            onValueChange(item)
            expanded = false
          }
        )
      }
    }
  }
}

@Composable
fun ActionAlertItem(
  title: String,
  description: String,
  pillText: String,
  pillType: PillType,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .clickable { onClick() }
      .border(1.dp, SurfaceBorder, RoundedCornerShape(12.dp)),
    colors = CardDefaults.cardColors(containerColor = SurfaceDark)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = title,
          style = MaterialTheme.typography.titleSmall,
          color = TextPrimary,
          fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
          text = description,
          style = MaterialTheme.typography.bodySmall,
          color = TextSecondary,
          fontSize = 12.sp
        )
      }
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        PillBadge(text = pillText, type = pillType)
        Icon(Icons.Default.ChevronRight, contentDescription = "Detail", tint = TextTertiary)
      }
    }
  }
}
