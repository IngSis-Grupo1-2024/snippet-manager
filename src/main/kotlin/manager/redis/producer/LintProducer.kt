package manager.redis.producer

import com.example.redisevents.LintRequest
import com.example.redisevents.LintRulesInput
import manager.manager.controller.ManagerController
import manager.manager.model.entity.Snippet
import manager.rules.model.dto.RulesOutput
import org.austral.ingsis.redis.RedisStreamProducer
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component

@Component
class LintProducer
    @Autowired
    constructor(
        @Value("\${manager.redis.stream.request_lint_key}") streamKey: String,
        redis: RedisTemplate<String, String>,
    ) : RedisStreamProducer(streamKey, redis) {
        private val logger = LoggerFactory.getLogger(ManagerController::class.java)

        suspend fun publishEvent(
            snippetContent: String,
            snippet: Snippet,
            rulesOutput: RulesOutput,
            version: String,
        ) {
            val rules: List<LintRulesInput> = rulesParser(rulesOutput)
            val event = getLintRequest(snippetContent, snippet, version, rules)

            logger.info("Publishing event: $event")
            println("Publishing event: $event")
            emit(event)
        }

        private fun getLintRequest(
            snippetContent: String,
            snippet: Snippet,
            version: String,
            rules: List<LintRulesInput>,
        ): LintRequest =
            LintRequest(
                snippetContent,
                snippet.language.toString(),
                version,
                rules,
                listOf(" "),
                snippet.id.toString(),
                snippet.userSnippet.userId,
            )

        private fun rulesParser(rulesOutput: RulesOutput): List<LintRulesInput> {
            val parents = rulesOutput.rules.map { rule -> rule.parent }.distinct()

            val rules = parents.map { parent ->
                if(parent != "identifier_format")
                    getBasicLintRulesInput(parent, rulesOutput)
                else
                    getFormatLintRuleInput(parent, rulesOutput)
            }
            return rules
        }

    private fun getFormatLintRuleInput(
        parent: String,
        rulesOutput: RulesOutput
    ) = LintRulesInput(
        parent,
        true,
        false,
        false,
        false,
        getFormatValue(rulesOutput)
    )

    private fun getBasicLintRulesInput(
        parent: String,
        rulesOutput: RulesOutput
    ) = LintRulesInput(
        parent,
        true,
        getRuleNameValue(rulesOutput, parent, "expression"),
        getRuleNameValue(rulesOutput, parent, "identifier"),
        getRuleNameValue(rulesOutput, parent, "literal"),
        ""
    )

    private fun getRuleNameValue(
            rulesOutput: RulesOutput,
            parent: String,
            ruleName: String,
        ): Boolean =
            rulesOutput.rules.filter {
                rule -> rule.parent == parent && rule.name == ruleName
            }.first().isActive

        private fun getFormatValue(
            rulesOutput: RulesOutput,
        ): String {
            return if(getRuleNameValue(rulesOutput, "identifier_format", "snake_case")) "snake case"
            else "camel case"
        }
    }
