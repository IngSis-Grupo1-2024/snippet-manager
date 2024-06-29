package manager.runner.manager

import manager.common.rest.dto.Output
import snippet.FormatInput
import snippet.SnippetInfo

interface Runner {
    fun runSnippet(snippet: SnippetInfo): Output

    fun formatSnippet(snippet: FormatInput): Output
}
