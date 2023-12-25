package dev.martins.marcio.stockmgmt

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class StockMgmtApplication

fun main(args: Array<String>) {
    runApplication<StockMgmtApplication>(*args)
}