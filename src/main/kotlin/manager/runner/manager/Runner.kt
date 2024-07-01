package manager.runner.manager

import manager.common.rest.dto.Output
import manager.snippet.FormatInput
import manager.snippet.RunningOutput
import manager.snippet.SnippetInfo

interface Runner {
    fun runSnippet(token: String, snippet: SnippetInfo): RunningOutput

    fun formatSnippet(
        token: String,
        snippet: FormatInput,
    ): RunningOutput
}
