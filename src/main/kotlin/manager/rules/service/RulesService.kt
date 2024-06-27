package manager.rules.service

import manager.manager.model.enums.SnippetLanguage
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
            SnippetLanguage.entries.forEach { snippetLanguage ->
                snippetConf.createDefaultConf(userId, token, snippetLanguage.toString())
            }
            return ResponseEntity.ok("")
        }
}