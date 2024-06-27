package manager.rules.integration.configuration

import manager.common.rest.BasicRest
import manager.manager.rules.model.input.ConfigInput.Companion.getJson
import manager.rules.model.dto.RulesOutput
import org.springframework.http.*
import org.springframework.web.client.*

class SnippetConfImpl(
    private val restTemplate: RestTemplate,
    private val snippetConfUrl: String
): SnippetConf {
    override fun createDefaultConf(userId: String, token: String, language: String): ResponseEntity<String> {
        val url = "$snippetConfUrl/configuration"
        val headers = BasicRest.getAuthHeaders(token)
        headers.contentType = MediaType.APPLICATION_JSON;

        val request = HttpEntity<String>(getJsonDefault(userId, language), headers)
        return restTemplate.postForEntity<String>(url, request)
    }

    private fun getJsonDefault(userId: String, language: String) =
        getJson(userId, "1.0.0", language).toString()
}