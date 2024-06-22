package manager.rules.controller

import manager.rules.dto.RulesDTO
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
    fun getLintingRules(@AuthenticationPrincipal jwt: Jwt): ResponseEntity<RulesOutput> {
        val userId=jwt.subject.substring(6)
        return rulesService.getLintingRules(userId, jwt.tokenValue)
    }
    @GetMapping("/formatting")
    fun getFormattingRules(@AuthenticationPrincipal jwt: Jwt): ResponseEntity<RulesOutput> {
        val userId=jwt.subject.substring(6)
        return rulesService.getFormattingRules(userId, jwt.tokenValue)
    }
}