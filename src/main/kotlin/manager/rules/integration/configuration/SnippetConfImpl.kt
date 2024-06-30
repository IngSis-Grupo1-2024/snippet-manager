package manager.rules.integration.configuration

import com.example.demo.testCase.model.dto.TestCaseDto
import com.nimbusds.jose.shaded.gson.Gson
import com.nimbusds.jose.shaded.gson.JsonArray
import manager.common.rest.BasicRest
import manager.manager.rules.model.input.ConfigInput.Companion.getJson
import manager.rules.model.dto.RulesOutput
import manager.rules.model.dto.UpdateRulesDTO
import manager.testCase.model.input.TestCaseInput
import org.springframework.http.*
import org.springframework.web.client.*

class SnippetConfImpl(
    private val restTemplate: RestTemplate,
    private val snippetConfUrl: String,
) : SnippetConf {
    override fun createDefaultConf(
        userId: String,
        token: String,
        language: String,
    ): ResponseEntity<String> {
        val url = "$snippetConfUrl/configuration"
        val headers = BasicRest.getAuthHeaders(token)
        headers.contentType = MediaType.APPLICATION_JSON

        val request = HttpEntity<String>(getJsonDefault(userId, language), headers)
        return restTemplate.postForEntity<String>(url, request)
    }

    override fun getSnippetId(
        token: String,
        testCaseId: String,
    ): String? {
        val url = "$snippetConfUrl/test_case/snippet/$testCaseId"
        val headers = BasicRest.getAuthHeaders(token)

        val request = HttpEntity<String>(headers)
        val response = restTemplate.exchange(url, HttpMethod.GET, request, String::class.java)
        return response.body
    }

    override fun postTestCase(
        token: String,
        testCaseInput: TestCaseInput,
    ): TestCaseDto {
        val url = "$snippetConfUrl/test_case"
        val headers = BasicRest.getAuthHeaders(token)
        headers.contentType = MediaType.APPLICATION_JSON

        val request = HttpEntity<String>(getJsonTestCase(testCaseInput), headers)
        val response = restTemplate.exchange(url, HttpMethod.POST, request, TestCaseDto::class.java)
        return response.body!!
    }

    override fun deleteTestCase(
        token: String,
        testCaseId: String,
    ): String {
        val url = "$snippetConfUrl/test_case/$testCaseId"
        val headers = BasicRest.getAuthHeaders(token)

        val request = HttpEntity<String>(headers)
        restTemplate.exchange(url, HttpMethod.DELETE, request, TestCaseDto::class.java)
        return ""
    }

    override fun getRules(
        userId: String,
        token: String,
        type: String,
    ): RulesOutput {
        val url = "$snippetConfUrl/rules?ruleType=$type"
        val headers = BasicRest.getAuthHeaders(token)
        val entity = HttpEntity<String>(headers)
        val response = restTemplate.exchange(url, HttpMethod.GET, entity, RulesOutput::class.java)
        return response.body!!
    }

    override fun updateRules(
        updateRulesDTO: UpdateRulesDTO,
        userId: String,
        token: String,
    ): String {
        val url = "$snippetConfUrl/rules/update_rules"
        val headers = BasicRest.getAuthHeaders(token)
        headers.contentType = MediaType.APPLICATION_JSON

        val request = HttpEntity<String>(getJsonUpdateRules(updateRulesDTO), headers)
        val response = restTemplate.exchange(url, HttpMethod.POST, request, String::class.java)
        return response.body!!
    }

    private fun getJsonUpdateRules(updateRulesDTO: UpdateRulesDTO): String {
        val gson = Gson()
        return gson.toJson(updateRulesDTO)
    }

    private fun getJsonTestCase(testCaseInput: TestCaseInput): String {
        val gson = Gson()
        return gson.toJson(testCaseInput)
    }

    private fun getJsonDefault(
        userId: String,
        language: String,
    ) = getJson(userId, "1.0.0", language).toString()

    override fun getVersion(token: String, language: String): String {
        val headers = BasicRest.getAuthHeaders(token)
        val entity = HttpEntity<String>(headers)
        val response = restTemplate.exchange("$snippetConfUrl/configuration/get_version/$language", HttpMethod.GET, entity, String::class.java)
        return response.body!!
    }
}
