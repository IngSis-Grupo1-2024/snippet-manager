package auth.tests

import manager.auth.AudienceValidator
import manager.auth.OAuth2ResourceServerSecurityConfiguration
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.oauth2.jwt.Jwt

@SpringBootTest(classes = [OAuth2ResourceServerSecurityConfiguration::class])
@AutoConfigureMockMvc
class AuthTests {

    private val audience = "expectedAudience"

    @Test
    fun `validate should succeed when audience matches`() {
        val jwt = mock(Jwt::class.java)
        `when`(jwt.audience).thenReturn(listOf(audience))

        val validator = AudienceValidator(audience)

        val result = validator.validate(jwt)

        assertFalse(result.hasErrors())
    }

    @Test
    fun `validate should fail when audience does not match`() {
        val jwt = mock(Jwt::class.java)
        `when`(jwt.audience).thenReturn(listOf("unexpectedAudience"))

        val validator = AudienceValidator(audience)

        val result = validator.validate(jwt)

        assertTrue(result.hasErrors())
    }

}
