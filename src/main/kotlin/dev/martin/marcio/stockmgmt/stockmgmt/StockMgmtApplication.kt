package dev.martin.marcio.stockmgmt.stockmgmt

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import org.openqa.selenium.edge.EdgeDriver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.boot.runApplication
import org.springframework.context.event.ContextClosedEvent
import org.springframework.context.event.EventListener
import org.springframework.data.jpa.repository.JpaRepository
import java.math.BigDecimal
import java.time.Instant

@SpringBootApplication
class StockMgmtApplication {
    @Autowired
    private lateinit var fiiRepository: FiiRepository

    val driver = EdgeDriver()

    @EventListener(ApplicationReadyEvent::class)
    fun fetchAllFiis() {
        val page = System.getenv("PAGE") ?: 1
        val now = Instant.now()

        driver.get("https://investidor10.com.br/fiis/?page=$page")
        Thread.sleep(2000)

        var hasNext: Boolean
        var isFinished: Boolean
        do {

            val actionsFiis = driver.findElements(By.cssSelector("div.actions.fii"))

            actionsFiis.forEach {
                fetchFii(it, now, driver)
            }

            val nextLink = driver.findElement(
                By.cssSelector("div.template-front-fii-list li.pagination-item.next a.pagination-link")
            )
            hasNext = nextLink.isDisplayed
            val href = nextLink.getAttribute("href")
            isFinished = hasNext && !href.endsWith("#")
            if (isFinished) {
                driver.executeScript("arguments[0].scrollIntoView();", nextLink)
                Thread.sleep(1000)
                driver.executeScript("window.scrollBy(0, -250)")
                Thread.sleep(1000)
                nextLink.safeClick(driver)
                Thread.sleep(1000)
            }

        } while (isFinished)
    }

    private fun fetchFii(it: WebElement, now: Instant, driver: EdgeDriver) {
        driver.executeScript("arguments[0].scrollIntoView();", it)
        Thread.sleep(1000)

        it.safeClick(driver)
        Thread.sleep(1000)
        val fii = driver.findElement(By.cssSelector("div.name-ticker h1")).text
        val cotacao = driver.findElement(By.cssSelector("div.cotacao span.value")).text
            .replace("R$ ", "")
            .replace(".", "")
            .replace(",", ".")
        val dy = driver.findElement(By.cssSelector("div.dy ._card-body div span")).text
            .replace("%", "")
            .replace(".", "")
            .replace(",", ".")

        try {
            fiiRepository.save(
                FiiEntity(
                    name = fii,
                    date = now,
                    currentValue = BigDecimal(cotacao),
                    dividendYield = BigDecimal(dy)
                )
            )
        } catch (e: Throwable) {
            try {
                fiiRepository.save(
                    FiiEntity(
                        name = fii,
                        date = now,
                        currentValue = BigDecimal(cotacao),
                        dividendYield = null
                    )
                )
            } catch (e: Throwable) {
                println("ERROR: fii=$fii, message=${e.message}")
            }
        }

        driver.navigate().back()
        Thread.sleep(1000)
    }

    @EventListener(ContextClosedEvent::class)
    fun shutdown() {
        driver.quit()
    }
}

fun main(args: Array<String>) {
    runApplication<StockMgmtApplication>(*args)
}

interface FiiRepository : JpaRepository<FiiEntity, Long> {
    override fun <S : FiiEntity> save(entity: S): S
}

@Entity(name = "fiis")
data class FiiEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(name = "fii_date")
    val date: Instant,
    @Column(name = "fii_name")
    val name: String,
    val currentValue: BigDecimal,
    val dividendYield: BigDecimal?,
    val createdAt: Instant? = Instant.now(),
)

fun WebElement.safeClick(driver: EdgeDriver) {
    val modals = driver.findElements(By.cssSelector("button.modal-close"))
    modals.forEach {
        if (it.isDisplayed) {
            it.click()
            Thread.sleep(1000)
        }
    }
    try {
        Thread.sleep(1000)
        this.click()
    } catch (e: Throwable) {
        println("Click problem: ${e.message}")
    }
}