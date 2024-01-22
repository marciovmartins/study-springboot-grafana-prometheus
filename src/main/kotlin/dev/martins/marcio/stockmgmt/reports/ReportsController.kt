package dev.martins.marcio.stockmgmt.reports

import dev.martins.marcio.stockmgmt.RootController
import dev.martins.marcio.stockmgmt.movements.Movement
import dev.martins.marcio.stockmgmt.movements.MovementRepository
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.math.abs

@RestController
@RequestMapping(value = ["/reports"], produces = ["application/prs.hal-forms+json"])
class ReportsController(
    private val movementRepository: MovementRepository,
) {
    @GetMapping("/profitability")
    fun profitability(
        @RequestParam shareCode: String?,
        @RequestParam periodStart: LocalDate?,
        @RequestParam periodEnd: LocalDate?,
    ): ResponseEntity<EntityModel<Profitability>> {
        val sharesPurchased = movementRepository.findByShareCodeAndDateLessThanEqualAndTypeInOrderByDate(
            shareCode!!, periodEnd!!, Movement.Type.TRANSFER,
        )
        val dividends = movementRepository.findByShareCodeAndDateLessThanEqualAndTypeInOrderByDate(
            shareCode, periodEnd, Movement.Type.DIVIDENDS,
        )
        val dividendsProfitability = profitability(sharesPurchased, dividends, periodStart)

        val revenue = movementRepository.findByShareCodeAndDateLessThanEqualAndTypeInOrderByDate(
            shareCode, periodEnd, Movement.Type.REVENUE,
        )
        val revenueProfitability = profitability(sharesPurchased, revenue, periodStart)

        val interestsOnEquity = movementRepository.findByShareCodeAndDateLessThanEqualAndTypeInOrderByDate(
            shareCode, periodEnd, Movement.Type.INTEREST_ON_EQUITY,
        )
        val interestOnEquityProfitability = profitability(sharesPurchased, interestsOnEquity, periodStart)

        val totalDividendsCentAmount = dividendsProfitability?.totalAmountEarnedCentAmount ?: 0
        val totalRevenueCentAmount = revenueProfitability?.totalAmountEarnedCentAmount ?: 0
        val totalInterestOnEquityCentAmount = interestOnEquityProfitability?.totalAmountEarnedCentAmount ?: 0
        val profitability = Profitability(
            shareCode = shareCode,
            periodStart = periodStart!!,
            periodEnd = periodEnd,
            totalAmountEarnedCentAmount = totalRevenueCentAmount + totalDividendsCentAmount + totalInterestOnEquityCentAmount,
            totalDividendsCentAmount = totalDividendsCentAmount,
            totalRevenueCentAmount = totalRevenueCentAmount,
            totalInterestOnEquityCentAmount = totalInterestOnEquityCentAmount,
            currency = "BRL",
            dividendsProfitability = dividendsProfitability?.profitability ?: 0.0,
            dividendsAverageProfitability = dividendsProfitability?.averageProfitability ?: 0.0,
            revenueProfitability = revenueProfitability?.profitability ?: 0.0,
            revenueAverageProfitability = revenueProfitability?.averageProfitability ?: 0.0,
            interestOnEquityProfitability = interestOnEquityProfitability?.profitability ?: 0.0,
            interestOnEquityAverageProfitability = interestOnEquityProfitability?.averageProfitability ?: 0.0,
        )

        return ResponseEntity.ok(profitabilityEntityModel(profitability))
    }

    private fun profitability(
        sharesPurchased: List<Movement>,
        earnings: List<Movement>,
        periodStart: LocalDate?,
    ): ProfitabilityData? {
        val profitabilityByEarnings = mutableListOf<ProfitabilityData>()
        var profitabilitySum = 0.0
        var profitabilityCount = 0
        var previousEarningDate: LocalDate = sharesPurchased.lastOrNull {
            it.date.isBefore(earnings.firstOrNull()?.date ?: periodStart)
        }?.date
            ?: periodStart!!

        for (earning in earnings) {
            val lastProfitabilityByEarnings = profitabilityByEarnings.lastOrNull()
            val monthsAfterLastEarning = abs(
                ChronoUnit.MONTHS.between(
                    earning.date.withDayOfMonth(1),
                    previousEarningDate.withDayOfMonth(1)
                )
            ).let { if (it < 1) 1 else it }

            val totalAmountPaid = sharesPurchased
                .filter { it.date.isBefore(earning.date) }
                .sumOf { it.transactionValueCentAmount }

            val lastTotalAmountEarnedCentAmount = lastProfitabilityByEarnings?.totalAmountEarnedCentAmount ?: 0
            val totalAmountEarned = lastTotalAmountEarnedCentAmount + earning.transactionValueCentAmount

            val earnedAmountByTotalAmountPaid = (earning.transactionValueCentAmount.toDouble() * 100) / totalAmountPaid
            val profitability = earnedAmountByTotalAmountPaid / monthsAfterLastEarning

            profitabilitySum += profitability
            profitabilityCount += 1
            val averageProfitability = profitabilitySum / profitabilityCount

            profitabilityByEarnings.add(
                ProfitabilityData(
                    totalAmountEarnedCentAmount = totalAmountEarned,
                    currency = earning.currency,
                    profitability = profitability,
                    averageProfitability = averageProfitability
                )
            )

            previousEarningDate = earning.date
        }

        return profitabilityByEarnings.lastOrNull()
    }

    private fun profitabilityEntityModel(profitability: Profitability) = EntityModel.of(
        profitability,
        linkTo<ReportsController> {
            profitability(
                profitability.shareCode, profitability.periodStart, profitability.periodEnd
            )
        }
            .withSelfRel(),
        linkTo<ReportsController> { profitability(null, null, null) }
            .withRel("profitability"),
        linkTo<ReportsController> { profitability(null, profitability.periodStart, profitability.periodEnd) }
            .withRel("profitability-for-another-share-code"),
        linkTo<RootController> { index() }
            .withRel("root")
    )

    private data class ProfitabilityData(
        val totalAmountEarnedCentAmount: Int,
        val currency: String,
        val profitability: Double,
        val averageProfitability: Double,
    )
}