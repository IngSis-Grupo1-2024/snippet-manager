package manager.manager.model.input

import manager.manager.model.enums.SnippetLanguage

data class CreateSnippet (
    val name: String,
    val content: String,
    val language: SnippetLanguage,
    val extension: String,
)