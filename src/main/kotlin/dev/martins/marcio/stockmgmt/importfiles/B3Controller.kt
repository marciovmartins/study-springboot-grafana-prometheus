package dev.martins.marcio.stockmgmt.importfiles

import dev.martins.marcio.stockmgmt.RootController
import dev.martins.marcio.stockmgmt.movements.Movement
import dev.martins.marcio.stockmgmt.movements.MovementRepository
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
    private val movementRepository: MovementRepository,
) {
    @PostMapping(value = ["/imports/b3"], consumes = ["text/csv"])
    fun importB3Csv(@RequestBody csvData: String?): CollectionModel<EntityModel<*>> {
        csvData!!
            .split("\n")
            .filter { it.trim() != "" }
            .drop(1)
            .forEach loop@{
                val line = it.split(";")
                val date = line[1].toLocalDate()
                val movementType = when (B3Types.from(line[2]) ?: return@loop) {
                    B3Types.TransferSettlement -> Movement.Type.TRANSFER
                    B3Types.Dividends -> Movement.Type.DIVIDENDS
                    B3Types.Revenue -> Movement.Type.REVENUE
                }
                val product = line[3]
                val brokerage = line[4]
                val quantity = line[5]
                val unitPrice = line[6]
                val transactionValue = line[7]

                movementRepository.save(
                    Movement(
                        id = UUID.randomUUID(),
                        type = movementType,
                        date = date,
                        shareCode = product.split(" ")[0],
                        brokerage = brokerage,
                        quantity = quantity.toInt(),
                        unitPriceCentAmount = unitPrice.toCentAmount(),
                        transactionValueCentAmount = transactionValue.toCentAmount(),
                        currency = "BRL",
                    )
                )
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

    private fun String.toCentAmount(): Int {
        return try {
            this
                .replace("R$", "")
                .replace(".", "")
                .replace(",", "")
                .trim()
                .toInt()
        } catch (e: NumberFormatException) {
            0
        }
    }
}