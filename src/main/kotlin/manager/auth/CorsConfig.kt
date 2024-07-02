package manager.auth

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration

@Configuration
class CorsConfig {
    @Bean
    fun corsFilter(): CorsConfiguration {
        val config = CorsConfiguration()
        config.allowCredentials = false
        config.allowedOrigins = listOf("http://localhost:5173", "https://snippet-runner-dev.duckdns.org/*")
        config.allowedMethods = listOf("*")
        config.allowedHeaders = listOf("*")
        return config
    }
}
