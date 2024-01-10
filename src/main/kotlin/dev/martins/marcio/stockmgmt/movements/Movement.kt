package dev.martins.marcio.stockmgmt.movements

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import java.time.LocalDate
import java.util.UUID

@Entity(name = "movements")
data class Movement(
    @Id
    @JsonIgnore
    @Column(name = "movement_id")
    var id: UUID? = null,
    val date: LocalDate,
    @Enumerated(EnumType.STRING)
    var type: Type,
    val shareCode: String,
    val brokerage: String,
    val quantity: Int,
    val unitPriceCentAmount: Int,
    val transactionValueCentAmount: Int,
    val currency: String,
) {
    enum class Type {
        TRANSFER, DIVIDENDS, REVENUE,
    }
}