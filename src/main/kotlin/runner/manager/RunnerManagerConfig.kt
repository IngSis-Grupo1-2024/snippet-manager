package runner.manager

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class RunnerManagerConfig
    @Autowired
    constructor(
        rest: RestTemplate,
        @Value("\${runner.url}") runnerUrl: String,
    ) {
        init {
            @Bean
            fun createRunnerManager(): RunnerManager {
                return RunnerManager(rest, runnerUrl)
            }
        }
    }
