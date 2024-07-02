package manager.snippet

import manager.manager.model.enums.SnippetLanguage
import manager.rules.dto.RulesDTO

class FormatInput(
    val content: String,
    val language: SnippetLanguage,
    val version: String,
    val rules: List<RulesDTO>,
    val input: List<String>,
)
