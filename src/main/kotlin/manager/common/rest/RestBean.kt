package manager.manager.rest

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

@Configuration
class RestBean {
    @Bean
    fun restTemplate(): RestTemplate {
        return RestTemplate()
    }
}
