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
                listOf("Hello"),
                snippet.id.toString(),
                snippet.userSnippet.userId,
            )

        private fun rulesParser(rulesOutput: RulesOutput): List<LintRulesInput> =
            rulesOutput.rules.map { rule ->
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
