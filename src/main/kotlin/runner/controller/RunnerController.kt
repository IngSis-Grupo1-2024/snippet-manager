package runner.controller

import com.example.snippetmanager.snippet.SnippetInfo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RestController
import runner.RunningOutput
import runner.service.RunnerService

@RestController
class RunnerController
@Autowired constructor(private val runnerService: RunnerService) {
    fun runSnippet(content: SnippetInfo) : RunningOutput {
        return runnerService.runSnippet(content)
    }
}