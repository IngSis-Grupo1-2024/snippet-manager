package manager.runner.manager

import manager.snippet.FormatInput
import manager.snippet.RunningOutput
import manager.snippet.SnippetInfo

interface Runner {
    fun runSnippet(
        token: String,
        snippet: SnippetInfo,
    ): RunningOutput

    fun formatSnippet(
        token: String,
        snippet: FormatInput,
    ): RunningOutput
}
