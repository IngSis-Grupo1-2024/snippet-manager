package runner.service

import runner.manager.RunnerManager
import com.example.snippetmanager.snippet.SnippetInfo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import runner.RunningOutput

@Service
class RunnerService
    @Autowired constructor(private val runnerManager: RunnerManager) {
    fun runSnippet(snippet: SnippetInfo): RunningOutput {
        // logic for checking permissions
        return runnerManager.runSnippet(snippet)
    }
}