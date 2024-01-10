package dev.martins.marcio.stockmgmt.movements

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.util.UUID

@Repository
interface MovementRepository : JpaRepository<Movement, UUID> {
    @Suppress("SpringDataRepositoryMethodParametersInspection")
    fun findByShareCodeAndDateLessThanEqualAndTypeInOrderByDate(
        shareCode: String,
        periodEnd: LocalDate,
        vararg type: Movement.Type,
    ): List<Movement>
}