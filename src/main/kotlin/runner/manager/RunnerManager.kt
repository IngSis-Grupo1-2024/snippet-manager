package runner.manager

import com.example.snippetmanager.snippet.SnippetInfo
import org.springframework.http.HttpEntity
import org.springframework.web.client.RestTemplate
import runner.RunningOutput

class RunnerManager(val rest: RestTemplate, val runnerUrl: String) {
    fun runSnippet(snippet: SnippetInfo): RunningOutput {
        val url = "$runnerUrl/execute/executeSnippet"
        val response = rest.postForEntity(url, HttpEntity(snippet), RunningOutput::class.java)

        if (response.statusCode.is2xxSuccessful) {
            return response.body!!
        } else {
            throw Exception("An error (${response.statusCode}) occurred: ${response.body}")
        }
    }
}
