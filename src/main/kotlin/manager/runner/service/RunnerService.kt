package manager.runner.service

import manager.bucket.BucketAPI
import manager.common.rest.ResponseOutput
import manager.common.rest.dto.Output
import manager.common.rest.exception.ErrorOutput
import manager.manager.repository.SnippetRepository
import manager.rules.integration.configuration.SnippetConf
import manager.runner.manager.Runner
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import snippet.FormatInput
import snippet.SnippetFormatBody
import snippet.SnippetInfo

@Service
class RunnerService
    @Autowired
    constructor(
        private val runnerManager: Runner,
        private val bucketAPI: BucketAPI,
        private val snippetRepository: SnippetRepository,
        private val snippetConf: SnippetConf,
    ) {
        fun runSnippet(snippet: SnippetInfo): Output {
            // logic for checking permissions
            return runnerManager.runSnippet(snippet)
        }

        fun formatSnippet(
            snippetBody: SnippetFormatBody,
            userId: String,
            token: String,
        ): String {
            // logic for checking permissions
            // TODO: bring config to have the corresponding version & input

            val snippetContent = bucketAPI.getSnippet(snippetBody.id.toString())
            val snippet = snippetRepository.findById(snippetBody.id.toLong())

            val rules = snippetConf.getRules(userId, token, "FORMATTING").rules

            val snippetInfo = FormatInput(snippetContent, snippet.get().language, "v1", rules, listOf("hi"))
            val response = runnerManager.formatSnippet(snippetInfo)
            if (response.output == null) {
                return ErrorOutput(response.error[0]).message
            }
            return ResponseOutput(response.output[0]).message
        }
    }
