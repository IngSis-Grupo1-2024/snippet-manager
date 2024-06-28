package manager.auth

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration

@Configuration
class CorsConfig {
    @Bean
    fun corsFilter(): CorsConfiguration {
        val config = CorsConfiguration()
        config.allowCredentials = true
        config.allowedOrigins = listOf("http://localhost:5173")
        config.allowedMethods = listOf("*")
        config.allowedHeaders = listOf("*")
        return config
    }
}
