package dev.martins.marcio.stockmgmt.dividendsandinterestsearned

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.time.LocalDate
import java.util.UUID

@Entity(name = "dividends_and_interests_earned")
data class DividendsAndInterestsEarned(
    @Id
    @JsonIgnore
    @Column(name = "dividends_and_interests_earned_id")
    val id: UUID? = null,
    val shareCode: String,
    val date: LocalDate,
    @Column(name = "amount_earned_cent_amount")
    val amountEarnedCentAmount: Int,
    @Column(name = "amount_earned_currency")
    val amountEarnedCurrency: String,
)
