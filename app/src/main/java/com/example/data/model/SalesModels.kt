package com.example.data.model

data class PromoterRecord(
  val empName: String,
  val empId: String,
  val status: String = "Active", // "Active" or "Inactive"
  val retailerName: String,
  val retailerId: String,
  val parentCode: String,
  val tl: String,
  val csm: String,
  val zsm: String,
  val zone: String,
  val city: String,
  val targetValue: Double,
  val mtdAchValue: Double,
  val mtdAchVolume: Int,
  val noteTargetValue: Double,
  val noteAchValue: Double,
  val lmtdValue: Double = 0.0,
  val productSales: Map<String, Int> = emptyMap()
) {
  val achievementPct: Double
    get() = if (targetValue > 0) mtdAchValue / targetValue else 0.0

  val targetGap: Double
    get() = (targetValue - mtdAchValue).coerceAtLeast(0.0)

  val noteAchievementPct: Double
    get() = if (noteTargetValue > 0) noteAchValue / noteTargetValue else 0.0

  val isOverall80Plus: Boolean
    get() = achievementPct >= 0.80

  val isNote80Plus: Boolean
    get() = noteAchievementPct >= 0.80
}

enum class UserRole(val label: String, val defaultPass: String) {
  ADMIN("Admin", "2512"),
  ZSM("ZSM", "1111"),
  CSM("CSM", "2222"),
  TL("TL", "3333")
}

data class AuthUser(
  val username: String,
  val role: UserRole,
  val scopeField: String = "",
  val scopeValue: String = ""
)

enum class HierarchyLevel(val title: String, val icon: String, val subtitle: String) {
  ZSM("ZSM", "👨‍💼", "ZSM → Retailer Management"),
  CSM("CSM", "🧑‍💼", "CSM → Retailer Management"),
  TL("TL", "👔", "TL → Promoter Management"),
  PROMOTER("Promoter", "👤", "Promoter → Individual Focus"),
  PARENT_CODE("Parent Code", "🏪", "Parent Code → Retailer Management")
}

data class ScopeFilter(
  val level: HierarchyLevel = HierarchyLevel.ZSM,
  val selectedZsm: String = "",
  val selectedCsm: String = "",
  val selectedTl: String = "",
  val selectedEmp: String = "",
  val selectedRetailer: String = "",
  val selectedParentCode: String = ""
)

data class DRRMetrics(
  val reportDateString: String,
  val daysPassed: Int,
  val daysRemaining: Int,
  val totalDaysInMonth: Int,
  val totalTarget: Double,
  val totalAchievement: Double,
  val totalVolume: Int,
  val totalGap: Double,
  val currentDRR: Double,
  val requiredDRR: Double,
  val projectedAchievement: Double,
  val paceStatus: Boolean // true = on track or ahead
)

data class PerformanceItem(
  val name: String,
  val target: Double,
  val achievement: Double,
  val volume: Int,
  val pct: Double,
  val gap: Double,
  val currentDrr: Double = 0.0,
  val requiredDrr: Double = 0.0,
  val secondaryInfo: String = ""
)

data class TeamKudos(
  val id: String,
  val recipientName: String,
  val role: String,
  val achievementText: String,
  val badgeType: String,
  val senderName: String,
  val timestamp: Long = System.currentTimeMillis()
)
