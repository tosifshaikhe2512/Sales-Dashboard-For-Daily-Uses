package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.sample.SampleSalesData
import kotlinx.coroutines.flow.*
import java.text.NumberFormat
import java.util.*

class SalesCommandViewModel : ViewModel() {

  // Initial default: Admin logged in so app opens instantly with 1-click
  private val _currentUser = MutableStateFlow<AuthUser?>(
    AuthUser("Admin", UserRole.ADMIN)
  )
  val currentUser: StateFlow<AuthUser?> = _currentUser.asStateFlow()

  private val _rawRecords = MutableStateFlow<List<PromoterRecord>>(
    SampleSalesData.getInitialRecords()
  )
  val rawRecords: StateFlow<List<PromoterRecord>> = _rawRecords.asStateFlow()

  private val _scopeFilter = MutableStateFlow(ScopeFilter())
  val scopeFilter: StateFlow<ScopeFilter> = _scopeFilter.asStateFlow()

  private val _searchQuery = MutableStateFlow("")
  val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

  private val _teamKudos = MutableStateFlow(SampleSalesData.sampleKudos)
  val teamKudos: StateFlow<List<TeamKudos>> = _teamKudos.asStateFlow()

  // Target simulator multiplier (1.0 = 100% of current target)
  private val _simulatedTargetPace = MutableStateFlow(1.0f)
  val simulatedTargetPace: StateFlow<Float> = _simulatedTargetPace.asStateFlow()

  val indianCurrencyFormat: NumberFormat = NumberFormat.getCurrencyInstance(Locale("en", "IN")).apply {
    maximumFractionDigits = 0
  }

  // Filtered records based on Login Scope + Selected Filter Dropdowns
  val filteredRecords: StateFlow<List<PromoterRecord>> = combine(
    _rawRecords,
    _currentUser,
    _scopeFilter
  ) { records, user, filter ->
    if (user == null) return@combine emptyList()

    // 1. Role-based isolation
    val roleScoped = records.filter { r ->
      when (user.role) {
        UserRole.ADMIN -> true
        UserRole.ZSM -> if (user.scopeValue.isNotEmpty()) r.zsm.equals(user.scopeValue, ignoreCase = true) else true
        UserRole.CSM -> if (user.scopeValue.isNotEmpty()) r.csm.equals(user.scopeValue, ignoreCase = true) else true
        UserRole.TL -> if (user.scopeValue.isNotEmpty()) r.tl.equals(user.scopeValue, ignoreCase = true) else true
      }
    }

    // 2. Interactive dropdown filter
    roleScoped.filter { r ->
      val matchZsm = filter.selectedZsm.isEmpty() || r.zsm.equals(filter.selectedZsm, ignoreCase = true)
      val matchCsm = filter.selectedCsm.isEmpty() || r.csm.equals(filter.selectedCsm, ignoreCase = true)
      val matchTl = filter.selectedTl.isEmpty() || r.tl.equals(filter.selectedTl, ignoreCase = true)
      val matchEmp = filter.selectedEmp.isEmpty() || r.empName.equals(filter.selectedEmp, ignoreCase = true)
      val matchRet = filter.selectedRetailer.isEmpty() || r.retailerName.equals(filter.selectedRetailer, ignoreCase = true)
      val matchParent = filter.selectedParentCode.isEmpty() || r.parentCode.equals(filter.selectedParentCode, ignoreCase = true)

      matchZsm && matchCsm && matchTl && matchEmp && matchRet && matchParent
    }
  }.stateIn(viewModelScope, SharingStarted.Eagerly, SampleSalesData.getInitialRecords())

  // Computed DRR and Target Metrics
  val drrMetrics: StateFlow<DRRMetrics> = filteredRecords.map { list ->
    val cal = Calendar.getInstance()
    val totalDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    val daysPassed = cal.get(Calendar.DAY_OF_MONTH).coerceAtLeast(1)
    val daysRemaining = (totalDays - daysPassed).coerceAtLeast(1)

    val target = list.sumOf { it.targetValue }
    val ach = list.sumOf { it.mtdAchValue }
    val volume = list.sumOf { it.mtdAchVolume }
    val gap = (target - ach).coerceAtLeast(0.0)

    val currDrr = if (daysPassed > 0) ach / daysPassed else 0.0
    val reqDrr = if (daysRemaining > 0) gap / daysRemaining else 0.0
    val proj = ach + (currDrr * daysRemaining)
    val paceAhead = currDrr >= reqDrr

    val monthNames = arrayOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
    val dateStr = "${cal.get(Calendar.DAY_OF_MONTH)} ${monthNames[cal.get(Calendar.MONTH)]} ${cal.get(Calendar.YEAR)}"

    DRRMetrics(
      reportDateString = dateStr,
      daysPassed = daysPassed,
      daysRemaining = daysRemaining,
      totalDaysInMonth = totalDays,
      totalTarget = target,
      totalAchievement = ach,
      totalVolume = volume,
      totalGap = gap,
      currentDRR = currDrr,
      requiredDRR = reqDrr,
      projectedAchievement = proj,
      paceStatus = paceAhead
    )
  }.stateIn(
    viewModelScope,
    SharingStarted.Eagerly,
    DRRMetrics("Today", 19, 11, 30, 0.0, 0.0, 0, 0.0, 0.0, 0.0, 0.0, true)
  )

  // Universal Search Results across all fields
  val searchResults: StateFlow<List<PromoterRecord>> = combine(
    filteredRecords,
    _searchQuery
  ) { list, query ->
    val q = query.trim().lowercase()
    if (q.isEmpty()) emptyList()
    else {
      list.filter { r ->
        r.empName.lowercase().contains(q) ||
          r.empId.lowercase().contains(q) ||
          r.retailerName.lowercase().contains(q) ||
          r.retailerId.lowercase().contains(q) ||
          r.parentCode.lowercase().contains(q) ||
          r.tl.lowercase().contains(q) ||
          r.csm.lowercase().contains(q) ||
          r.zsm.lowercase().contains(q) ||
          r.city.lowercase().contains(q) ||
          r.zone.lowercase().contains(q) ||
          r.status.lowercase().contains(q) ||
          r.productSales.keys.any { it.lowercase().contains(q) }
      }
    }
  }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

  // Leaderboards & Aggregations
  val csmPerformance: StateFlow<List<PerformanceItem>> = filteredRecords.map { list ->
    val drr = drrMetrics.value
    list.groupBy { it.csm }.map { (name, group) ->
      val target = group.sumOf { it.targetValue }
      val ach = group.sumOf { it.mtdAchValue }
      val vol = group.sumOf { it.mtdAchVolume }
      val gap = (target - ach).coerceAtLeast(0.0)
      val currDrr = if (drr.daysPassed > 0) ach / drr.daysPassed else 0.0
      val reqDrr = if (drr.daysRemaining > 0) gap / drr.daysRemaining else 0.0
      PerformanceItem(
        name = name,
        target = target,
        achievement = ach,
        volume = vol,
        pct = if (target > 0) ach / target else 0.0,
        gap = gap,
        currentDrr = currDrr,
        requiredDrr = reqDrr,
        secondaryInfo = "${group.size} Promoters"
      )
    }.sortedByDescending { it.pct }
  }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

  val tlPerformance: StateFlow<List<PerformanceItem>> = filteredRecords.map { list ->
    val drr = drrMetrics.value
    list.groupBy { it.tl }.map { (name, group) ->
      val target = group.sumOf { it.targetValue }
      val ach = group.sumOf { it.mtdAchValue }
      val vol = group.sumOf { it.mtdAchVolume }
      val gap = (target - ach).coerceAtLeast(0.0)
      val currDrr = if (drr.daysPassed > 0) ach / drr.daysPassed else 0.0
      val reqDrr = if (drr.daysRemaining > 0) gap / drr.daysRemaining else 0.0
      PerformanceItem(
        name = name,
        target = target,
        achievement = ach,
        volume = vol,
        pct = if (target > 0) ach / target else 0.0,
        gap = gap,
        currentDrr = currDrr,
        requiredDrr = reqDrr,
        secondaryInfo = "${group.size} Promoters"
      )
    }.sortedByDescending { it.pct }
  }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

  val retailerPerformance: StateFlow<List<PerformanceItem>> = filteredRecords.map { list ->
    list.groupBy { it.retailerName }.map { (name, group) ->
      val target = group.sumOf { it.targetValue }
      val ach = group.sumOf { it.mtdAchValue }
      val vol = group.sumOf { it.mtdAchVolume }
      PerformanceItem(
        name = name,
        target = target,
        achievement = ach,
        volume = vol,
        pct = if (target > 0) ach / target else 0.0,
        gap = (target - ach).coerceAtLeast(0.0),
        secondaryInfo = "${group.firstOrNull()?.city ?: ""} • ${group.firstOrNull()?.parentCode ?: ""}"
      )
    }.sortedByDescending { it.achievement }
  }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

  val promoterPerformance: StateFlow<List<PerformanceItem>> = filteredRecords.map { list ->
    list.map { r ->
      PerformanceItem(
        name = r.empName,
        target = r.targetValue,
        achievement = r.mtdAchValue,
        volume = r.mtdAchVolume,
        pct = r.achievementPct,
        gap = r.targetGap,
        secondaryInfo = "${r.retailerName} (${r.tl})"
      )
    }.sortedByDescending { it.pct }
  }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

  val productTotals: StateFlow<Map<String, Int>> = filteredRecords.map { list ->
    val map = mutableMapOf<String, Int>()
    list.forEach { r ->
      r.productSales.forEach { (model, count) ->
        map[model] = (map[model] ?: 0) + count
      }
    }
    map.toList().sortedByDescending { it.second }.toMap()
  }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyMap())

  // Note series aggregated values
  val noteSeriesMetrics: StateFlow<Triple<Double, Double, Double>> = filteredRecords.map { list ->
    val target = list.sumOf { it.noteTargetValue }
    val ach = list.sumOf { it.noteAchValue }
    val pct = if (target > 0) ach / target else 0.0
    Triple(target, ach, pct)
  }.stateIn(viewModelScope, SharingStarted.Eagerly, Triple(0.0, 0.0, 0.0))

  // 80%+ Performance stats: Pair of (Overall 80%+ count, Note 80%+ count) to Total promoters
  val performance80Club: StateFlow<Triple<Int, Int, Int>> = filteredRecords.map { list ->
    val total = list.size
    val overall80 = list.count { it.isOverall80Plus }
    val note80 = list.count { it.isNote80Plus }
    Triple(overall80, note80, total)
  }.stateIn(viewModelScope, SharingStarted.Eagerly, Triple(0, 0, 0))

  // Unique Dropdown Options
  val availableZsM: StateFlow<List<String>> = _rawRecords.map { it.map { r -> r.zsm }.distinct().sorted() }
    .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
  val availableCsm: StateFlow<List<String>> = _rawRecords.map { it.map { r -> r.csm }.distinct().sorted() }
    .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
  val availableTl: StateFlow<List<String>> = _rawRecords.map { it.map { r -> r.tl }.distinct().sorted() }
    .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
  val availableParentCodes: StateFlow<List<String>> = _rawRecords.map { it.map { r -> r.parentCode }.distinct().sorted() }
    .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

  // Authentication
  fun login(user: String, pass: String): Boolean {
    val trimmedUser = user.trim()
    val matchedRole = UserRole.values().firstOrNull {
      it.label.equals(trimmedUser, ignoreCase = true) && it.defaultPass == pass
    }

    return if (matchedRole != null) {
      val scopeVal = when (matchedRole) {
        UserRole.TL -> "TAUSIF SAFIK SHAIKH"
        UserRole.CSM -> "Vikram Patil"
        UserRole.ZSM -> "Rahul Sharma"
        UserRole.ADMIN -> ""
      }
      _currentUser.value = AuthUser(trimmedUser, matchedRole, scopeField = matchedRole.label, scopeValue = scopeVal)
      true
    } else false
  }

  fun loginAs(role: UserRole) {
    val scopeVal = when (role) {
      UserRole.TL -> "TAUSIF SAFIK SHAIKH"
      UserRole.CSM -> "Vikram Patil"
      UserRole.ZSM -> "Rahul Sharma"
      UserRole.ADMIN -> ""
    }
    _currentUser.value = AuthUser(role.label, role, scopeField = role.label, scopeValue = scopeVal)
  }

  fun logout() {
    _currentUser.value = null
  }

  // Filter setters
  fun setHierarchyLevel(level: HierarchyLevel) {
    _scopeFilter.update { it.copy(level = level) }
  }

  fun setZsmFilter(value: String) {
    _scopeFilter.update { it.copy(selectedZsm = value) }
  }

  fun setCsmFilter(value: String) {
    _scopeFilter.update { it.copy(selectedCsm = value) }
  }

  fun setTlFilter(value: String) {
    _scopeFilter.update { it.copy(selectedTl = value) }
  }

  fun setRetailerFilter(value: String) {
    _scopeFilter.update { it.copy(selectedRetailer = value) }
  }

  fun setParentCodeFilter(value: String) {
    _scopeFilter.update { it.copy(selectedParentCode = value) }
  }

  fun resetFilters() {
    _scopeFilter.value = ScopeFilter(level = _scopeFilter.value.level)
    _searchQuery.value = ""
  }

  fun setSearchQuery(query: String) {
    _searchQuery.value = query
  }

  fun setSimulatedTargetPace(multiplier: Float) {
    _simulatedTargetPace.value = multiplier
  }

  // Add Kudos
  fun addKudos(recipientName: String, role: String, achievement: String, badge: String) {
    val sender = _currentUser.value?.let { "${it.username} (${it.role.label})" } ?: "Sales Command Leader"
    val kudos = TeamKudos(
      id = UUID.randomUUID().toString(),
      recipientName = recipientName,
      role = role,
      achievementText = achievement,
      badgeType = badge,
      senderName = sender
    )
    _teamKudos.update { listOf(kudos) + it }
  }

  // Reset to original mock dataset
  fun resetToSampleData() {
    _rawRecords.value = SampleSalesData.getInitialRecords()
    resetFilters()
  }

  // Simple CSV / Text Import
  fun importCsv(csvText: String): Int {
    return try {
      val lines = csvText.lines().filter { it.isNotBlank() }
      if (lines.size < 2) return 0

      val parsed = mutableListOf<PromoterRecord>()
      lines.drop(1).forEach { line ->
        val parts = line.split(",").map { it.trim().removeSurrounding("\"") }
        if (parts.size >= 7) {
          val name = parts[0]
          val id = parts.getOrNull(1) ?: "EMP${100 + parsed.size}"
          val retailer = parts.getOrNull(2) ?: "General Retailer"
          val tl = parts.getOrNull(3) ?: "TL Lead"
          val target = parts.getOrNull(4)?.toDoubleOrNull() ?: 500000.0
          val ach = parts.getOrNull(5)?.toDoubleOrNull() ?: 400000.0
          val vol = parts.getOrNull(6)?.toIntOrNull() ?: 30

          parsed.add(
            PromoterRecord(
              empName = name,
              empId = id,
              retailerName = retailer,
              retailerId = "RET_${id}",
              parentCode = "PC_DEF",
              tl = tl,
              csm = "CSM Central",
              zsm = "ZSM Head",
              zone = "Central",
              city = "Metro",
              targetValue = target,
              mtdAchValue = ach,
              mtdAchVolume = vol,
              noteTargetValue = target * 0.45,
              noteAchValue = ach * 0.42,
              productSales = mapOf("Redmi A5" to (vol * 0.3).toInt(), "Note 14" to (vol * 0.4).toInt(), "Note 14 Pro" to (vol * 0.3).toInt())
            )
          )
        }
      }
      if (parsed.isNotEmpty()) {
        _rawRecords.value = parsed
        resetFilters()
      }
      parsed.size
    } catch (e: Exception) {
      0
    }
  }

  // Format currency helper
  fun formatMoney(amount: Double): String {
    return indianCurrencyFormat.format(amount)
  }

  // Generate WhatsApp Update briefing
  fun generateWhatsAppMessage(): String {
    val drr = drrMetrics.value
    val list = filteredRecords.value
    val club80 = performance80Club.value
    val (noteTarget, noteAch, notePct) = noteSeriesMetrics.value

    val scopeDesc = when (_scopeFilter.value.level) {
      HierarchyLevel.ZSM -> if (_scopeFilter.value.selectedZsm.isNotEmpty()) "ZSM: ${_scopeFilter.value.selectedZsm}" else "All ZSMs"
      HierarchyLevel.CSM -> if (_scopeFilter.value.selectedCsm.isNotEmpty()) "CSM: ${_scopeFilter.value.selectedCsm}" else "All CSMs"
      HierarchyLevel.TL -> if (_scopeFilter.value.selectedTl.isNotEmpty()) "TL: ${_scopeFilter.value.selectedTl}" else "All TLs"
      HierarchyLevel.PARENT_CODE -> if (_scopeFilter.value.selectedParentCode.isNotEmpty()) "Parent: ${_scopeFilter.value.selectedParentCode}" else "All Stores"
      HierarchyLevel.PROMOTER -> "Promoter Scope"
    }

    val topPromoter = promoterPerformance.value.firstOrNull()?.let { "${it.name} (${(it.pct * 100).toInt()}%)" } ?: "—"
    val lowPerformers = list.count { it.achievementPct < 0.30 }
    val zeroVolume = list.count { it.mtdAchVolume == 0 }

    return """
🚀 *TAUSIF • SALES COMMAND BRIEFING*
📅 *Report Date:* ${drr.reportDateString} (${drr.daysPassed} Days Passed • ${drr.daysRemaining} Days Left)
📍 *Scope:* $scopeDesc

🎯 *Target:* ${formatMoney(drr.totalTarget)}
✅ *Achievement:* ${formatMoney(drr.totalAchievement)} (${(if (drr.totalTarget > 0) drr.totalAchievement / drr.totalTarget * 100 else 0.0).toInt()}%)
📦 *Volume:* ${drr.totalVolume} Units Sold
⚠️ *Target Gap:* ${formatMoney(drr.totalGap)}

⚡ *RUN-RATE INTELLIGENCE*
• Current DRR: ${formatMoney(drr.currentDRR)} / day
• Required DRR: ${formatMoney(drr.requiredDRR)} / remaining day
• Month-End Projection: ${formatMoney(drr.projectedAchievement)}
• Status: ${if (drr.paceStatus) "🟢 Ahead of required pace!" else "🔴 Behind required pace — push daily volume!"}

📱 *NOTE SERIES & 80%+ CLUB*
• Note Series Ach: ${formatMoney(noteAch)} / ${formatMoney(noteTarget)} (${(notePct * 100).toInt()}%)
• Overall 80%+ Achievers: ${club80.first} / ${club80.third}
• Note 80%+ Achievers: ${club80.second} / ${club80.third}

🏆 *PERFORMANCE HIGHLIGHTS*
• Top Performer: $topPromoter
• Promoters < 30%: $lowPerformers (Needs Immediate Activation)
• Zero Volume Promoters: $zeroVolume

🔥 *ACTION TODAY:* Close the daily DRR gap and prioritize high-value Note Series activations!
    """.trimIndent()
  }
}
