package manager.runner.manager

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class RunnerManagerConfig
    @Autowired
    constructor(
        private val rest: RestTemplate,
        @Value("\${manager.runner.url}")
        private val runnerUrl: String,
    ) {
        @Bean
        fun createRunnerManager(): Runner {
            return RunnerManager(rest, runnerUrl)
        }
    }
