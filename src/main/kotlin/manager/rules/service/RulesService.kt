package manager.rules.service

import manager.manager.model.enums.SnippetLanguage
import manager.rules.dto.RulesDTO
import manager.rules.integration.configuration.SnippetConf
import manager.rules.model.dto.RulesOutput
import manager.rules.model.dto.UpdateRulesDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class RulesService
    @Autowired
    constructor(
        private val snippetConf: SnippetConf,
    ) {
        fun createDefaultConf(
            userId: String,
            token: String,
        ): ResponseEntity<String> {
            SnippetLanguage.entries.forEach { snippetLanguage ->
                snippetConf.createDefaultConf(userId, token, snippetLanguage.toString())
            }
            return ResponseEntity.ok("")
        }

        fun updateRules(
            updateRulesDTO: UpdateRulesDTO,
            userId: String,
            tokenValue: String,
        ) {
            snippetConf.updateRules(updateRulesDTO, userId, tokenValue)
            if (updateRulesDTO.type == "LINTING")
                {
                    TODO("call lint reddis")
                }
        }

        fun getLintingRules(
            userId: String,
            tokenValue: String,
        ): RulesOutput {
            val rules = snippetConf.getRules(userId, tokenValue, "LINTING")
            return RulesOutput(
                rules.rules.map { rule ->
                    RulesDTO(
                        id = rule.id,
                        name = rule.name + " in " + rule.parent,
                        isActive = rule.isActive,
                        value = rule.value,
                        parent = rule.parent,
                    )
                },
            )
        }

        fun getFormattingRules(
            userId: String,
            tokenValue: String,
        ): RulesOutput {
            return snippetConf.getRules(userId, tokenValue, "FORMATTING")
        }
    }
