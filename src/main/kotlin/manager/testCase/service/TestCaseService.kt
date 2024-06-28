package manager.testCase.service

import manager.common.rest.dto.Output
import manager.common.rest.exception.BadReqException
import manager.manager.integration.permission.SnippetPerm
import manager.manager.model.enums.PermissionType
import manager.manager.repository.SnippetRepository
import manager.rules.integration.configuration.SnippetConf
import manager.testCase.model.input.TestCaseInput
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class TestCaseService
    @Autowired
    constructor(
        private val snippetConf: SnippetConf,
        private val snippetPerm: SnippetPerm,
        private val snippetRepo: SnippetRepository,
    ) {
        fun postTestCase(
            userId: String,
            token: String,
            testCaseInput: TestCaseInput,
        ): Output {
            if (snippetNotExist(testCaseInput)) throw BadReqException("Snippet not found")

            if (userIsNotTheOwner(testCaseInput.snippetId, userId, token)) {
                throw BadReqException(
                    "The user has no permissions " +
                        "for creation or update of the test",
                )
            }
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

            snippetConf.deleteTestCase(token, testCaseId)
        }

        private fun userIsNotTheOwner(
            snippetId: String,
            userId: String,
            token: String,
        ): Boolean {
            val permissionType: PermissionType = snippetPerm.getPermissionType(snippetId, userId, token)
            return permissionType != PermissionType.OWNER
        }

        private fun snippetNotExist(testCaseInput: TestCaseInput): Boolean {
            val snippet = snippetRepo.findById(testCaseInput.snippetId.toLong())
            return snippet.isEmpty
        }
    }
