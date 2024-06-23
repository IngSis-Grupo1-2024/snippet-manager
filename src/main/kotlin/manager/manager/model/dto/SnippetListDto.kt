package manager.manager.model.dto

import manager.common.rest.dto.Output
import manager.manager.model.enums.ComplianceSnippet
import manager.manager.model.enums.SnippetLanguage

class SnippetListDto(
    val snippets: List<SnippetDto>
): Output