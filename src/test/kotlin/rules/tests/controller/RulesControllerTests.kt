package rules.tests.controller

import manager.common.rest.exception.NotFoundException
import manager.rules.controller.RulesController
import manager.rules.model.dto.RulesOutput
import manager.rules.model.dto.UpdateRulesDTO
import manager.rules.service.RulesService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.oauth2.jwt.Jwt

@ExtendWith(MockitoExtension::class)
class RulesControllerTests {

    @Mock
    private lateinit var rulesService: RulesService

    @Mock
    private lateinit var jwt: Jwt

    @InjectMocks
    private lateinit var rulesController: RulesController

    @BeforeEach
    fun setUp() {
        doReturn("user123").`when`(jwt).subject
        doReturn("token123").`when`(jwt).tokenValue
    }

    @Test
    fun createDefaultConfigurationReturnsOk() {
        doReturn(ResponseEntity.ok("")).`when`(rulesService).createDefaultConf(any(), any())
        val response = rulesController.createDefaultConfiguration(jwt)
        verify(rulesService).createDefaultConf("user123", "token123")
        assert(response.statusCode == HttpStatus.OK)
    }

    @Test
    fun getLintingRulesReturnsCorrectRules() {
        val rulesOutput = RulesOutput(emptyList())
        doReturn(rulesOutput).`when`(rulesService).getLintingRules(any(), any())

        val response = rulesController.getLintingRules(jwt)

        verify(rulesService).getLintingRules("user123", "token123")
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(rulesOutput, response.body)
    }

    @Test
    fun getFormattingRulesReturnsCorrectRules() {
        val rulesOutput = RulesOutput(emptyList())
        doReturn(rulesOutput).`when`(rulesService).getFormattingRules(any(), any())
        val response = rulesController.getFormattingRules(jwt)
        verify(rulesService).getFormattingRules("user123", "token123")
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(rulesOutput, response.body)
    }

    @Test
    fun updateRulesByTypeReturnsOkForValidUpdate() {
        doNothing().`when`(rulesService).updateRules(any(), any(), any())
        val updateRulesDTO = UpdateRulesDTO(emptyList(), "LINTING")

        val response = rulesController.updateRulesByType(jwt, updateRulesDTO)

        verify(rulesService).updateRules(updateRulesDTO, "user123", "token123")
        assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun updateRulesByTypeReturnsNotFoundForInvalidUser() {
        doThrow(NotFoundException("User not found")).`when`(rulesService).updateRules(any(), any(), any())
        val updateRulesDTO = UpdateRulesDTO(emptyList(), "LINTING")
        val response = rulesController.updateRulesByType(jwt, updateRulesDTO)
        assert(response.statusCode == HttpStatus.NOT_FOUND)
        assert(response.body == "User not found")
    }
}
