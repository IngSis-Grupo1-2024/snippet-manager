package manager.rules.integration.configuration

import com.example.demo.testCase.model.dto.TestCaseDto
import manager.rules.model.dto.RulesOutput
import manager.rules.model.dto.UpdateRulesDTO
import manager.testCase.model.input.TestCaseInput
import org.springframework.http.ResponseEntity

interface SnippetConf {
    fun createDefaultConf(
        userId: String,
        token: String,
        language: String,
    ): ResponseEntity<String>

    fun getSnippetId(
        token: String,
        testCaseId: String,
    ): String?

    fun postTestCase(
        token: String,
        testCaseInput: TestCaseInput,
    ): TestCaseDto

    fun deleteTestCase(
        token: String,
        testCaseId: String,
    ): String

    fun getRules(
        userId: String,
        token: String,
        type: String,
    ): RulesOutput

    fun getVersion(
        token: String,
        language: String,
    ): String

    fun updateRules(
        updateRulesDTO: UpdateRulesDTO,
        userId: String,
        token: String,
    ): String
}
