package manager.redis.events

data class LintRequest(
    val content: String,
    val language: String,
    val version: String,
    val rules: List<LintRulesInput>,
    val input: List<String>,
    val snippetId: String,
    val userId: String,
)

data class LintRulesInput(
    val name: String,
    val isActive: Boolean,
    val expression: Boolean,
    val identifier: Boolean,
    val literal: Boolean,
    val format: String,
)
