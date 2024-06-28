package manager.rules.integration.configuration

import com.example.demo.testCase.model.dto.TestCaseDto
import com.nimbusds.jose.shaded.gson.JsonArray
import com.nimbusds.jose.shaded.gson.JsonObject
import manager.common.rest.BasicRest
import manager.manager.rules.model.input.ConfigInput.Companion.getJson
import manager.rules.model.dto.RulesOutput
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

        getJsonArray(testCaseInput.input!!)

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

    private fun getJsonTestCase(testCaseInput: TestCaseInput): String {
        val json = JsonObject()
        json.addProperty("id", testCaseInput.id)
        json.addProperty("snippetId", testCaseInput.snippetId)
        json.addProperty("name", testCaseInput.name)
        if (testCaseInput.input.isNullOrEmpty()) {
            json.add("input", getJsonArray(emptyList()))
        } else {
            json.add("input", getJsonArray(testCaseInput.input))
        }

        if (testCaseInput.output.isNullOrEmpty()) {
            json.add("input", getJsonArray(emptyList()))
        } else {
            json.add("output", getJsonArray(testCaseInput.output))
        }

        json.addProperty("envVars", testCaseInput.envVars)

        return json.toString()
    }

    override fun getRules(userId: String,
                          token: String,
                          type: String): RulesOutput {
        val url = "$snippetConfUrl/configuration/rules?userId=$userId&ruleType=$type"
        val headers = BasicRest.getAuthHeaders(token)
        val entity = HttpEntity<String>(headers)
        val response = restTemplate.exchange(url, HttpMethod.GET, entity, RulesOutput::class.java)
        return response.body!!
    }

    private fun getJsonDefault(
        userId: String,
        language: String,
    ) = getJson(userId, "1.0.0", language).toString()

    private fun getJsonArray(list: List<String>): JsonArray {
        val array = JsonArray(list.size)
        list.forEach { array.add(it) }
        return array
    }
}
