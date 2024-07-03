package testcase.tests.service

import manager.common.rest.exception.BadReqException
import manager.manager.integration.permission.SnippetPerm
import manager.manager.model.entity.Snippet
import manager.manager.model.enums.PermissionType
import manager.manager.repository.SnippetRepository
import manager.rules.integration.configuration.SnippetConf
import manager.testCase.model.dto.TestCaseDto
import manager.testCase.model.dto.TestCaseResult
import manager.testCase.model.input.TestCaseInput
import manager.testCase.service.TestCaseService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.*
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.util.*

@ExtendWith(MockitoExtension::class)
class TestCaseServiceTests {

    @Mock
    private lateinit var snippetConf: SnippetConf

    @Mock
    private lateinit var snippetPerm: SnippetPerm

    @Mock
    private lateinit var snippetRepo: SnippetRepository

    @Mock
    private lateinit var runnerManager: manager.runner.manager.Runner

    @Mock
    private lateinit var bucketAPI: manager.common.bucket.BucketAPI

    @InjectMocks
    private lateinit var testCaseService: TestCaseService

    @Test
    fun `postTestCase throws BadReqException when user is not the owner`() {
        val userId = "user123"
        val token = "token123"
        val testCaseInput =
            TestCaseInput("1", "1", "Test Case", listOf("input"), listOf("output"), "env")

        val snippet = Snippet()
        `when`(snippetRepo.findById(1L)).thenReturn(Optional.of(snippet))
        `when`(snippetPerm.getPermissionType("1", userId, token)).thenReturn(PermissionType.R)

        assertThrows(BadReqException::class.java) {
            testCaseService.postTestCase(userId, token, testCaseInput)
        }
    }

    @Test
    fun `postTestCase succeeds when user is the owner`() {
        val userId = "user123"
        val token = "token123"
        val testCaseInput =
            TestCaseInput("1", "1", "Test Case", listOf("input"), listOf("output"), "env")
        val expectedOutput = TestCaseDto("1", "name", listOf("input"), listOf("output"), "env")

        val snippet = Snippet()
        `when`(snippetRepo.findById(1L)).thenReturn(Optional.of(snippet))
        `when`(snippetPerm.getPermissionType("1", userId, token)).thenReturn(PermissionType.OWNER)
        `when`(snippetConf.postTestCase(token, testCaseInput)).thenReturn(expectedOutput)

        val result = testCaseService.postTestCase(userId, token, testCaseInput)

        assert(result == expectedOutput)
    }

    @Test
    fun `deleteTestCase does nothing when snippet ID does not exist`() {
        val userId = "user123"
        val token = "token123"
        val testCaseId = "nonexistent"

        `when`(snippetConf.getSnippetId(token, testCaseId)).thenReturn(null)

        testCaseService.deleteTestCase(userId, token, testCaseId)
    }

    @Test
    fun `deleteTestCase throws BadReqException when user is not the owner`() {
        val userId = "user123"
        val token = "token123"
        val testCaseId = "1"
        val snippetId = "1"

        `when`(snippetConf.getSnippetId(token, testCaseId)).thenReturn(snippetId)
        `when`(snippetPerm.getPermissionType(snippetId, userId, token)).thenReturn(PermissionType.R)

        assertThrows(BadReqException::class.java) {
            testCaseService.deleteTestCase(userId, token, testCaseId)
        }
    }

    @Test
    fun `deleteTestCase succeeds when user is the owner`() {
        val userId = "user123"
        val token = "token123"
        val testCaseId = "1"
        val snippetId = "1"

        `when`(snippetConf.getSnippetId(token, testCaseId)).thenReturn(snippetId)
        `when`(snippetPerm.getPermissionType(snippetId, userId, token)).thenReturn(PermissionType.OWNER)

        testCaseService.deleteTestCase(userId, token, testCaseId)
    }

    @Test
    fun `runTestCase throws BadReqException when snippet does not exist`() {
        val testCaseInput = TestCaseInput("nonexistent", "1", "Test Case", listOf("input"), listOf("output"), "env")
        val token = "token123"

        `when`(snippetRepo.findById(anyLong())).thenReturn(Optional.empty())

        assertThrows(BadReqException::class.java) {
            testCaseService.runTestCase(testCaseInput, "nonexistent", token)
        }
    }


}
