package dev.martins.marcio.stockmgmt.reports

import java.time.LocalDate

data class Profitability(
    val shareCode: String,
    val periodStart: LocalDate,
    val periodEnd: LocalDate,
    val totalAmountEarnedCentAmount: Int,
    val totalDividendsCentAmount: Int,
    val totalRevenueCentAmount: Int,
    val currency: String,
    val dividendsProfitability: Double,
    val dividendsAverageProfitability: Double,
    val revenueProfitability: Double,
    val revenueAverageProfitability: Double,
)
