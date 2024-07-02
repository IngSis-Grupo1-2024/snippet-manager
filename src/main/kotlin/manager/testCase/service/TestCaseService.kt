package manager.testCase.service

import manager.common.bucket.BucketAPI
import manager.common.rest.dto.Output
import manager.common.rest.exception.BadReqException
import manager.manager.integration.permission.SnippetPerm
import manager.manager.model.entity.Snippet
import manager.manager.model.enums.PermissionType
import manager.manager.repository.SnippetRepository
import manager.rules.controller.RulesController
import manager.rules.integration.configuration.SnippetConf
import manager.runner.manager.Runner
import manager.snippet.RunningOutput
import manager.snippet.SnippetInfo
import manager.testCase.model.dto.TestCaseResult
import manager.testCase.model.input.TestCaseInput
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class TestCaseService
    @Autowired
    constructor(
        private val snippetConf: SnippetConf,
        private val snippetPerm: SnippetPerm,
        private val snippetRepo: SnippetRepository,
        private val runnerManager: Runner,
        private val bucketAPI: BucketAPI,
    ) {
    private val logger = LoggerFactory.getLogger(RulesController::class.java)
        fun postTestCase(
            userId: String,
            token: String,
            testCaseInput: TestCaseInput,
        ): Output {
            checkIfSnippetExists(testCaseInput)

            if (userIsNotTheOwner(testCaseInput.snippetId, userId, token)) {
                throw BadReqException(
                    "The user has no permissions " +
                        "for creation or update of the test",
                )
            }
            logger.info("Calling snippet configuration to post the test case ${testCaseInput.name}")
            return snippetConf.postTestCase(token, testCaseInput)
        }

    fun deleteTestCase(
            userId: String,
            token: String,
            testCaseId: String,
        ) {
            val snippetId: String = snippetConf.getSnippetId(token, testCaseId) ?: return
            if (userIsNotTheOwner(snippetId, userId, token)) {
                throw BadReqException(
                    "The user has no permissions " +
                        "for deleting the test",
                )
            }
            logger.info("Calling snippet configuration to delete the test case $testCaseId")
            snippetConf.deleteTestCase(token, testCaseId)
        }

        fun runTestCase(testCaseInput: TestCaseInput, snippetId: String, token: String): TestCaseResult {
            val snippet = checkIfSnippetExists(testCaseInput)
            val snippetInfo = getSnippet(snippet, testCaseInput.input, token)
            val output: RunningOutput = runnerManager.runSnippet(token, snippetInfo)

            return verifyOutput(output, testCaseInput.output)
        }

    private fun verifyOutput(actual: RunningOutput, expected: List<String>?): TestCaseResult {
        return if(actual.error.isNotEmpty()) TestCaseResult.fail
        else if(expected.isNullOrEmpty() && actual.output.isEmpty()) TestCaseResult.success
        else if(expected == actual.output) TestCaseResult.success
        else TestCaseResult.fail
    }

    private fun getSnippet(snippet: Snippet, input: List<String>?, token: String): SnippetInfo {
        val content = bucketAPI.getSnippet(snippet.id.toString())
        val version = snippetConf.getVersion(token, snippet.language.toString())

        if(input.isNullOrEmpty()) return snippetInfo(snippet, content, emptyList(), version)
        return snippetInfo(snippet, content, input, version)
    }

    private fun snippetInfo(
        snippet: Snippet,
        content: String,
        input: List<String>,
        version: String
    ): SnippetInfo {
        return SnippetInfo(
            name = snippet.name,
            content = content,
            language = snippet.language,
            version = version,
            extension = snippet.extension,
            input = input
        )
    }

    private fun userIsNotTheOwner(
            snippetId: String,
            userId: String,
            token: String,
        ): Boolean {
            val permissionType: PermissionType = snippetPerm.getPermissionType(snippetId, userId, token)
            return permissionType != PermissionType.OWNER
        }


    private fun checkIfSnippetExists(testCaseInput: TestCaseInput): Snippet {
        val snippet = snippetRepo.findById(testCaseInput.snippetId.toLong())
        if(snippet.isEmpty) throw BadReqException("Snippet not found")
        return snippet.get()
    }
}
