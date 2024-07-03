package testcase.tests.controller

import manager.common.bucket.BucketAPI
import manager.common.rest.exception.BadReqException
import manager.common.rest.exception.NotFoundException
import manager.manager.repository.SnippetRepository
import manager.rules.integration.configuration.SnippetConf
import manager.runner.manager.Runner
import manager.testCase.controller.TestCaseController
import manager.testCase.model.dto.TestCaseDto
import manager.testCase.model.input.TestCaseInput
import manager.testCase.service.TestCaseService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import org.springframework.security.oauth2.jwt.Jwt

@ExtendWith(MockitoExtension::class)
class TestCaseControllerTest {

    @Mock
    private lateinit var testCaseService: TestCaseService

    @InjectMocks
    private lateinit var testCaseController: TestCaseController

    @Mock
    private lateinit var snippetRepo: SnippetRepository

    @Mock
    private lateinit var bucketAPI: BucketAPI

    @Mock
    private lateinit var snippetConf: SnippetConf

    @Mock
    private lateinit var runnerManager: Runner


    @Test
    fun `postTestCase should return OK status with correct body`() {
        val jwtMock = mockJwt("userId")
        val testCaseInput = TestCaseInput("1", "1", "name", listOf(), listOf(), "env")
        val expectedOutput = TestCaseDto("1", "name", listOf(), listOf(), "env")
        whenever(testCaseService.postTestCase(eq("userId"), any(), eq(testCaseInput))).thenReturn(expectedOutput)

        val response = testCaseController.postTestCase(jwtMock, testCaseInput)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(expectedOutput, response.body)
    }

    @Test
    fun `postTestCase should handle BadReqException correctly`() {
        val jwtMock = mockJwt("userId")
        val testCaseInput = TestCaseInput("1", "1", "name", listOf(), listOf(), "env")
        whenever(testCaseService.postTestCase(eq("userId"), any(), eq(testCaseInput)))
            .thenThrow(BadReqException("Bad request"))

        val response = testCaseController.postTestCase(jwtMock, testCaseInput)

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
    }

    @Test
    fun `postTestCase should handle NotFoundException correctly`() {
        val jwtMock = mockJwt("userId")
        val testCaseInput = TestCaseInput("1", "1", "name", listOf(), listOf(), "env")
        whenever(testCaseService.postTestCase(eq("userId"), any(), eq(testCaseInput)))
            .thenThrow(NotFoundException("Not found"))

        val response = testCaseController.postTestCase(jwtMock, testCaseInput)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }

    private fun mockJwt(subject: String): Jwt {
        return Jwt.withTokenValue("token")
            .header("alg", "none")
            .claim("sub", subject)
            .build()
    }
}
