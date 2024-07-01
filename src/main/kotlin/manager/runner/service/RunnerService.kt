package manager.runner.service

import manager.common.bucket.BucketAPI
import manager.common.rest.dto.Output
import manager.common.rest.exception.NotFoundException
import manager.manager.integration.permission.SnippetPerm
import manager.manager.model.enums.PermissionType
import manager.manager.repository.SnippetRepository
import manager.rules.integration.configuration.SnippetConf
import manager.runner.manager.Runner
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import manager.snippet.FormatInput
import manager.snippet.SnippetInfo

@Service
class RunnerService
    @Autowired
    constructor(
        private val runnerManager: Runner,
        private val bucketAPI: BucketAPI,
        private val snippetRepository: SnippetRepository,
        private val snippetConf: SnippetConf,
        private val snippetPermImpl: SnippetPerm,
    ) {
        fun runSnippet(snippet: SnippetInfo): Output {
            return runnerManager.runSnippet(snippet)
        }

        fun formatSnippet(
            snippetId: String,
            userId: String,
            token: String,
        ): String {
            /* Logic for checking permissions */
            val permission = snippetPermImpl.getPermissionType(snippetId, userId, token)
            if (permission != PermissionType.OWNER) {
                return "You don't have permission to format this snippet"
            }
            /* Bring Snippet and content from Bucket and DB */
            val snippetContent = bucketAPI.getSnippet(snippetId)
            val snippet = snippetRepository.findById(snippetId.toLong())

            /* Bring rules, version and input */
            val rules = snippetConf.getRules(userId, token, "FORMATTING").rules
            val version = snippetConf.getVersion(token, snippet.get().language.toString())

            val snippetInfo = FormatInput(snippetContent, snippet.get().language, version, rules, listOf("hi"))
            val response = runnerManager.formatSnippet(token, snippetInfo)
            if (response.error.size > 0) {
                throw NotFoundException("Error occurred: ${response.error.joinToString()}")
            }
            val stringResponse = response.output.joinToString()

            bucketAPI.deleteSnippet(snippetId)
            bucketAPI.createSnippet(snippetId, stringResponse)
            return stringResponse
        }
    }
