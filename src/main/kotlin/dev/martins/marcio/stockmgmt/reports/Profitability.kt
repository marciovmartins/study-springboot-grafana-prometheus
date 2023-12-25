package dev.martins.marcio.stockmgmt.reports

import java.time.LocalDate

data class Profitability(
    val shareCode: String,
    val periodStart: LocalDate,
    val periodEnd: LocalDate,
    val totalAmountEarnedCentAmount: Int,
    val totalAmountEarnedCurrency: String,
    val profitability: Double,
    val averageProfitability: Double,
)
