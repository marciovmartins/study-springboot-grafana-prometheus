package dev.martins.marcio.stockmgmt.reports

import dev.martins.marcio.stockmgmt.RootController
import dev.martins.marcio.stockmgmt.dividendsandinterestsearned.DividendsAndInterestsEarnedRepository
import dev.martins.marcio.stockmgmt.sharespurchased.SharePurchasedRepository
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
@RequestMapping(value = ["/reports"], produces = ["application/prs.hal-forms+json"])
class ReportsController(
    private val sharePurchasedRepository: SharePurchasedRepository,
    private val dividendsAndInterestsEarnedRepository: DividendsAndInterestsEarnedRepository,
) {
    @GetMapping("/profitability")
    fun profitability(
        @RequestParam shareCode: String?,
        @RequestParam periodStart: LocalDate?,
        @RequestParam periodEnd: LocalDate?,
    ): ResponseEntity<EntityModel<Profitability>> {
        val earnings = dividendsAndInterestsEarnedRepository
            .findByShareCodeAndDateGreaterThanEqualAndDateLessThanEqualOrderByDate(
                shareCode!!, periodStart!!, periodEnd!!
            )
        val sharesPurchased = sharePurchasedRepository
            .findByShareCodeAndDateGreaterThanEqualAndDateLessThanEqualOrderByDate(
                shareCode, periodStart, periodEnd
            )

        val profitabilityByEarnings = mutableListOf<Profitability>()
        for (earning in earnings) {
            val lastProfitabilityByEarnings = profitabilityByEarnings.lastOrNull()

            val totalAmountPaid = sharesPurchased
                .filter { it.date.isBefore(earning.date) }
                .sumOf { it.amountPaidCentAmount }

            val lastTotalAmountEarnedCentAmount = lastProfitabilityByEarnings?.totalAmountEarnedCentAmount ?: 0
            val totalAmountEarned = lastTotalAmountEarnedCentAmount + earning.amountEarnedCentAmount

            val profitability = (earning.amountEarnedCentAmount.toDouble() * 100) / totalAmountPaid

            val lastProfitability = lastProfitabilityByEarnings?.profitability ?: profitability
            val averageProfitability = (profitability + lastProfitability) / 2

            profitabilityByEarnings.add(
                Profitability(
                    shareCode = shareCode,
                    periodStart = periodStart,
                    periodEnd = periodEnd,
                    totalAmountEarnedCentAmount = totalAmountEarned,
                    totalAmountEarnedCurrency = earning.amountEarnedCurrency,
                    profitability = profitability,
                    averageProfitability = averageProfitability
                )
            )
        }

        return ResponseEntity.ok(profitabilityEntityModel(profitabilityByEarnings.last()))
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
}