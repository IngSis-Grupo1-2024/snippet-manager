package manager.manager.model.dto

import manager.common.rest.dto.Output
import manager.manager.model.enums.ComplianceSnippet
import manager.manager.model.enums.SnippetLanguage

class SnippetDto(
    val name: String,
    val content: String,
    val language: SnippetLanguage,
    val extension: String,
    val id: Long,
    val compliance: ComplianceSnippet,
    val author: String
): Output