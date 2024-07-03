package testcase.tests.model

import manager.testCase.model.dto.TestCaseDto
import manager.testCase.model.dto.TestCaseResult
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class TestCaseDtoTest {

    @Test
    fun `test getters`() {
        val id = "testId"
        val name = "testName"
        val input = listOf("input1", "input2")
        val output = listOf("output1", "output2")
        val envVars = "envVar1=envVarValue1"

        val testCaseDto = TestCaseDto(id, name, input, output, envVars)

        assertEquals(id, testCaseDto.id)
        assertEquals(name, testCaseDto.name)
        assertEquals(input, testCaseDto.input)
        assertEquals(output, testCaseDto.output)
        assertEquals(envVars, testCaseDto.envVars)
    }


    @Test
    fun `TestCaseResult contains expected values`() {
        val expectedValues = setOf("success", "fail", "error")
        val actualValues = TestCaseResult.values().map { it.name }.toSet()

        assertTrue(actualValues.containsAll(expectedValues))
    }
}
