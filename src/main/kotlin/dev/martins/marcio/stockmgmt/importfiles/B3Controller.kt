package dev.martins.marcio.stockmgmt.importfiles

import dev.martins.marcio.stockmgmt.RootController
import dev.martins.marcio.stockmgmt.dividendsandinterestsearned.DividendsAndInterestsEarned
import dev.martins.marcio.stockmgmt.dividendsandinterestsearned.DividendsAndInterestsEarnedRepository
import dev.martins.marcio.stockmgmt.sharespurchased.SharePurchased
import dev.martins.marcio.stockmgmt.sharespurchased.SharePurchasedRepository
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.server.mvc.andAffordances
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import java.util.UUID

@RestController
class B3Controller(
    private val sharePurchasedRepository: SharePurchasedRepository,
    private val dividendsAndInterestsEarnedRepository: DividendsAndInterestsEarnedRepository,
) {
    @PostMapping(value = ["/imports/b3"], consumes = ["text/csv"])
    fun importB3Csv(@RequestBody csvData: String?): CollectionModel<EntityModel<*>> {
        csvData!!
            .split("\n")
            .filter { it.trim() != "" }
            .forEach loop@{
                val line = it.split(";")
                println("### $line ###")
                val type = B3Types.from(line[2]) ?: return@loop
                val date = line[1].toLocalDate()
                val movement = line[3]
                val quantity = line[5]
                val unitPrice = line[6]
                val operationValue = line[7]

                when (type) {
                    B3Types.TransferSettlement ->
                        sharePurchasedRepository.save(
                            SharePurchased(
                                id = UUID.randomUUID(),
                                shareCode = movement.split(" ")[0],
                                date = date,
                                sharePriceCentAmount = unitPrice.toCentAmount(),
                                sharePriceCurrency = "BRL",
                                quantity = quantity.toInt(),
                                amountPaidCentAmount = operationValue.toCentAmount(),
                                amountPaidCurrency = "BRL",
                            )
                        )

                    B3Types.Dividends,
                    B3Types.Income ->
                        dividendsAndInterestsEarnedRepository.save(
                            DividendsAndInterestsEarned(
                                id = UUID.randomUUID(),
                                shareCode = movement.split(" ")[0],
                                date = date,
                                amountEarnedCentAmount = operationValue.toCentAmount(),
                                amountEarnedCurrency = "BRL",
                            )
                        )
                }
            }

        return CollectionModel.of(
            emptyList(),
            linkTo<RootController> { index() }.withRel("root"),
            linkTo<B3Controller> { importB3Csv(null) }
                .withRel("importB3Csv")
                .andAffordances { afford<B3Controller> { importB3Csv(null) } }
        )
    }

    private fun String.toLocalDate(): LocalDate {
        val date = this.split("/")
        val year = ("20" + date[2]).takeLast(4).toInt()
        val month = date[1].toInt()
        val dayOfMonth = date[0].toInt()
        return LocalDate.of(year, month, dayOfMonth)
    }

    private fun String.toCentAmount(): Int = this
        .replace("R$", "")
        .replace(".", "")
        .replace(",", "")
        .trim()
        .toInt()
}