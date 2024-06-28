package manager.runner.manager

import manager.common.rest.dto.Output
import snippet.SnippetInfo
import snippet.FormatInput

interface Runner {
    fun runSnippet(snippet: SnippetInfo): Output
    fun formatSnippet(snippet: FormatInput): Output
}