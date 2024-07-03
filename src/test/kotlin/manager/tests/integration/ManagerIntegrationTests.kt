package manager.tests.integration

import manager.manager.integration.permission.SnippetPermImpl
import manager.manager.model.dto.AddPermDto
import manager.manager.model.enums.PermissionType
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureMockRestServiceServer
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.ResponseEntity
import org.springframework.test.context.TestPropertySource
import org.springframework.web.client.RestTemplate

@SpringBootTest(classes = [SnippetPermImpl::class])
@AutoConfigureMockRestServiceServer
@TestPropertySource(properties = ["snippet.perm.url=http://localhost:8080"]) // Replace with your actual URL
class SnippetPermImplTests {

    @MockBean
    lateinit var restTemplate: RestTemplate

    @Autowired
    lateinit var snippetPerm: SnippetPermImpl


    // Add similar tests for getPermissionType and getSharedSnippets
}
