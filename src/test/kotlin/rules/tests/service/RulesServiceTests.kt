package rules.tests.service

import manager.common.bucket.BucketAPI
import manager.common.rest.exception.NotFoundException
import manager.manager.model.entity.Snippet
import manager.manager.model.entity.UserSnippet
import manager.manager.model.enums.SnippetLanguage
import manager.manager.repository.UserRepository
import manager.redis.producer.LintProducer
import manager.rules.dto.RulesDTO
import manager.rules.integration.configuration.SnippetConf
import manager.rules.model.dto.RulesOutput
import manager.rules.model.dto.UpdateRulesDTO
import manager.rules.service.RulesService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus

@ExtendWith(MockitoExtension::class)
class RulesServiceTest {

    @Mock
    private lateinit var snippetConf: SnippetConf

    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var bucketAPI: BucketAPI

    @Mock
    private lateinit var lintProducer: LintProducer

    @InjectMocks
    private lateinit var rulesService: RulesService

    @Test
    fun `createDefaultConf should call createDefaultConf for each SnippetLanguage and return ok`() {
        // Arrange
        val userId = "user123"
        val token = "token123"

        // Act
        val response = rulesService.createDefaultConf(userId, token)

        // Assert
        SnippetLanguage.entries.forEach { snippetLanguage ->
            verify(snippetConf).createDefaultConf(userId, token, snippetLanguage.toString())
        }
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals("", response.body)
    }

    @Test
    fun `updateRules should update rules and publish events for LINTING type`() {
        // Arrange
        val userId = "user123"
        val tokenValue = "token123"
        val updateRulesDTO = UpdateRulesDTO(listOf(),"LINTING")
        val user = UserSnippet("1", "chulo", listOf(Snippet()))
        whenever(userRepository.findByUserId(userId)).thenReturn(user)

        // Act
        rulesService.updateRules(updateRulesDTO, userId, tokenValue)

        // Assert
        verify(snippetConf).updateRules(updateRulesDTO, userId, tokenValue)
    }

    @Test
    fun `updateRules should throw NotFoundException when user is not found`() {
        // Arrange
        val userId = "userNotFound"
        val tokenValue = "token123"
        val updateRulesDTO = UpdateRulesDTO(listOf(),"LINTING")
        whenever(userRepository.findByUserId(userId)).thenReturn(null)

        // Act & Assert
        assertThrows<NotFoundException> {
            rulesService.updateRules(updateRulesDTO, userId, tokenValue)
        }
    }

    @Test
    fun `getLintingRules should return expected rules output`() {
        // Arrange
        val userId = "user123"
        val tokenValue = "token123"
        val expectedRules = listOf(RulesDTO(1L, "expression",true, 1, "parent"))
        whenever(snippetConf.getRules(userId, tokenValue, "LINTING")).thenReturn(RulesOutput(expectedRules))

        // Act
        val result = rulesService.getLintingRules(userId, tokenValue)

        // Assert
        assertEquals(expectedRules.size, result.rules.size)
        val rules = expectedRules[0]
        assertEquals(rules.name + " in " + rules.parent, result.rules[0].name)
    }

    @Test
    fun `getFormattingRules should return expected rules output`() {
        // Arrange
        val userId = "user123"
        val tokenValue = "token123"
        val expectedRules = listOf(RulesDTO(1L, "expression",true, 1, "parent"))
        whenever(snippetConf.getRules(userId, tokenValue, "FORMATTING")).thenReturn(RulesOutput(expectedRules))

        // Act
        val result = rulesService.getFormattingRules(userId, tokenValue)

        // Assert
        assertEquals(expectedRules.size, result.rules.size)
        assertEquals(expectedRules[0].name, result.rules[0].name)
    }
}
