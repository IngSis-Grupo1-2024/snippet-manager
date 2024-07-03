package runner.tests.controller

import manager.runner.controller.RunnerController
import manager.runner.service.RunnerService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.oauth2.jwt.Jwt

@SpringBootTest(classes = [RunnerController::class])
class RunnerControllerTest {

    @Autowired
    private lateinit var runnerController: RunnerController

    @MockBean
    private lateinit var runnerService: RunnerService

    private lateinit var jwt: Jwt

    @BeforeEach
    fun setUp() {
        jwt = Mockito.mock(Jwt::class.java)
        Mockito.`when`(jwt.tokenValue).thenReturn("test-token")
        Mockito.`when`(jwt.subject).thenReturn("test-subject")
    }

    @Test
    fun `test runSnippet success`() {
        val snippetId = "snippet-1"
        val expectedOutput = "Execution result"

        Mockito.`when`(runnerService.runSnippet(jwt.tokenValue, snippetId))
            .thenReturn(expectedOutput)

        val response: ResponseEntity<String> = runnerController.runSnippet(jwt, snippetId)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(expectedOutput, response.body)
    }

    @Test
    fun `test runSnippet failure`() {
        val snippetId = "snippet-1"
        val errorMessage = "Error during execution"

        Mockito.`when`(runnerService.runSnippet(jwt.tokenValue, snippetId))
            .thenThrow(RuntimeException(errorMessage))

        val response: ResponseEntity<String> = runnerController.runSnippet(jwt, snippetId)

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode)
        assertEquals(errorMessage, response.body)
    }

    @Test
    fun `test formatSnippet success`() {
        val snippetId = "snippet-1"
        val expectedOutput = "Formatted snippet"

        Mockito.`when`(runnerService.formatSnippet(snippetId, jwt.subject, jwt.tokenValue))
            .thenReturn(expectedOutput)

        val response: ResponseEntity<String> = runnerController.formatSnippet(jwt, snippetId)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(expectedOutput, response.body)
    }

    @Test
    fun `test formatSnippet failure`() {
        val snippetId = "snippet-1"
        val errorMessage = "Error during formatting"

        Mockito.`when`(runnerService.formatSnippet(snippetId, jwt.subject, jwt.tokenValue))
            .thenThrow(RuntimeException(errorMessage))

        val response: ResponseEntity<String> = runnerController.formatSnippet(jwt, snippetId)

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode)
        assertEquals(errorMessage, response.body)
    }
}
