package manager.rules.service

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import manager.bucket.BucketAPI
import manager.common.rest.exception.NotFoundException
import manager.manager.model.enums.SnippetLanguage
import manager.manager.repository.UserRepository
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
        private val lintProducer: LintProducer,
        private val userRepository: UserRepository,
        private val bucketAPI: BucketAPI,
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
                val user = userRepository.findByUserId(userId) ?: throw NotFoundException("There is no user with id $userId")
                user.snippet.forEach { snippet ->
                    GlobalScope.launch {
                        val content = bucketAPI.getSnippet(snippet.id.toString())
                        lintProducer.publishEvent(
                            content,
                            snippet,
                            getLintingRules(userId, tokenValue),
                            "v1",
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
    }
