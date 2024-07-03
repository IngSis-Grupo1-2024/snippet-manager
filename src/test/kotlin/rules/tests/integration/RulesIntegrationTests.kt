package rules.tests.integration

import manager.testCase.model.dto.TestCaseDto
import com.google.gson.Gson
import manager.rules.dto.RulesDTO
import manager.rules.integration.configuration.SnippetConfBean
import manager.rules.integration.configuration.SnippetConfImpl
import manager.rules.model.dto.RulesOutput
import manager.rules.model.dto.UpdateRulesDTO
import manager.testCase.model.input.TestCaseInput
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.*
import org.mockito.kotlin.whenever
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate

class RulesIntegrationTests {

    private lateinit var restTemplate: RestTemplate
    private lateinit var snippetConfImpl: SnippetConfImpl
    private val snippetConfUrl = "http://localhost:8080"
    private val gson = Gson()

    @BeforeEach
    fun setUp() {
        restTemplate = mock(RestTemplate::class.java)
        snippetConfImpl = SnippetConfImpl(restTemplate, snippetConfUrl)
    }

    @Test
    fun `createSnippetConf returns SnippetConfImpl instance`() {
        val restTemplate = mock(RestTemplate::class.java)
        val snippetConfUrl = "http://localhost:8080"
        val snippetConfBean = SnippetConfBean(restTemplate, snippetConfUrl)

        val snippetConf = snippetConfBean.createSnippetConf()

        assertNotNull(snippetConf)
        assert(snippetConf is SnippetConfImpl)
    }

    @Test
    fun `createDefaultConf sends correct request and returns response`() {
        val expectedResponse = ResponseEntity("Success", HttpStatus.OK)
        `when`(
            restTemplate.exchange(
                any(String::class.java),
                any(),
                any(HttpEntity::class.java),
                any(Class::class.java)
            )
        )
            .thenReturn(expectedResponse)

        val response = snippetConfImpl.createDefaultConf("userId", "token", "language")

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals("Success", response.body)
    }

    @Test
    fun `getSnippetId sends correct request and returns snippet ID`() {
        val expectedResponse = ResponseEntity("snippetId", HttpStatus.OK)
        `when`(
            restTemplate.exchange(
                any(String::class.java),
                any(),
                any(HttpEntity::class.java),
                any(Class::class.java)
            )
        )
            .thenReturn(expectedResponse)

        val snippetId = snippetConfImpl.getSnippetId("token", "testCaseId")

        assertEquals("snippetId", snippetId)
    }

    @Test
    fun `deleteTestCase sends correct request`() {
        val testCaseId = "test-case-id"
        val token = "dummy-token"
        // Mock the restTemplate.exchange call to expect a response of type TestCaseDto
        `when`(
            restTemplate.exchange(
                eq("$snippetConfUrl/test_case/$testCaseId"),
                eq(HttpMethod.DELETE),
                any(HttpEntity::class.java),
                eq(TestCaseDto::class.java) // Change this to match the actual method invocation
            )
        ).thenReturn(ResponseEntity.ok().body(TestCaseDto("test-case-id", "test-case-name", listOf(), listOf(), "")))

        snippetConfImpl.deleteTestCase(token, testCaseId)

        // Verify the restTemplate.exchange call with the corrected response type
        verify(restTemplate).exchange(
            eq("$snippetConfUrl/test_case/$testCaseId"),
            eq(HttpMethod.DELETE),
            any(HttpEntity::class.java),
            eq(TestCaseDto::class.java) // Ensure this matches the change above
        )
    }

    @Test
    fun `updateRules sends correct request and returns response`() {
        val updateRulesDTO = UpdateRulesDTO(listOf(), "LINTING")
        val userId = "user-id"
        val token = "dummy-token"
        val expectedResponse = "Success"
        `when`(
            restTemplate.exchange(
                eq("$snippetConfUrl/rules/update_rules"),
                eq(HttpMethod.POST),
                any(HttpEntity::class.java),
                eq(String::class.java)
            )
        ).thenReturn(ResponseEntity.ok(expectedResponse))

        val response = snippetConfImpl.updateRules(updateRulesDTO, userId, token)

        verify(restTemplate).exchange(
            eq("$snippetConfUrl/rules/update_rules"),
            eq(HttpMethod.POST),
            any(HttpEntity::class.java),
            eq(String::class.java)
        )
        assertEquals(expectedResponse, response)
    }

    @Test
    fun `getVersion sends correct request and returns version`() {
        val token = "dummy-token"
        val language = "en"
        val expectedVersion = "1.0.0"
        whenever(restTemplate.exchange(
            eq("$snippetConfUrl/configuration/get_version/$language"),
            eq(HttpMethod.GET),
            any(HttpEntity::class.java),
            eq(String::class.java)
        )).thenReturn(ResponseEntity.ok(expectedVersion))

        val version = snippetConfImpl.getVersion(token, language)

        assertEquals(expectedVersion, version)
    }

    @Test
    fun `postTestCase sends correct request and returns TestCaseDto`() {
        val token = "dummy-token"
        val testCaseInput = TestCaseInput(
            "test-case-id",
            "1",
            "Test Case Name",
            listOf("Step 1", "Step 2"),
            listOf("Condition 1", "Condition 2"),
            "Expected Result"
        )
        val expectedTestCaseDto = TestCaseDto(
            "test-case-id",
            "Test Case Name",
            listOf("Step 1", "Step 2"),
            listOf("Condition 1", "Condition 2"),
            "Expected Result"
        )
        whenever(restTemplate.exchange(
            eq("$snippetConfUrl/test_case"),
            eq(HttpMethod.POST),
            any(HttpEntity::class.java),
            eq(TestCaseDto::class.java)
        )).thenReturn(ResponseEntity.ok(expectedTestCaseDto))

        val actualTestCaseDto = snippetConfImpl.postTestCase(token, testCaseInput)

        assertEquals(expectedTestCaseDto, actualTestCaseDto)
    }

    @Test
    fun `getRules sends correct request and returns RulesOutput`() {
        val userId = "user-id"
        val token = "dummy-token"
        val type = "rule-type"
        val rulesList = listOf(
            RulesDTO(1, "rule-type-1", true, 1, "parent"),
        )
        val expectedRulesOutput = RulesOutput(rulesList)
        whenever(restTemplate.exchange(
            eq("$snippetConfUrl/rules?ruleType=$type"),
            eq(HttpMethod.GET),
            any(HttpEntity::class.java),
            eq(RulesOutput::class.java)
        )).thenReturn(ResponseEntity.ok(expectedRulesOutput))

        val actualRulesOutput = snippetConfImpl.getRules(userId, token, type)

        assertEquals(expectedRulesOutput, actualRulesOutput)
    }

}
