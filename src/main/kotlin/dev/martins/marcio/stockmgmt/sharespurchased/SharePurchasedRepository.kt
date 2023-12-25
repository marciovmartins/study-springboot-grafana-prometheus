package dev.martins.marcio.stockmgmt.sharespurchased

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.util.UUID

@Repository
interface SharePurchasedRepository : JpaRepository<SharePurchased, UUID> {
    fun findByShareCodeAndDateGreaterThanEqualAndDateLessThanEqualOrderByDate(
        shareCode: String,
        periodStart: LocalDate,
        periodEnd: LocalDate
    ): List<SharePurchased>
}