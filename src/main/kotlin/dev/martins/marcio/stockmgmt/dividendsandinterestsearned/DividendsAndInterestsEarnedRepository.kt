package dev.martins.marcio.stockmgmt.dividendsandinterestsearned

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.util.UUID

@Repository
interface DividendsAndInterestsEarnedRepository : JpaRepository<DividendsAndInterestsEarned, UUID> {
    fun findByShareCodeAndDateGreaterThanEqualAndDateLessThanEqualOrderByDate(
        shareCode: String,
        periodStart: LocalDate,
        periodEnd: LocalDate
    ): List<DividendsAndInterestsEarned>
}
