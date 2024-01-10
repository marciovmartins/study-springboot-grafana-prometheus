package dev.martins.marcio.stockmgmt

import dev.martins.marcio.stockmgmt.importfiles.B3Controller
import dev.martins.marcio.stockmgmt.reports.ReportsController
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.server.mvc.andAffordances
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = ["/"], produces = ["application/prs.hal-forms+json"])
class RootController {
    @GetMapping
    fun index(): ResponseEntity<CollectionModel<Any>> = ResponseEntity.ok(
        CollectionModel.of(
            emptyList(),
            linkTo<ReportsController> { profitability(null, null, null) }
                .withRel("profitability"),
            linkTo<B3Controller> { importB3Csv(null) }
                .withRel("importB3Csv")
                .andAffordances { afford<B3Controller> { importB3Csv(null) } }
        )
    )
}