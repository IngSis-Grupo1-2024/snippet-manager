package manager.rules.controller

import manager.common.rest.BasicRest.Companion.getUserId
import manager.rules.model.dto.RulesOutput
import manager.rules.model.dto.UpdateRulesDTO
import manager.rules.service.RulesService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*

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

        @GetMapping("/linting")
        fun getLintingRules(
            @AuthenticationPrincipal jwt: Jwt,
        ): ResponseEntity<RulesOutput> {
            val rules = rulesService.getLintingRules(jwt.subject, jwt.tokenValue)
            return ResponseEntity.ok(rules)
        }

        @GetMapping("/formatting")
        fun getFormattingRules(
            @AuthenticationPrincipal jwt: Jwt,
        ): ResponseEntity<RulesOutput> {
            val rules = rulesService.getFormattingRules(jwt.subject, jwt.tokenValue)
            return ResponseEntity.ok(rules)
        }

        @PutMapping
        fun updateLintingRules(
            @AuthenticationPrincipal jwt: Jwt,
            @RequestBody updateRulesDTO: UpdateRulesDTO,
        ): ResponseEntity<String> {
            this.rulesService.updateRules(updateRulesDTO, getUserId(jwt.subject), jwt.tokenValue)
            return ResponseEntity.ok("")
        }
    }
