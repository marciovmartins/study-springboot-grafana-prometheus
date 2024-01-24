package dev.martins.marcio.stockmgmt

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.boot.test.web.client.postForEntity
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatusCode
import org.springframework.http.MediaType
import org.springframework.web.util.UriComponentsBuilder
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class ReportsIT {
    @Autowired
    private lateinit var testRestTemplate: TestRestTemplate

    private val chars = ('A'..'Z') + ('a'..'z') + ('0'..'9')

    @Test
    fun `generate profitability report`() {
        // given
        val shareCode = List(10) { chars.random() }.joinToString("") // HCTR11
        val periodStart = LocalDate.of(2021, 4, 28)
        val periodEnd = LocalDate.of(2021, 7, 15)

        val sharesPurchases = listOf(
            testMovement(
                type = "Transferência - Liquidação",
                date = LocalDate.of(2021, 4, 28),
                shareCode = shareCode,
                quantity = 3,
                unitPriceCentAmount = 14700,
                transactionValueCentAmount = 44100,
            ),
            testMovement(
                type = "Transferência - Liquidação",
                date = LocalDate.of(2021, 5, 19),
                shareCode = shareCode,
                quantity = 1,
                unitPriceCentAmount = 13500,
                transactionValueCentAmount = 13500,
            ),
            testMovement(
                type = "Transferência - Liquidação",
                date = LocalDate.of(2021, 6, 10),
                shareCode = shareCode,
                quantity = 14,
                unitPriceCentAmount = 13946,
                transactionValueCentAmount = 195244,
            ),
            testMovement(
                type = "Transferência - Liquidação",
                date = LocalDate.of(2021, 7, 2),
                shareCode = shareCode,
                quantity = 27,
                unitPriceCentAmount = 13260,
                transactionValueCentAmount = 358020,
            ),
        )
        val earnings = listOf(
            testMovement(
                type = "Rendimento",
                date = LocalDate.of(2021, 5, 14),
                shareCode = shareCode,
                quantity = 3,
                unitPriceCentAmount = 200,
                transactionValueCentAmount = 600,
            ),
            testMovement(
                type = "Rendimento",
                date = LocalDate.of(2021, 6, 15),
                shareCode = shareCode,
                quantity = 18,
                unitPriceCentAmount = 190,
                transactionValueCentAmount = 3420,
            ),
            testMovement(
                type = "Dividendo",
                date = LocalDate.of(2021, 6, 15),
                shareCode = shareCode,
                quantity = 1,
                unitPriceCentAmount = 26,
                transactionValueCentAmount = 26,
            ),
            testMovement(
                type = "Rendimento",
                date = LocalDate.of(2021, 7, 15),
                shareCode = shareCode,
                quantity = 46,
                unitPriceCentAmount = 170,
                transactionValueCentAmount = 7820,
            ),
            testMovement(
                type = "Juros Sobre Capital Próprio",
                date = LocalDate.of(2021, 7, 15),
                shareCode = shareCode,
                quantity = 61,
                unitPriceCentAmount = 150,
                transactionValueCentAmount = 9150,
            ),
            testMovement(
                type = "Rendimento",
                date = LocalDate.of(2021, 8, 13),
                shareCode = shareCode,
                quantity = 46,
                unitPriceCentAmount = 150,
                transactionValueCentAmount = 6900,
            ),
        )

        val headers = HttpHeaders().apply { contentType = MediaType.valueOf("text/csv") }
        sharesPurchases.let {
            testRestTemplate.postForEntity<Any>("/imports/b3", HttpEntity(it.toCSV(), headers))
        }
        earnings.let {
            testRestTemplate.postForEntity<Any>("/imports/b3", HttpEntity(it.toCSV(), headers))
        }

        val uriBuilder = UriComponentsBuilder.fromUriString("/reports/profitability")
            .queryParam("shareCode", shareCode)
            .queryParam("periodStart", periodStart)
            .queryParam("periodEnd", periodEnd)

        // when
        val response = testRestTemplate.getForEntity<TestProfitabilityReport>(uriBuilder.toUriString())

        // then
        assertThat(response.statusCode).isEqualTo(HttpStatusCode.valueOf(200))
        assertThat(response.headers.contentType).isEqualTo(MediaType.valueOf("application/prs.hal-forms+json"))
        assertThat(response.body).isNotNull
        assertThat(response.body!!.shareCode).isEqualTo(shareCode)
        assertThat(response.body!!.periodStart).isEqualTo(periodStart)
        assertThat(response.body!!.periodEnd).isEqualTo(periodEnd)
        assertThat(response.body!!.score).isEqualTo(0.010283020360380313 + 1.2801540113675056 + 1.4978784148353808)
        assertThat(response.body!!.scoreAverage).isEqualTo(0.010283020360380313 + 1.3311036357425612 + 1.4978784148353808)
        assertThat(response.body!!.totalAmountEarnedCentAmount).isEqualTo(21016)
        assertThat(response.body!!.totalDividendsCentAmount).isEqualTo(26)
        assertThat(response.body!!.totalRevenueCentAmount).isEqualTo(11840)
        assertThat(response.body!!.totalInterestOnEquityCentAmount).isEqualTo(9150)
        assertThat(response.body!!.currency).isEqualTo("BRL")
        assertThat(response.body!!.dividendsProfitability).isEqualTo(0.010283020360380313)
        assertThat(response.body!!.dividendsAverageProfitability).isEqualTo(0.010283020360380313)
        assertThat(response.body!!.revenueProfitability).isEqualTo(1.2801540113675056)
        assertThat(response.body!!.revenueAverageProfitability).isEqualTo(1.3311036357425612)
        assertThat(response.body!!.interestOnEquityProfitability).isEqualTo(1.4978784148353808)
        assertThat(response.body!!.interestOnEquityAverageProfitability).isEqualTo(1.4978784148353808)
    }

    @Test
    fun `generate profitability report with irregular dividends`() {
        // given
        val shareCode = List(10) { chars.random() }.joinToString("") // BBSE3
        val periodStart = LocalDate.of(2020, 4, 8)
        val periodEnd = LocalDate.of(2020, 8, 24)

        val sharesPurchases = listOf(
            testMovement(
                "Transferência - Liquidação",
                date = LocalDate.of(2020, 4, 8),
                shareCode = shareCode,
                quantity = 83,
                unitPriceCentAmount = 2425,
                transactionValueCentAmount = 201275,
            ),
            testMovement(
                "Transferência - Liquidação",
                date = LocalDate.of(2020, 5, 7),
                shareCode = shareCode,
                quantity = 1,
                unitPriceCentAmount = 2515,
                transactionValueCentAmount = 2515,
            ),
        )
        val dividendsAndInterestsEarned = listOf(
            testMovement(
                "Dividendo",
                date = LocalDate.of(2020, 8, 24),
                shareCode = shareCode,
                quantity = 84,
                unitPriceCentAmount = 88,
                transactionValueCentAmount = 7352,
            ),
        )

        val headers = HttpHeaders().apply { contentType = MediaType.valueOf("text/csv") }
        sharesPurchases.let {
            testRestTemplate.postForEntity<Any>("/imports/b3", HttpEntity(it.toCSV(), headers))
        }
        dividendsAndInterestsEarned.let {
            testRestTemplate.postForEntity<Any>("/imports/b3", HttpEntity(it.toCSV(), headers))
        }

        val uriBuilder = UriComponentsBuilder.fromUriString("/reports/profitability")
            .queryParam("shareCode", shareCode)
            .queryParam("periodStart", periodStart)
            .queryParam("periodEnd", periodEnd)

        // when
        val response = testRestTemplate.getForEntity<TestProfitabilityReport>(uriBuilder.toUriString())

        // then
        assertThat(response.statusCode).isEqualTo(HttpStatusCode.valueOf(200))
        assertThat(response.headers.contentType).isEqualTo(MediaType.valueOf("application/prs.hal-forms+json"))
        assertThat(response.body).isNotNull
        assertThat(response.body!!.shareCode).isEqualTo(shareCode)
        assertThat(response.body!!.periodStart).isEqualTo(periodStart)
        assertThat(response.body!!.periodEnd).isEqualTo(periodEnd)
        assertThat(response.body!!.totalAmountEarnedCentAmount).isEqualTo(7352)
        assertThat(response.body!!.totalDividendsCentAmount).isEqualTo(7352)
        assertThat(response.body!!.totalRevenueCentAmount).isEqualTo(0)
        assertThat(response.body!!.currency).isEqualTo("BRL")
        assertThat(response.body!!.dividendsProfitability).isEqualTo(1.2025451036197392)
        assertThat(response.body!!.dividendsAverageProfitability).isEqualTo(1.2025451036197392)
        assertThat(response.body!!.revenueProfitability).isEqualTo(0.0)
        assertThat(response.body!!.revenueAverageProfitability).isEqualTo(0.0)
    }

    private fun testMovement(
        type: String?,
        date: LocalDate?,
        shareCode: String?,
        brokerage: String = "CLEAR CORRETORA - GRUPO XP",
        quantity: Int?,
        unitPriceCentAmount: Int?,
        transactionValueCentAmount: Int?,
        currency: String = "BRL"
    ) = TestMovement(
        type,
        date,
        shareCode,
        brokerage,
        quantity,
        unitPriceCentAmount,
        transactionValueCentAmount,
        currency,
    )

    data class TestProfitabilityReport(
        val shareCode: String? = null,
        val periodStart: LocalDate? = null,
        val periodEnd: LocalDate? = null,
        val score: Double? = null,
        val scoreAverage: Double? = null,
        val totalAmountEarnedCentAmount: Int? = null,
        val totalDividendsCentAmount: Int? = null,
        val totalRevenueCentAmount: Int? = null,
        val totalInterestOnEquityCentAmount: Int? = null,
        val currency: String? = null,
        val dividendsProfitability: Double? = null,
        val dividendsAverageProfitability: Double? = null,
        val revenueProfitability: Double? = null,
        val revenueAverageProfitability: Double? = null,
        val interestOnEquityProfitability: Double? = null,
        val interestOnEquityAverageProfitability: Double? = null,
    )

    data class TestMovement(
        val type: String? = null,
        val date: LocalDate? = null,
        val shareCode: String? = null,
        val brokerage: String? = null,
        val quantity: Int? = null,
        val unitPriceCentAmount: Int? = null,
        val transactionValueCentAmount: Int? = null,
        val currency: String? = null,
    )

    private fun List<TestMovement>.toCSV(): String =
        "Entrada/Saída;Data;Movimentação;Produto;Instituição;Quantidade; Preço unitário ; Valor da Operação\n" +
                this.joinToString(separator = "\n") {
                    val operation = it.transactionValueCentAmount?.let { tv -> if (tv >= 0) "Credito" else "Debito" }
                    val dateBR = it.date?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    "$operation;${dateBR};${it.type};${it.shareCode};${it.brokerage};${it.quantity};${it.unitPriceCentAmount};${it.transactionValueCentAmount}"
                }
}