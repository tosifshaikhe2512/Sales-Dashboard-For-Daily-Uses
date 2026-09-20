package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserRole
import com.example.ui.theme.*

@Composable
fun LoginScreen(
  onLogin: (username: String, pass: String) -> Boolean,
  onQuickLoginAs: (role: UserRole) -> Unit,
  modifier: Modifier = Modifier
) {
  var username by remember { mutableStateOf("Admin") }
  var password by remember { mutableStateOf("2512") }
  var passwordVisible by remember { mutableStateOf(false) }
  var errorMessage by remember { mutableStateOf<String?>(null) }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(
        Brush.radialGradient(
          colors = listOf(Color(0xFF27204D), BackgroundDark, BackgroundDark),
          radius = 1200f
        )
      )
      .padding(20.dp),
    contentAlignment = Alignment.Center
  ) {
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .widthIn(max = 440.dp)
        .testTag("login_card"),
      shape = RoundedCornerShape(22.dp),
      colors = CardDefaults.cardColors(containerColor = SurfaceDark),
      border = CardDefaults.outlinedCardBorder().copy(
        brush = Brush.verticalGradient(
          listOf(Color(0xFF3B4660), SurfaceBorder)
        )
      )
    ) {
      Column(
        modifier = Modifier
          .padding(24.dp)
          .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Glowing App Logo
        Box(
          modifier = Modifier
            .size(60.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Brush.linearGradient(listOf(AccentViolet, AccentMint))),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "TS",
            color = Color.White,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Black
          )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
          text = "TAUSIF • SALES COMMAND",
          style = MaterialTheme.typography.titleMedium,
          color = TextPrimary,
          fontWeight = FontWeight.ExtraBold,
          letterSpacing = 0.5.sp,
          textAlign = TextAlign.Center
        )

        Text(
          text = "Secure Role-Based Mobile Command Center",
          style = MaterialTheme.typography.bodySmall,
          color = TextSecondary,
          textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Username
        OutlinedTextField(
          value = username,
          onValueChange = {
            username = it
            errorMessage = null
          },
          label = { Text("Username") },
          leadingIcon = {
            Icon(Icons.Default.Person, contentDescription = null, tint = TextSecondary)
          },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("login_username_input"),
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

        Spacer(modifier = Modifier.height(14.dp))

        // Password
        OutlinedTextField(
          value = password,
          onValueChange = {
            password = it
            errorMessage = null
          },
          label = { Text("Passcode") },
          leadingIcon = {
            Icon(Icons.Default.Lock, contentDescription = null, tint = TextSecondary)
          },
          trailingIcon = {
            IconButton(onClick = { passwordVisible = !passwordVisible }) {
              Icon(
                if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                contentDescription = "Toggle visibility",
                tint = TextSecondary
              )
            }
          },
          visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("login_password_input"),
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

        if (errorMessage != null) {
          Spacer(modifier = Modifier.height(10.dp))
          Text(
            text = errorMessage ?: "",
            color = StatusDanger,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold
          )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
          onClick = {
            val success = onLogin(username, password)
            if (!success) {
              errorMessage = "Invalid username or passcode. Try quick demo accounts below."
            }
          },
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .testTag("login_submit_btn"),
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = AccentViolet
          )
        ) {
          Text(
            text = "🔐 Authenticate & Enter",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = Color.White
          )
        }

        Spacer(modifier = Modifier.height(24.dp))
        Divider(color = SurfaceBorder)
        Spacer(modifier = Modifier.height(16.dp))

        Text(
          text = "⚡ Quick Demo Role Logins",
          style = MaterialTheme.typography.labelMedium,
          color = AccentMint,
          fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          QuickRoleButton(
            label = "Admin",
            sub = "2512",
            modifier = Modifier.weight(1f),
            onClick = {
              username = "Admin"
              password = "2512"
              onQuickLoginAs(UserRole.ADMIN)
            }
          )
          QuickRoleButton(
            label = "TL",
            sub = "3333",
            modifier = Modifier.weight(1f),
            onClick = {
              username = "TL"
              password = "3333"
              onQuickLoginAs(UserRole.TL)
            }
          )
          QuickRoleButton(
            label = "CSM",
            sub = "2222",
            modifier = Modifier.weight(1f),
            onClick = {
              username = "CSM"
              password = "2222"
              onQuickLoginAs(UserRole.CSM)
            }
          )
          QuickRoleButton(
            label = "ZSM",
            sub = "1111",
            modifier = Modifier.weight(1f),
            onClick = {
              username = "ZSM"
              password = "1111"
              onQuickLoginAs(UserRole.ZSM)
            }
          )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(
          text = "Roles automatically scope dashboard views and leaderboards.",
          style = MaterialTheme.typography.bodySmall,
          color = TextTertiary,
          fontSize = 11.sp,
          textAlign = TextAlign.Center
        )
      }
    }
  }
}

@Composable
private fun QuickRoleButton(
  label: String,
  sub: String,
  modifier: Modifier = Modifier,
  onClick: () -> Unit
) {
  OutlinedButton(
    onClick = onClick,
    modifier = modifier.height(52.dp),
    shape = RoundedCornerShape(10.dp),
    contentPadding = PaddingValues(2.dp),
    border = ButtonDefaults.outlinedButtonBorder().copy(brush = Brush.linearGradient(listOf(SurfaceBorder, SurfaceBorder))),
    colors = ButtonDefaults.outlinedButtonColors(containerColor = SurfaceElevated)
  ) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Text(
        text = label,
        color = TextPrimary,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold
      )
      Text(
        text = sub,
        color = AccentMint,
        style = MaterialTheme.typography.bodySmall,
        fontSize = 10.sp
      )
    }
  }
}
