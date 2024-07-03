package runner.tests.manager

import manager.manager.model.enums.SnippetLanguage
import manager.rules.dto.RulesDTO
import manager.runner.manager.Runner
import manager.runner.manager.RunnerManager
import manager.runner.manager.RunnerManagerConfig
import manager.snippet.FormatInput
import manager.snippet.RunningOutput
import manager.snippet.SnippetInfo
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.web.client.RestTemplate

@ExtendWith(SpringExtension::class, MockitoExtension::class)
@SpringBootTest(classes = [RunnerManagerConfig::class])
class RunnerManagerConfigTest {

    @MockBean
    private lateinit var restTemplate: RestTemplate

    @Autowired
    private lateinit var runner: Runner

    private lateinit var runnerManager: RunnerManager

    @Value("\${manager.runner.url}")
    private lateinit var runnerUrl: String

    companion object {
        @JvmStatic
        @DynamicPropertySource
        fun runnerProperties(registry: DynamicPropertyRegistry) {
            registry.add("manager.runner.url") { "http://testurl" }
        }
    }

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        runnerManager = RunnerManager(restTemplate, "http://testurl")
    }


    @Test
    fun `createRunnerManager should create RunnerManager with correct RestTemplate and runnerUrl`() {
        assertTrue(runner is RunnerManager)
        val runnerManager = runner as RunnerManager
        // Assuming RunnerManager exposes rest and runnerUrl for testing purposes
        assertTrue(runnerManager.rest == restTemplate)
        assertTrue(runnerManager.runnerUrl == runnerUrl)
    }

    @Test
    fun `runSnippet should return RunningOutput on success`() {
        val token = "testToken"
        val snippetInfo = SnippetInfo("name", "content", SnippetLanguage.PRINTSCRIPT, "v1", "ps", listOf())
        val expectedOutput = RunningOutput(listOf(), listOf())
        `when`(restTemplate.postForEntity(any(String::class.java), any(), any(Class::class.java)))
            .thenReturn(ResponseEntity(expectedOutput, HttpStatus.OK))

        val result = runnerManager.runSnippet(token, snippetInfo)

        assertEquals(expectedOutput, result)
    }

    @Test
    fun `runSnippet should throw exception on error`() {
        val token = "testToken"
        val snippetInfo = SnippetInfo("name", "content", SnippetLanguage.PRINTSCRIPT, "v1", "ps", listOf())
        `when`(restTemplate.postForEntity(any(String::class.java), any(), any(Class::class.java)))
            .thenReturn(ResponseEntity(null, HttpStatus.INTERNAL_SERVER_ERROR))

        assertThrows(Exception::class.java) {
            runnerManager.runSnippet(token, snippetInfo)
        }
    }

//    @Test
//    fun `formatSnippet should return RunningOutput on success`() {
//        val token = "testToken"
//        val formatInput =
//            FormatInput(
//                "content",
//                SnippetLanguage.PRINTSCRIPT,
//                "v1",
//                listOf(RulesDTO(1, "rule", true, 1, "parent")),
//                listOf()
//            )
//        val expectedOutput = RunningOutput(listOf(), listOf())
//        `when`(restTemplate.exchange(any(String::class.java), HttpMethod.POST, any(), any(Class::class.java)))
//            .thenReturn(ResponseEntity(expectedOutput, HttpStatus.OK))
//
//        val result = runnerManager.formatSnippet(token, formatInput)
//
//        assertEquals(expectedOutput, result)
//    }

    @Test
    fun `formatSnippet should throw exception on error`() {
        val token = "testToken"
        val formatInput =
            FormatInput(
                "content",
                SnippetLanguage.PRINTSCRIPT,
                "v1",
                listOf(RulesDTO(1, "rule", true, 1, "parent")),
                listOf()
            )
        `when`(restTemplate.exchange(any(String::class.java), any(), any(), any(Class::class.java)))
            .thenReturn(ResponseEntity(null, HttpStatus.INTERNAL_SERVER_ERROR))

        assertThrows(Exception::class.java) {
            runnerManager.formatSnippet(token, formatInput)
        }
    }

}
