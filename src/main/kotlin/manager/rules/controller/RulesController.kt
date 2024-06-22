package manager.rules.controller

import manager.rest.exception.ErrorOutput
import manager.rest.dto.Output
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
        val userId=jwt.subject.substring(6)
        return rulesService.createDefaultConf(userId, jwt.tokenValue)
    }
    @GetMapping("/linting")
    fun getLintingRules(@AuthenticationPrincipal jwt: Jwt): ResponseEntity<out Output> {
        val getRules = { userId: String ->
            ResponseEntity.ok(rulesService.getLintingRules(userId, jwt.tokenValue))
        }
        return getResponse(jwt, getRules)
    }
    @GetMapping("/formatting")
    fun getFormattingRules(@AuthenticationPrincipal jwt: Jwt): ResponseEntity<out Output> {
        val getRules = { userId: String ->
            ResponseEntity.ok(rulesService.getFormattingRules(userId, jwt.tokenValue))
        }
        return getResponse(jwt, getRules)
    }

    private fun getResponse(
        jwt: Jwt,
        getRules: (String) -> ResponseEntity<RulesOutput>
    ): ResponseEntity<out Output> {
        try {
            val userId = jwt.subject.substring(6)
            return getRules(userId)
        } catch (e: HttpClientErrorException) {
            val response = ResponseEntity.status(e.statusCode)
            return response.body(ErrorOutput(String(e.responseBodyAsByteArray)))
        }
    }
}