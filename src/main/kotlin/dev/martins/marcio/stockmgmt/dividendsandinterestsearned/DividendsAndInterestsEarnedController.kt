package dev.martins.marcio.stockmgmt.dividendsandinterestsearned

import dev.martins.marcio.stockmgmt.RootController
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.mediatype.Affordances
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI
import java.util.UUID

@RestController
@RequestMapping(value = ["/dividends_and_interests_earned"], produces = ["application/prs.hal-forms+json"])
class DividendsAndInterestsEarnedController(
    private val dividendsAndInterestsEarnedRepository: DividendsAndInterestsEarnedRepository
) {
    @GetMapping
    fun all(): ResponseEntity<CollectionModel<DividendsAndInterestsEarned>> = ResponseEntity.ok(
        CollectionModel.of(
            emptyList(),
            linkTo(methodOn(DividendsAndInterestsEarnedController::class.java).all())
                .withSelfRel(),
            linkTo(methodOn(RootController::class.java).index())
                .withRel("root"),
            Affordances.of(
                linkTo(methodOn(DividendsAndInterestsEarnedController::class.java).add(UUID.randomUUID(), null))
                    .withRel("add")
            )
                .afford(HttpMethod.POST)
                .withInputAndOutput(DividendsAndInterestsEarned::class.java)
                .withName("add")
                .toLink()
        )
    )

    @PostMapping("/{id}")
    fun add(
        @PathVariable id: UUID,
        @RequestBody dividendsAndInterestsEarned: DividendsAndInterestsEarned?,
    ): ResponseEntity<EntityModel<*>> {
        val newEntity = dividendsAndInterestsEarnedRepository.save(dividendsAndInterestsEarned!!.copy(id = id))
        return ResponseEntity.created(URI.create("about:blank")).body(entityModel(newEntity))
    }

    private fun entityModel(dividendsAndInterestsEarned: DividendsAndInterestsEarned): EntityModel<*> {
        return EntityModel.of(
            dividendsAndInterestsEarned,
            linkTo(methodOn(DividendsAndInterestsEarnedController::class.java).all())
                .withRel("dividendsAndInterestsEarned")
        )
    }
}