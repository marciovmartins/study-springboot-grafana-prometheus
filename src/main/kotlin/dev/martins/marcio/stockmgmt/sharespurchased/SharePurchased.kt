package dev.martins.marcio.stockmgmt.sharespurchased

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.time.LocalDate
import java.util.UUID

@Entity(name = "shares_purchased")
data class SharePurchased(
    @Id
    @JsonIgnore
    @Column(name = "share_purchased_id")
    var id: UUID? = null,
    val shareCode: String,
    val date: LocalDate,
    @Column(name = "share_price_cent_amount")
    val sharePriceCentAmount: Int,
    @Column(name = "share_price_currency")
    val sharePriceCurrency: String,
    val quantity: Int,
    @Column(name = "amount_paid_cent_amount")
    val amountPaidCentAmount: Int,
    @Column(name = "amount_paid_currency")
    val amountPaidCurrency: String,
)
