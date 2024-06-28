package snippet

import manager.manager.model.enums.SnippetLanguage

data class SnippetInfo (
    val name: String,
    val content: String,
    val language: SnippetLanguage,
    val version: String,
    val extension: String,
    val input: List<String>,
)
