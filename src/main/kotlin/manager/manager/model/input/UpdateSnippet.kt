package com.example.snippetmanager.snippet

import manager.manager.model.entity.SnippetLanguage

data class UpdateSnippet (
    val name: String?,
    val content: String?,
    val language: SnippetLanguage?,
)