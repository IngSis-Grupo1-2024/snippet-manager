package manager.rules.service

import manager.rules.dto.RulesDTO
import manager.rules.integration.configuration.SnippetConf
import manager.rules.model.dto.RulesOutput
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.oauth2.server.servlet.OAuth2AuthorizationServerProperties.Token
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class RulesService
    @Autowired
    constructor(
        private val snippetConf: SnippetConf
    ) {
        fun createDefaultConf(userId: String, token: String) : ResponseEntity<String> {
            return snippetConf.createDefaultConf(userId, token)
        }

    fun getLintingRules(userId: String, tokenValue: String): ResponseEntity<RulesOutput> {
        return snippetConf.getRules(userId, tokenValue, "LINTING")
    }
    fun getFormattingRules(userId: String, tokenValue: String): ResponseEntity<RulesOutput> {
        return snippetConf.getRules(userId, tokenValue, "FORMATTING")
    }
}