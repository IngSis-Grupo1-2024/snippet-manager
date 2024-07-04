package manager.runner.service

import manager.common.bucket.BucketAPI
import manager.manager.integration.permission.SnippetPerm
import manager.manager.model.entity.Snippet
import manager.manager.model.enums.PermissionType
import manager.manager.model.enums.SnippetLanguage
import manager.manager.repository.SnippetRepository
import manager.manager.service.ManagerService
import manager.rules.dto.RulesDTO
import manager.rules.integration.configuration.SnippetConf
import manager.runner.manager.Runner
import manager.snippet.FormatInput
import manager.snippet.SnippetInfo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class RunnerService
    @Autowired
    constructor(
        private val runnerManager: Runner,
        private val bucketAPI: BucketAPI,
        private val snippetRepository: SnippetRepository,
        private val snippetConf: SnippetConf,
        private val snippetPermImpl: SnippetPerm,
        private val managerService: ManagerService,
    ) {
        fun runSnippet(
            token: String,
            snippetId: String,
        ): String {
            val content = fetchSnippetContent(snippetId)
            val snippet = snippetRepository.findById(snippetId.toLong())

            val version = fetchSnippetVersion(token, snippet.get().language.toString())
            val snippetInfo = getSnippetInfo(snippet, content, version)

            val response = runnerManager.runSnippet(token, snippetInfo)
            if (response.error.isNotEmpty()) {
                throw Exception(response.error.joinToString("\n"))
            }
            return response.output.joinToString("\n")
        }
    fun formatSnippet(
            snippetId: String,
            userId: String,
            token: String,
        ): String {
            checkPermission(snippetId, userId, token)
            val snippetContent = fetchSnippetContent(snippetId)
            val snippet = snippetRepository.findById(snippetId.toLong())

            val rules = snippetConf.getRules(userId, token, "FORMATTING").rules
            val version = fetchSnippetVersion(token, snippet.get().language.toString())

            val formattedResponse = executeFormatting(token, snippetContent, snippet.get().language, version, rules, listOf("hi"))
            managerService.updateSnippet(snippetId, formattedResponse, userId, token)

            return formattedResponse
        }

        private fun checkPermission(
            snippetId: String,
            userId: String,
            token: String,
        ) {
            val permission = snippetPermImpl.getPermissionType(snippetId, userId, token)
            if (permission != PermissionType.OWNER) {
                throw IllegalStateException("You don't have permission to format this snippet")
            }
        }

        private fun fetchSnippetContent(snippetId: String): String {
            return bucketAPI.getSnippet(snippetId)
        }

        private fun fetchSnippetVersion(
            token: String,
            language: String,
        ): String {
            return snippetConf.getVersion(token, language)
        }

        private fun executeFormatting(
            token: String,
            content: String,
            language: SnippetLanguage,
            version: String,
            rules: List<RulesDTO>,
            input: List<String>,
        ): String {
            val snippetInfo = FormatInput(content, language, version, rules, input)
            val response = runnerManager.formatSnippet(token, snippetInfo)

            if (response.error.isNotEmpty()) {
                throw IllegalStateException("Error occurred: ${response.error.joinToString()}")
            }

            return response.output.joinToString()
        }

        private fun getSnippetInfo(
            snippet: Optional<Snippet>,
            content: String,
            version: String
        ) = SnippetInfo(
            snippet.get().name,
            content,
            snippet.get().language,
            version,
            snippet.get().extension,
            listOf("hi"),
        )
}
