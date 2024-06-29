package manager.runner.manager

import manager.common.rest.ResponseOutput
import manager.common.rest.dto.Output
import org.springframework.http.HttpEntity
import org.springframework.web.client.RestTemplate
import snippet.FormatInput
import snippet.SnippetInfo

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

    override fun formatSnippet(snippet: FormatInput): Output {
        val url = "$runnerUrl/execute/formatSnippet"
        val response = rest.postForEntity(url, HttpEntity(snippet), ResponseOutput::class.java)

        if (response.statusCode.is2xxSuccessful) {
            return response.body!!
        } else {
            throw Exception("Error (${response.statusCode}) : ${response.body}")
        }
    }
}
