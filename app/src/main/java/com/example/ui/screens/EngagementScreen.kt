package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TeamKudos
import com.example.ui.components.PillBadge
import com.example.ui.components.PillType
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*
import com.example.ui.viewmodel.SalesCommandViewModel

@Composable
fun EngagementScreen(
  viewModel: SalesCommandViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val clipboardManager = LocalClipboardManager.current

  val drr by viewModel.drrMetrics.collectAsState()
  val simulatedPace by viewModel.simulatedTargetPace.collectAsState()
  val kudosList by viewModel.teamKudos.collectAsState()
  val club80 by viewModel.performance80Club.collectAsState()

  var showKudosDialog by remember { mutableStateOf(false) }
  var showImportDialog by remember { mutableStateOf(false) }

  // Simulated calculations
  val simulatedTarget = drr.totalTarget * simulatedPace
  val simulatedGap = (simulatedTarget - drr.totalAchievement).coerceAtLeast(0.0)
  val simulatedRequiredDrr = if (drr.daysRemaining > 0) simulatedGap / drr.daysRemaining else 0.0

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 14.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp),
    contentPadding = PaddingValues(top = 10.dp, bottom = 30.dp)
  ) {
    // 1. WhatsApp Team Broadcast Center
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        border = CardDefaults.outlinedCardBorder().copy(
          brush = Brush.horizontalGradient(
            listOf(Color(0xFF25D366).copy(alpha = 0.5f), AccentMint.copy(alpha = 0.3f))
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
                text = "📲 WhatsApp Broadcast Center",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary
              )
              Text(
                text = "Instant 1-tap leadership update generation",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
              )
            }
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF1B382B))
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Text("LIVE", color = Color(0xFF25D366), fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Preview snippet box
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .background(SurfaceElevated)
              .border(1.dp, SurfaceBorder, RoundedCornerShape(12.dp))
              .padding(12.dp)
          ) {
            Text(
              text = "🚀 *TAUSIF • SALES COMMAND BRIEFING*\n" +
                "🎯 Target: ${viewModel.formatMoney(drr.totalTarget)} • Ach: ${viewModel.formatMoney(drr.totalAchievement)}\n" +
                "⚡ Current DRR: ${viewModel.formatMoney(drr.currentDRR)}/day • Req: ${viewModel.formatMoney(drr.requiredDRR)}/day\n" +
                "🏅 80%+ Club: ${club80.first} promoters • Note 80%: ${club80.second} promoters\n" +
                "🔥 Action Today: Push daily DRR and Note series activations!",
              color = TextSecondary,
              style = MaterialTheme.typography.bodySmall,
              fontSize = 11.sp,
              lineHeight = 16.sp
            )
          }

          Spacer(modifier = Modifier.height(14.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Button(
              onClick = {
                val message = viewModel.generateWhatsAppMessage()
                shareViaWhatsApp(context, message)
              },
              modifier = Modifier
                .weight(1f)
                .height(46.dp)
                .testTag("wa_share_btn"),
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
              shape = RoundedCornerShape(12.dp)
            ) {
              Icon(Icons.Default.Share, contentDescription = "Share", tint = Color.Black, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Share WhatsApp", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }

            OutlinedButton(
              onClick = {
                val message = viewModel.generateWhatsAppMessage()
                clipboardManager.setText(AnnotatedString(message))
                Toast.makeText(context, "Briefing copied to clipboard!", Toast.LENGTH_SHORT).show()
              },
              modifier = Modifier
                .weight(1f)
                .height(46.dp)
                .testTag("wa_copy_btn"),
              colors = ButtonDefaults.outlinedButtonColors(containerColor = SurfaceElevated),
              border = ButtonDefaults.outlinedButtonBorder().copy(brush = Brush.linearGradient(listOf(SurfaceBorder, SurfaceBorder))),
              shape = RoundedCornerShape(12.dp)
            ) {
              Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = TextPrimary, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Copy Text", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
          }
        }
      }
    }

    // 2. Interactive Target Simulator & What-If Planner
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        border = CardDefaults.outlinedCardBorder().copy(
          brush = Brush.verticalGradient(listOf(AccentViolet.copy(alpha = 0.5f), SurfaceBorder))
        )
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          SectionHeader(
            title = "🎯 What-If DRR Simulator",
            subtitle = "Simulate required daily run-rate for target adjustments"
          )

          Spacer(modifier = Modifier.height(12.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Target Multiplier: ${(simulatedPace * 100).toInt()}%",
              style = MaterialTheme.typography.bodyMedium,
              fontWeight = FontWeight.Bold,
              color = AccentMint
            )
            Text(
              text = viewModel.formatMoney(simulatedTarget),
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.ExtraBold,
              color = TextPrimary
            )
          }

          Slider(
            value = simulatedPace,
            onValueChange = { viewModel.setSimulatedTargetPace(it) },
            valueRange = 0.8f..1.5f,
            steps = 14,
            colors = SliderDefaults.colors(
              thumbColor = AccentMint,
              activeTrackColor = AccentViolet,
              inactiveTrackColor = SurfaceElevated
            )
          )

          Spacer(modifier = Modifier.height(8.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(SurfaceElevated)
                .padding(10.dp)
            ) {
              Column {
                Text("Simulated Gap", style = MaterialTheme.typography.labelSmall, color = TextSecondary, fontSize = 10.sp)
                Text(viewModel.formatMoney(simulatedGap), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = StatusWarning)
              }
            }

            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(SurfaceElevated)
                .padding(10.dp)
            ) {
              Column {
                Text("Simulated Required DRR", style = MaterialTheme.typography.labelSmall, color = TextSecondary, fontSize = 10.sp)
                Text("${viewModel.formatMoney(simulatedRequiredDrr)}/day", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = AccentMint)
              }
            }
          }
        }
      }
    }

    // 3. Team Recognition & Kudos Wall
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        SectionHeader(
          title = "👏 Team Kudos & Badges",
          subtitle = "Recognize & motivate top performers",
          modifier = Modifier.weight(1f)
        )
        IconButton(
          onClick = { showKudosDialog = true },
          modifier = Modifier
            .size(38.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(AccentViolet)
        ) {
          Icon(Icons.Default.Add, contentDescription = "Give Kudos", tint = Color.White)
        }
      }
    }

    items(kudosList) { kudos ->
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
            Column {
              Text(
                text = kudos.recipientName,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
              )
              Text(
                text = kudos.role,
                style = MaterialTheme.typography.bodySmall,
                color = TextTertiary,
                fontSize = 11.sp
              )
            }
            PillBadge(text = kudos.badgeType, type = PillType.GOOD)
          }

          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "\"${kudos.achievementText}\"",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            fontSize = 12.sp
          )

          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "Awarded by ${kudos.senderName}",
            style = MaterialTheme.typography.labelSmall,
            color = AccentMint,
            fontSize = 10.sp
          )
        }
      }
    }

    // 4. Data Source & Import Controls
    item {
      SectionHeader(
        title = "📂 Data Management & Controls",
        subtitle = "Import custom CSV or restore sample workbook"
      )

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Button(
          onClick = { showImportDialog = true },
          modifier = Modifier
            .weight(1f)
            .height(46.dp),
          colors = ButtonDefaults.buttonColors(containerColor = SurfaceElevated),
          shape = RoundedCornerShape(12.dp)
        ) {
          Text("📥 Import CSV", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }

        OutlinedButton(
          onClick = {
            viewModel.resetToSampleData()
            Toast.makeText(context, "Restored authentic sample sales data!", Toast.LENGTH_SHORT).show()
          },
          modifier = Modifier
            .weight(1f)
            .height(46.dp),
          colors = ButtonDefaults.outlinedButtonColors(containerColor = SurfaceDark),
          border = ButtonDefaults.outlinedButtonBorder().copy(brush = Brush.linearGradient(listOf(SurfaceBorder, SurfaceBorder))),
          shape = RoundedCornerShape(12.dp)
        ) {
          Icon(Icons.Default.Refresh, contentDescription = "Reset", tint = AccentMint, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Reset Sample", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
      }
    }
  }

  // Dialog: Send Kudos
  if (showKudosDialog) {
    KudosDialog(
      onDismiss = { showKudosDialog = false },
      onSend = { name, role, note, badge ->
        viewModel.addKudos(name, role, note, badge)
        showKudosDialog = false
        Toast.makeText(context, "Kudos awarded to $name!", Toast.LENGTH_SHORT).show()
      }
    )
  }

  // Dialog: Import CSV
  if (showImportDialog) {
    CsvImportDialog(
      onDismiss = { showImportDialog = false },
      onImport = { csv ->
        val count = viewModel.importCsv(csv)
        showImportDialog = false
        if (count > 0) {
          Toast.makeText(context, "Successfully loaded $count records!", Toast.LENGTH_LONG).show()
        } else {
          Toast.makeText(context, "Failed to parse CSV format", Toast.LENGTH_SHORT).show()
        }
      }
    )
  }
}

private fun shareViaWhatsApp(context: Context, message: String) {
  val intent = Intent(Intent.ACTION_SEND).apply {
    type = "text/plain"
    putExtra(Intent.EXTRA_TEXT, message)
    `package` = "com.whatsapp"
  }
  try {
    context.startActivity(intent)
  } catch (e: Exception) {
    // Fallback to general share sheet if WhatsApp app is not installed
    val chooser = Intent.createChooser(
      Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, message)
      },
      "Share Sales Briefing"
    )
    context.startActivity(chooser)
  }
}

@Composable
fun KudosDialog(
  onDismiss: () -> Unit,
  onSend: (name: String, role: String, note: String, badge: String) -> Unit
) {
  var name by remember { mutableStateOf("") }
  var role by remember { mutableStateOf("Promoter") }
  var note by remember { mutableStateOf("") }
  var selectedBadge by remember { mutableStateOf("80% Elite Club") }

  val badges = listOf("80% Elite Club", "Top Gun Performer", "DRR Rocket Champion", "Note Maestro")

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("👏 Award Team Kudos", color = TextPrimary, fontWeight = FontWeight.Bold) },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text("Teammate Name") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
          value = note,
          onValueChange = { note = it },
          label = { Text("Achievement / Shoutout") },
          modifier = Modifier.fillMaxWidth(),
          maxLines = 3
        )
        Text("Select Recognition Badge:", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          badges.take(2).forEach { b ->
            FilterChip(
              selected = selectedBadge == b,
              onClick = { selectedBadge = b },
              label = { Text(b, fontSize = 10.sp) },
              modifier = Modifier.weight(1f)
            )
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (name.isNotBlank() && note.isNotBlank()) {
            onSend(name, role, note, selectedBadge)
          }
        },
        colors = ButtonDefaults.buttonColors(containerColor = AccentViolet)
      ) {
        Text("Send Kudos", color = Color.White)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel", color = TextSecondary)
      }
    },
    containerColor = SurfaceDark
  )
}

@Composable
fun CsvImportDialog(
  onDismiss: () -> Unit,
  onImport: (String) -> Unit
) {
  var csvText by remember {
    mutableStateOf(
      "Emp Name,Emp ID,Retailer Name,TL,Target,Achievement,Volume\n" +
        "Rohan Gupta,EMP201,Reliance Digital Thane,TAUSIF SAFIK SHAIKH,850000,810000,56\n" +
        "Suresh Nair,EMP202,Croma Vashi,TAUSIF SAFIK SHAIKH,750000,720000,50\n" +
        "Aniket More,EMP203,Vijay Sales Chembur,Rohit Mehta,690000,540000,38"
    )
  }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("📥 Quick CSV Import", color = TextPrimary, fontWeight = FontWeight.Bold) },
    text = {
      Column {
        Text(
          "Paste or edit CSV records below (Columns: Emp Name, Emp ID, Retailer Name, TL, Target, Achievement, Volume):",
          style = MaterialTheme.typography.bodySmall,
          color = TextSecondary,
          fontSize = 11.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
          value = csvText,
          onValueChange = { csvText = it },
          modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
          textStyle = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp)
        )
      }
    },
    confirmButton = {
      Button(
        onClick = { onImport(csvText) },
        colors = ButtonDefaults.buttonColors(containerColor = AccentMint)
      ) {
        Text("Load Records", color = Color.Black, fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel", color = TextSecondary)
      }
    },
    containerColor = SurfaceDark
  )
}
