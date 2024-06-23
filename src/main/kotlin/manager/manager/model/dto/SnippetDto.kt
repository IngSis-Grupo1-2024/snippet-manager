package manager.manager.model.dto

import manager.common.rest.dto.Output
import manager.manager.model.entity.SnippetLanguage
import java.time.LocalDateTime

class SnippetDto(
    val id: Long,
    val name: String,
    val content: String,
    val language: SnippetLanguage,
    val createdAt: LocalDateTime,
    val updateAt: LocalDateTime,
): Output