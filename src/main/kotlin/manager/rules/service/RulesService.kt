package manager.rules.service

import manager.rules.integration.configuration.SnippetConf
import manager.rules.model.dto.RulesOutput
import org.springframework.beans.factory.annotation.Autowired
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

    fun getLintingRules(userId: String, tokenValue: String): RulesOutput {
        return snippetConf.getRules(userId, tokenValue, "LINTING")
    }
    fun getFormattingRules(userId: String, tokenValue: String): RulesOutput {
        return snippetConf.getRules(userId, tokenValue, "FORMATTING")
    }
}