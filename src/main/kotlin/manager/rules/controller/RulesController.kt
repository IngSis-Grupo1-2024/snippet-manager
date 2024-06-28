package manager.rules.controller

import manager.common.rest.BasicRest.Companion.getUserId
import manager.rules.service.RulesService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/rules")
class RulesController
    @Autowired
    constructor(private val rulesService: RulesService) {
        @PostMapping("/default")
        fun createDefaultConfiguration(
            @AuthenticationPrincipal jwt: Jwt,
        ): ResponseEntity<String> {
            val userId = getUserId(jwt.subject)
            return rulesService.createDefaultConf(userId, jwt.tokenValue)
        }
    }
