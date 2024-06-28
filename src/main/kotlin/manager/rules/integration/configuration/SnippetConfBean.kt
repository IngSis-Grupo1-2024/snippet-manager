package manager.rules.integration.configuration

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class SnippetConfBean
    @Autowired
    constructor(
        private val restTemplate: RestTemplate,
        @Value("\${snippet_conf_url}")
        private val snippetConfUrl: String,
    ) {
        @Bean
        fun createSnippetConf(): SnippetConf {
            return SnippetConfImpl(restTemplate, snippetConfUrl)
        }
    }
