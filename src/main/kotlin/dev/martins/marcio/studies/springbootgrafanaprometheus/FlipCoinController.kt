package dev.martins.marcio.studies.springbootgrafanaprometheus

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/flipCoin")
class FlipCoinController(
    registry: MeterRegistry
) {
    private val options: List<String> = listOf("Heads", "Tails")

    private val flipCoinCounter: Counter = Counter.builder("flip_coin.count")
        .register(registry)

    private val headsCounter: Counter = Counter.builder("flip_coin.odds")
        .tag("side", "Heads")
        .register(registry)

    private val tailsCounter: Counter = Counter.builder("flip_coin.odds")
        .tag("side", "Tails")
        .register(registry)

    @GetMapping
    fun handle(): String {
        flipCoinCounter.increment()
        val result = options.random()
        when (result) {
            "Heads" -> headsCounter.increment()
            "Tails" -> tailsCounter.increment()
        }
        return result
    }
}