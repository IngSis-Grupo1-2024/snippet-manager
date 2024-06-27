package manager.rules.controller

import manager.common.rest.exception.ErrorOutput
import manager.common.rest.dto.Output
import manager.common.rest.BasicRest.Companion.getUserId
import manager.rules.model.dto.RulesOutput
import manager.rules.service.RulesService
import org.springframework.beans.factory.annotation.Autowired

import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.HttpClientErrorException

@RestController
@RequestMapping("/rules")
class RulesController
    @Autowired
    constructor(private val rulesService: RulesService){

    @PostMapping("/default")
    fun createDefaultConfiguration(@AuthenticationPrincipal jwt: Jwt): ResponseEntity<String> {
        val userId=getUserId(jwt.subject)
        return rulesService.createDefaultConf(userId, jwt.tokenValue)
    }
}