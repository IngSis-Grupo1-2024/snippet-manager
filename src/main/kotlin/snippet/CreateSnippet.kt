package com.example.snippetmanager.snippet

data class CreateSnippet (

    val name: String,
    val content: String,
    val language: String,
    val extension: String,
)