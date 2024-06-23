package manager.manager.model.dto

import manager.common.rest.dto.Output
import manager.manager.model.enums.ComplianceSnippet
import manager.manager.model.enums.SnippetLanguage

class SnippetDto(
    val id: Long,
    val name: String,
    val content: String,
    val compliance: ComplianceSnippet,
    val author: String,
    val language: SnippetLanguage,
    val extension: String
): Output