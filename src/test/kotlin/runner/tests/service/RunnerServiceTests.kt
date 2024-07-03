package runner.tests.service

import manager.common.bucket.BucketAPI
import manager.manager.integration.permission.SnippetPerm
import manager.manager.model.entity.Snippet
import manager.manager.model.enums.PermissionType
import manager.manager.model.enums.SnippetLanguage
import manager.manager.repository.SnippetRepository
import manager.manager.service.ManagerService
import manager.rules.dto.RulesDTO
import manager.rules.integration.configuration.SnippetConf
import manager.rules.model.dto.RulesOutput
import manager.runner.manager.Runner
import manager.runner.service.RunnerService
import manager.snippet.RunningOutput
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.util.*

class RunnerServiceTests {

    private lateinit var runnerService: RunnerService
    private val runnerManager: Runner = mock()
    private val bucketAPI: BucketAPI = mock()
    private val snippetRepository: SnippetRepository = mock()
    private val snippetConf: SnippetConf = mock()
    private val snippetPermImpl: SnippetPerm = mock()
    private val managerService: ManagerService = mock()

    @BeforeEach
    fun setUp() {
        runnerService = RunnerService(runnerManager, bucketAPI, snippetRepository, snippetConf, snippetPermImpl, managerService)

        whenever(snippetConf.getVersion(any(), any())).thenReturn("1.0")
    }

    @Test
    fun `runSnippet returns expected output on success`() {
        val token = "token"
        val snippetId = "1"
        val expectedOutput = "output"
        val mockSnippet = Snippet().apply {
            language = SnippetLanguage.PRINTSCRIPT
            name = "ExampleSnippet"
            extension = "ps"
        }
        whenever(snippetRepository.findById(snippetId.toLong())).thenReturn(Optional.of(mockSnippet))
        whenever(bucketAPI.getSnippet(snippetId)).thenReturn("content")
        whenever(runnerManager.runSnippet(any(), any())).thenReturn(RunningOutput(listOf(expectedOutput), listOf()))

        val result = runnerService.runSnippet(token, snippetId)

        assertEquals(expectedOutput, result.replace("\n", ""))
    }

    @Test
    fun `formatSnippet returns expected formatted output`() {
        val snippetId = "1"
        val userId = "user"
        val token = "token"
        val expectedFormattedContent = "formatted content"
        val mockSnippet = Snippet().apply {
            language = SnippetLanguage.PRINTSCRIPT
            name = "ExampleSnippet"
            extension = "ps"
        }
        whenever(snippetRepository.findById(snippetId.toLong())).thenReturn(Optional.of(mockSnippet))
        whenever(snippetPermImpl.getPermissionType(snippetId, userId, token)).thenReturn(PermissionType.OWNER)
        whenever(bucketAPI.getSnippet(snippetId)).thenReturn("content")
        whenever(snippetConf.getRules(userId, token, "FORMATTING")).thenReturn(
            (RulesOutput(
                listOf(
                    RulesDTO(
                        1,
                        "name",
                        true,
                        1,
                        "parent"
                    )
                )
            ))
        )
        whenever(runnerManager.formatSnippet(any(), any())).thenReturn(
            RunningOutput(
                listOf(expectedFormattedContent),
                listOf()
            )
        )

        val result = runnerService.formatSnippet(snippetId, userId, token)

        assertEquals(expectedFormattedContent, result)
    }

    @Test
    fun `formatSnippet throws exception for insufficient permissions`() {
        val snippetId = "1"
        val userId = "user"
        val token = "token"
        whenever(snippetPermImpl.getPermissionType(snippetId, userId, token)).thenReturn(PermissionType.R)

        assertThrows<IllegalStateException> {
            runnerService.formatSnippet(snippetId, userId, token)
        }
    }
}
