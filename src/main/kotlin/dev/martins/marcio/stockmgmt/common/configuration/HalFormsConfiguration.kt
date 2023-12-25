package dev.martins.marcio.stockmgmt.common.configuration

import org.springframework.context.annotation.Configuration
import org.springframework.hateoas.config.EnableHypermediaSupport
import org.springframework.hateoas.config.EnableHypermediaSupport.HypermediaType

@Configuration
@EnableHypermediaSupport(type = [HypermediaType.HAL_FORMS])
class HalFormsConfiguration