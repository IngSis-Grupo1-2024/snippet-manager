package manager.manager.integration.permission

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class SnippetPermBean
@Autowired
constructor(
    private val restTemplate: RestTemplate,
    @Value("\${snippet_perm_url}")
    private val snippetPermUrl: String
){
    @Bean
    fun createSnippetPerm(): SnippetPerm {
        return SnippetPermImpl(restTemplate, snippetPermUrl)
    }
}