package manager.rules.controller

import manager.common.rest.BasicRest.Companion.getUserId
import manager.common.rest.exception.NotFoundException
import manager.rules.model.dto.RulesOutput
import manager.rules.model.dto.UpdateRulesDTO
import manager.rules.service.RulesService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/rules")
class RulesController
    @Autowired
    constructor(private val rulesService: RulesService) {
        private val logger = LoggerFactory.getLogger(RulesController::class.java)
        @PostMapping("/default")
        fun createDefaultConfiguration(
            @AuthenticationPrincipal jwt: Jwt,
        ): ResponseEntity<String> {
            logger.info("Create default configuration for user ${jwt.subject}")
            val userId = getUserId(jwt.subject)
            return rulesService.createDefaultConf(userId, jwt.tokenValue)
        }

        @GetMapping("/linting")
        fun getLintingRules(
            @AuthenticationPrincipal jwt: Jwt,
        ): ResponseEntity<RulesOutput> {
            logger.info("Get linting rules for user ${jwt.subject}")
            val rules = rulesService.getLintingRules(jwt.subject, jwt.tokenValue)
            return ResponseEntity.ok(rules)
        }

        @GetMapping("/formatting")
        fun getFormattingRules(
            @AuthenticationPrincipal jwt: Jwt,
        ): ResponseEntity<RulesOutput> {
            logger.info("Get formatting rules for user ${jwt.subject}")

            val rules = rulesService.getFormattingRules(jwt.subject, jwt.tokenValue)
            return ResponseEntity.ok(rules)
        }

        @PutMapping
        fun updateRulesByType(
            @AuthenticationPrincipal jwt: Jwt,
            @RequestBody updateRulesDTO: UpdateRulesDTO,
        ): ResponseEntity<String> {
            try{
                logger.info("Update ${updateRulesDTO.type} rules for user ${jwt.subject}. \n Rules: ${updateRulesDTO.rules}")
                this.rulesService.updateRules(updateRulesDTO, getUserId(jwt.subject), jwt.tokenValue)
                return ResponseEntity.ok("")
            } catch(e: NotFoundException){
                logger.warn(e.message)
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
            }
        }
    }
