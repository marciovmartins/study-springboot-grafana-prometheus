package dev.martins.marcio.stockmgmt.sharespurchased

import dev.martins.marcio.stockmgmt.RootController
import org.springframework.data.repository.findByIdOrNull
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.mediatype.Affordances
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
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
@RequestMapping(value = ["/shares_purchased"], produces = ["application/prs.hal-forms+json"])
class SharesPurchasedController(
    private val sharePurchasedRepository: SharePurchasedRepository,
) {
    @GetMapping
    fun all(): CollectionModel<EntityModel<*>> = CollectionModel.of(
        emptyList(),
        linkTo(methodOn(SharesPurchasedController::class.java).all())
            .withSelfRel(),
        linkTo(methodOn(RootController::class.java).index())
            .withRel("root"),
        Affordances.of(
            linkTo(methodOn(SharesPurchasedController::class.java).add(UUID.randomUUID(), null)).withRel("add")
        )
            .afford(HttpMethod.POST)
            .withInputAndOutput(SharePurchased::class.java)
            .withName("add")
            .toLink()
    )

    @PostMapping("/{id}")
    fun add(
        @PathVariable id: UUID,
        @RequestBody sharePurchased: SharePurchased?,
    ): ResponseEntity<*> {
        val sharePurchasedExisting = sharePurchasedRepository.findByIdOrNull(id)
        if (sharePurchasedExisting != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null)
        }

        sharePurchased!!.id = id
        sharePurchasedRepository.save(sharePurchased)

        val location = URI.create("about:blank")
        return ResponseEntity.created(location).body(sharePurchasedEntityModel(sharePurchased))
    }

    private fun sharePurchasedEntityModel(sharePurchased: SharePurchased) = EntityModel.of(
        sharePurchased,
        linkTo(methodOn(SharesPurchasedController::class.java).all())
            .withRel("sharesPurchased"),
    )
}