package com.example.snippetmanager.snippet

import manager.manager.model.enums.SnippetLanguage

data class CreateSnippet (
    val name: String,
    val content: String,
    val language: SnippetLanguage,
    val extension: String,
)