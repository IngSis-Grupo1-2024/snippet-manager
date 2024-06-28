package manager.redis.producer

data class LintRequest(
    val userId: String,
    val snippetId: String,
    val ruleConfig: String,
)
