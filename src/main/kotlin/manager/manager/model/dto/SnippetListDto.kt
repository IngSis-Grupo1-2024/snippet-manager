package manager.manager.model.dto

import manager.common.rest.dto.Output
import manager.manager.model.enums.ComplianceSnippet
import manager.manager.model.enums.SnippetLanguage

class SnippetListDto(
    val snippets: List<SnippetDto>
): Output {
    fun merge(snippetListDto: SnippetListDto): SnippetListDto {
        return if(this.snippets.isEmpty())  snippetListDto
        else if(snippetListDto.snippets.isEmpty()) this
        else SnippetListDto(this.snippets.plus(snippetListDto.snippets))
    }
}