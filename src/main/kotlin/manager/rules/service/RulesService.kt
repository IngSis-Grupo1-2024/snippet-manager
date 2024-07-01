package manager.rules.service

import com.example.redisevents.LintRequest
import com.example.redisevents.LintRulesInput
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import manager.manager.model.dto.SnippetListDto
import manager.manager.model.enums.SnippetLanguage
import manager.manager.service.ManagerService
import manager.redis.producer.LintProducer
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
        private val snippetManagerService: ManagerService,
        private val lintProducer: LintProducer,
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
            if (updateRulesDTO.type == "LINTING") {
                val snippets: SnippetListDto = snippetManagerService.getSnippetDescriptors(userId, tokenValue)
                for (snippet in snippets) {
                    GlobalScope.launch {
                        lintProducer.publishEvent(
                            LintRequest(
                                snippet.content,
                                snippet.language.name,
                                "v1",
                                rulesParser(getLintingRules(userId, tokenValue)),
                                listOf("hello"),
                                snippet.id.toString(),
                                userId,
                            ),
                        )
                    }
                }
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

        private fun rulesParser(rulesOutput: RulesOutput): List<LintRulesInput> {
            return rulesOutput.rules.map { rule ->
                LintRulesInput(
                    rule.parent,
                    rule.isActive,
                    rule.name == "expression",
                    rule.name == "identifier",
                    rule.name == "literal",
                    if (rule.name == "snake_case" || rule.name == "") "snake case" else "camel case",
                )
            }
        }
    }
