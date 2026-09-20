package com.example

import com.example.data.model.UserRole
import com.example.ui.viewmodel.SalesCommandViewModel
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testLoginAuthentication() {
    val vm = SalesCommandViewModel()
    // Test valid credentials
    assertTrue(vm.login("Admin", "2512"))
    assertEquals(UserRole.ADMIN, vm.currentUser.value?.role)

    assertTrue(vm.login("TL", "3333"))
    assertEquals(UserRole.TL, vm.currentUser.value?.role)

    // Test invalid credentials
    assertFalse(vm.login("TL", "wrong_pass"))
  }

  @Test
  fun testWhatsAppMessageGeneration() {
    val vm = SalesCommandViewModel()
    val msg = vm.generateWhatsAppMessage()
    assertTrue(msg.contains("TAUSIF • SALES COMMAND BRIEFING"))
    assertTrue(msg.contains("Target:"))
    assertTrue(msg.contains("Achievement:"))
    assertTrue(msg.contains("RUN-RATE INTELLIGENCE"))
  }
}

