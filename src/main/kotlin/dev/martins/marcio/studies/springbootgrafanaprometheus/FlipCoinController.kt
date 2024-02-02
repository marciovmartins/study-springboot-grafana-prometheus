package dev.martins.marcio.studies.springbootgrafanaprometheus

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/flipcoin")
class FlipCoinController(
    private val registry: MeterRegistry
) {
    private val options: List<String> = listOf("Heads", "Tails")

    private val flipCoinCounter: Counter = Counter.builder("flipcoin.count")
        .register(registry)

    @GetMapping
    fun handle(): String {
        flipCoinCounter.increment()
        return options.random()
    }
}