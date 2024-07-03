package snippet.tests

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import manager.manager.model.enums.SnippetLanguage
import manager.rules.dto.RulesDTO
import manager.snippet.FormatInput
import manager.snippet.RunningOutput
import manager.snippet.SnippetInfo

class SnippetTests {

    @Test
    fun testFormatInput() {
        val rules = listOf(RulesDTO(1L, "expression", true, 1, "lint"),
            RulesDTO(2L, "line-length", true, 100, "lint"))
        val input = listOf("input1", "input2")
        val formatInput = FormatInput("content", SnippetLanguage.JAVA, "1.0", rules, input)

        assertEquals("content", formatInput.content)
        assertEquals(SnippetLanguage.JAVA, formatInput.language)
        assertEquals("1.0", formatInput.version)
        assertEquals(rules, formatInput.rules)
        assertEquals(input, formatInput.input)
    }

    @Test
    fun testRunningOutput() {
        val output = listOf("output1", "output2")
        val error = listOf("error1", "error2")
        val runningOutput = RunningOutput(output, error)

        assertEquals(output, runningOutput.output)
        assertEquals(error, runningOutput.error)
    }

    @Test
    fun testSnippetInfo() {
        val input = listOf("input1", "input2")
        val snippetInfo = SnippetInfo("name", "content", SnippetLanguage.JAVA, "1.0", ".java", input)

        assertEquals("name", snippetInfo.name)
        assertEquals("content", snippetInfo.content)
        assertEquals(SnippetLanguage.JAVA, snippetInfo.language)
        assertEquals("1.0", snippetInfo.version)
        assertEquals(".java", snippetInfo.extension)
        assertEquals(input, snippetInfo.input)
    }
}
