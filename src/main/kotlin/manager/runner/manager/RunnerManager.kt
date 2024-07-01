package manager.runner.manager

import manager.common.rest.dto.Output
import manager.snippet.FormatInput
import manager.snippet.RunningOutput
import manager.snippet.SnippetInfo
import org.springframework.http.*
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.web.client.RestTemplate

class RunnerManager(val rest: RestTemplate, val runnerUrl: String) : Runner {
    override fun runSnippet(snippet: SnippetInfo): Output {
        val url = "$runnerUrl/execute/executeSnippet"
        val response = rest.postForEntity(url, HttpEntity(snippet), Output::class.java)

        if (response.statusCode.is2xxSuccessful) {
            return response.body!!
        } else {
            throw Exception("An error (${response.statusCode}) occurred: ${response.body}")
        }
    }

    override fun formatSnippet(
        token: String,
        snippet: FormatInput,
    ): RunningOutput {
        val url = "$runnerUrl/execute/formatSnippet"

        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        headers.setBearerAuth(token)
        val entity = HttpEntity(snippet, headers)

        val response = RestTemplate().exchange(url, HttpMethod.POST, entity, RunningOutput::class.java)

        if (response.statusCode.is2xxSuccessful) {
            return response.body!!
        } else {
            throw Exception("Error (${response.statusCode}) : ${response.body}")
        }
    }
}
