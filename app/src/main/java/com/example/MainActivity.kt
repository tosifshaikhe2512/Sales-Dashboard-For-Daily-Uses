package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.ActionDetailModal
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.SalesCommandViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        SalesCommandApp()
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesCommandApp(
  viewModel: SalesCommandViewModel = viewModel()
) {
  val currentUser by viewModel.currentUser.collectAsState()
  val records by viewModel.filteredRecords.collectAsState()

  var currentNavTab by remember { mutableStateOf(0) }
  var activeActionKey by remember { mutableStateOf<String?>(null) }

  if (currentUser == null) {
    LoginScreen(
      onLogin = { username, pass -> viewModel.login(username, pass) },
      onQuickLoginAs = { role -> viewModel.loginAs(role) }
    )
  } else {
    Scaffold(
      modifier = Modifier.fillMaxSize(),
      contentWindowInsets = WindowInsets.safeDrawing,
      topBar = {
        TopAppBar(
          title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(36.dp)
                  .clip(RoundedCornerShape(10.dp))
                  .background(Brush.linearGradient(listOf(AccentViolet, AccentMint))),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = "TS",
                  color = Color.White,
                  fontWeight = FontWeight.Black,
                  fontSize = 14.sp
                )
              }
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(
                  text = "SALES COMMAND",
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.Black,
                  color = TextPrimary,
                  letterSpacing = 0.5.sp
                )
                Text(
                  text = "Role: ${currentUser?.role?.label}${if (!currentUser?.scopeValue.isNullOrEmpty()) " • ${currentUser?.scopeValue}" else ""}",
                  style = MaterialTheme.typography.labelSmall,
                  color = AccentMint,
                  fontSize = 10.sp
                )
              }
            }
          },
          actions = {
            IconButton(
              onClick = { viewModel.logout() },
              modifier = Modifier.testTag("logout_btn")
            ) {
              Icon(
                Icons.Default.ExitToApp,
                contentDescription = "Logout",
                tint = TextSecondary
              )
            }
          },
          colors = TopAppBarDefaults.topAppBarColors(
            containerColor = BackgroundDark,
            titleContentColor = TextPrimary
          )
        )
      },
      bottomBar = {
        NavigationBar(
          containerColor = SurfaceDark,
          contentColor = TextSecondary,
          tonalElevation = 8.dp
        ) {
          NavigationBarItem(
            selected = currentNavTab == 0,
            onClick = { currentNavTab = 0 },
            icon = { Icon(Icons.Default.Dashboard, contentDescription = "Overview") },
            label = { Text("Overview", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = AccentMint,
              selectedTextColor = AccentMint,
              indicatorColor = SurfaceElevated,
              unselectedIconColor = TextSecondary,
              unselectedTextColor = TextSecondary
            )
          )

          NavigationBarItem(
            selected = currentNavTab == 1,
            onClick = { currentNavTab = 1 },
            icon = { Icon(Icons.Default.Groups, contentDescription = "Hierarchy") },
            label = { Text("Boards", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = AccentMint,
              selectedTextColor = AccentMint,
              indicatorColor = SurfaceElevated,
              unselectedIconColor = TextSecondary,
              unselectedTextColor = TextSecondary
            )
          )

          NavigationBarItem(
            selected = currentNavTab == 2,
            onClick = { currentNavTab = 2 },
            icon = { Icon(Icons.Default.Smartphone, contentDescription = "Products") },
            label = { Text("Products", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = AccentMint,
              selectedTextColor = AccentMint,
              indicatorColor = SurfaceElevated,
              unselectedIconColor = TextSecondary,
              unselectedTextColor = TextSecondary
            )
          )

          NavigationBarItem(
            selected = currentNavTab == 3,
            onClick = { currentNavTab = 3 },
            icon = { Icon(Icons.Default.Search, contentDescription = "V5 Search") },
            label = { Text("V5 Search", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = AccentMint,
              selectedTextColor = AccentMint,
              indicatorColor = SurfaceElevated,
              unselectedIconColor = TextSecondary,
              unselectedTextColor = TextSecondary
            )
          )

          NavigationBarItem(
            selected = currentNavTab == 4,
            onClick = { currentNavTab = 4 },
            icon = { Icon(Icons.Default.EmojiEvents, contentDescription = "Engagement") },
            label = { Text("Engagement", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = AccentMint,
              selectedTextColor = AccentMint,
              indicatorColor = SurfaceElevated,
              unselectedIconColor = TextSecondary,
              unselectedTextColor = TextSecondary
            )
          )
        }
      }
    ) { innerPadding ->
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(BackgroundDark)
          .padding(innerPadding)
      ) {
        when (currentNavTab) {
          0 -> OverviewScreen(
            viewModel = viewModel,
            onShowActionDetail = { key -> activeActionKey = key }
          )
          1 -> HierarchyBoardsScreen(
            viewModel = viewModel
          )
          2 -> ProductIntelligenceScreen(
            viewModel = viewModel
          )
          3 -> V5SearchScreen(
            viewModel = viewModel
          )
          4 -> EngagementScreen(
            viewModel = viewModel
          )
        }
      }

      // Drill-down Action Details Modal Dialog
      activeActionKey?.let { key ->
        ActionDetailModal(
          actionKey = key,
          records = records,
          viewModel = viewModel,
          onDismiss = { activeActionKey = null }
        )
      }
    }
  }
}

