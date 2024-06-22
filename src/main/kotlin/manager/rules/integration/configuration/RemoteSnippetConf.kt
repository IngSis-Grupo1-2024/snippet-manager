package manager.rules.integration.configuration

import manager.manager.rules.model.input.ConfigInput.Companion.getJson
import manager.rules.model.dto.RulesOutput
import org.springframework.http.*
import org.springframework.web.client.*

class RemoteSnippetConf(
    private val restTemplate: RestTemplate,
    private val snippetConfUrl: String
): SnippetConf {
    override fun createDefaultConf(userId: String, token: String): ResponseEntity<String> {
        val url = "$snippetConfUrl/configuration"
        val headers = manager.manager.rest.BasicRest.getAuthHeaders(token)
        headers.contentType = MediaType.APPLICATION_JSON;

        val request = HttpEntity<String>(getJsonDefault(userId), headers)
        return restTemplate.postForEntity<String>(url, request)
    }

    override fun getRules(userId: String,
                          token: String,
                          type: String): RulesOutput {
        val url = "$snippetConfUrl/configuration/rules?userId=$userId&ruleType=$type"
        val headers = manager.manager.rest.BasicRest.getAuthHeaders(token)
        val entity = HttpEntity<String>(headers)
        val response = restTemplate.exchange(url, HttpMethod.GET, entity, RulesOutput::class.java)
        return response.body!!
    }

    private fun getJsonDefault(userId: String) =
        getJson(userId, "1.0.0").toString()
}