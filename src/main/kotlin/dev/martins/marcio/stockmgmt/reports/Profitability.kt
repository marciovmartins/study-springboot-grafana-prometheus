package dev.martins.marcio.stockmgmt.reports

import java.time.LocalDate

data class Profitability(
    val shareCode: String,
    val periodStart: LocalDate,
    val periodEnd: LocalDate,
    val score: Double,
    val scoreAverage: Double,
    val totalInvestedCentAmount: Int,
    val totalEarnedCentAmount: Int,
    val totalDividendsCentAmount: Int,
    val totalRevenueCentAmount: Int,
    val totalInterestOnEquityCentAmount: Int,
    val currency: String,
    val dividendsProfitability: Double,
    val dividendsAverageProfitability: Double,
    val revenueProfitability: Double,
    val revenueAverageProfitability: Double,
    val interestOnEquityProfitability: Double,
    val interestOnEquityAverageProfitability: Double,
)
