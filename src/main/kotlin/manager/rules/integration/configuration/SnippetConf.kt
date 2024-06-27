package manager.rules.integration.configuration

import manager.rules.model.dto.RulesOutput
import org.springframework.http.ResponseEntity

interface SnippetConf {
    fun createDefaultConf(userId: String, token: String, language: String): ResponseEntity<String>
}