package dev.martins.marcio.stockmgmt

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.boot.test.web.client.postForEntity
import org.springframework.http.HttpStatusCode
import org.springframework.http.MediaType
import org.springframework.web.util.UriComponentsBuilder
import java.time.LocalDate
import java.util.UUID

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class ReportsIT {
    @Autowired
    private lateinit var testRestTemplate: TestRestTemplate

    private val chars = ('A'..'Z') + ('a'..'z') + ('0'..'9')

    @Test
    fun `generate profitability report`() {
        // given
        val shareCode = List(10) { chars.random() }.joinToString("")
        val periodStart = LocalDate.now().minusDays(91)
        val periodEnd = LocalDate.now().minusDays(29)

        val sharesPurchases = listOf(
            testSharePurchased(
                shareCode = shareCode,
                date = LocalDate.now().minusDays(90),
                sharePriceCentAmount = 9725,
                quantity = 16,
                amountPaidCentAmount = 155600
            ),
            testSharePurchased(
                shareCode = shareCode,
                date = LocalDate.now().minusDays(60),
                sharePriceCentAmount = 8350,
                quantity = 28,
                amountPaidCentAmount = 233800
            ),
            testSharePurchased(
                shareCode = shareCode,
                date = LocalDate.now().minusDays(30),
                sharePriceCentAmount = 7710,
                quantity = 35,
                amountPaidCentAmount = 269850
            ),
        )
        val dividendsAndInterestsEarned = listOf(
            testDividendsAndInterestsEarned(
                shareCode = shareCode,
                date = LocalDate.now().minusDays(75),
                amountEarnedCentAmount = 1152
            ),
            testDividendsAndInterestsEarned(
                shareCode = shareCode,
                date = LocalDate.now().minusDays(45),
                amountEarnedCentAmount = 2700
            ),
            testDividendsAndInterestsEarned(
                shareCode = shareCode,
                date = LocalDate.now().minusDays(15),
                amountEarnedCentAmount = 4800
            ),
        )

        val uriBuilder = UriComponentsBuilder.fromUriString("/reports/profitability")
            .queryParam("shareCode", shareCode)
            .queryParam("periodStart", periodStart)
            .queryParam("periodEnd", periodEnd)

        // when
        sharesPurchases.forEach {
            testRestTemplate.postForEntity<Any>("/shares_purchased/${UUID.randomUUID()}", it)
        }
        dividendsAndInterestsEarned.forEach {
            testRestTemplate.postForEntity<Any>("/dividends_and_interests_earned/${UUID.randomUUID()}", it)
        }
        val response = testRestTemplate.getForEntity<TestProfitabilityReport>(uriBuilder.toUriString())

        // then
        assertThat(response.statusCode).isEqualTo(HttpStatusCode.valueOf(200))
        assertThat(response.headers.contentType).isEqualTo(MediaType.valueOf("application/prs.hal-forms+json"))
        assertThat(response.body).isEqualTo(
            TestProfitabilityReport(
                shareCode = shareCode,
                periodStart = periodStart,
                periodEnd = periodEnd,
                totalAmountEarnedCentAmount = 3852,
                totalAmountEarnedCurrency = "BRL",
                profitability = 0.6933744221879815,
                averageProfitability = 0.7168671596801091,
            )
        )
    }

    private fun testSharePurchased(
        shareCode: String = "RECT11",
        date: LocalDate,
        sharePriceCentAmount: Int,
        sharePriceCurrency: String = "BRL",
        quantity: Int,
        amountPaidCentAmount: Int,
        amountPaidCurrency: String = "BRL"
    ) = TestSharePurchased(
        shareCode, date, sharePriceCentAmount, sharePriceCurrency, quantity, amountPaidCentAmount, amountPaidCurrency,
    )

    private fun testDividendsAndInterestsEarned(
        shareCode: String = "RECT11",
        date: LocalDate,
        amountEarnedCentAmount: Int,
        amountEarnedCurrency: String = "BRL"
    ) = TestDividendsAndInterestsEarned(
        shareCode, date, amountEarnedCentAmount, amountEarnedCurrency
    )

    data class TestProfitabilityReport(
        val shareCode: String? = null,
        val periodStart: LocalDate? = null,
        val periodEnd: LocalDate? = null,
        val totalAmountEarnedCentAmount: Int? = null,
        val totalAmountEarnedCurrency: String? = null,
        val profitability: Double? = null,
        val averageProfitability: Double? = null,
    )

    data class TestSharePurchased(
        val shareCode: String? = null,
        val date: LocalDate? = null,
        val sharePriceCentAmount: Int? = null,
        val sharePriceCurrency: String? = null,
        val quantity: Int? = null,
        val amountPaidCentAmount: Int? = null,
        val amountPaidCurrency: String? = null,
    )

    data class TestDividendsAndInterestsEarned(
        val shareCode: String? = null,
        val date: LocalDate? = null,
        val amountEarnedCentAmount: Int? = null,
        val amountEarnedCurrency: String? = null,
    )
}