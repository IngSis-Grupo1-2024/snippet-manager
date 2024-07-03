package rules.tests.model

import com.google.gson.JsonObject
import manager.rules.dto.RulesDTO
import manager.rules.model.dto.RulesOutput
import manager.rules.model.dto.UpdateRuleDTO
import manager.rules.model.dto.UpdateRulesDTO
import manager.manager.rules.model.input.ConfigInput
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class RulesModelTests {

    @Test
    fun `RulesDTO properties are correctly assigned`() {
        val rulesDTO = RulesDTO(id = 1L, name = "Test Rule", isActive = true, value = 10, parent = "Parent Rule")
        assertEquals(1L, rulesDTO.id)
        assertEquals("Test Rule", rulesDTO.name)
        assertEquals(true, rulesDTO.isActive)
        assertEquals(10, rulesDTO.value)
        assertEquals("Parent Rule", rulesDTO.parent)
    }

    @Test
    fun `RulesOutput contains the correct list of RulesDTO`() {
        val rulesList = listOf(
            RulesDTO(id = 1L, name = "Rule 1", isActive = true, value = 10, parent = "Parent 1"),
            RulesDTO(id = 2L, name = "Rule 2", isActive = false, value = 20, parent = "Parent 2")
        )
        val rulesOutput = RulesOutput(rules = rulesList)
        assertEquals(2, rulesOutput.rules.size)
        assertEquals(rulesList, rulesOutput.rules)
    }

    @Test
    fun `UpdateRuleDTO properties are correctly assigned`() {
        val updateRuleDTO = UpdateRuleDTO(id = 1, name = "Update Rule", value = 100, isActive = true)
        assertEquals(1, updateRuleDTO.id)
        assertEquals("Update Rule", updateRuleDTO.name)
        assertEquals(100, updateRuleDTO.value)
        assertEquals(true, updateRuleDTO.isActive)
    }

    @Test
    fun `UpdateRulesDTO contains the correct list of UpdateRuleDTO and type`() {
        val updateRulesList = listOf(
            UpdateRuleDTO(id = 1, name = "Update Rule 1", value = 100, isActive = true),
            UpdateRuleDTO(id = 2, name = "Update Rule 2", value = 200, isActive = false)
        )
        val updateRulesDTO = UpdateRulesDTO(rules = updateRulesList, type = "LINTING")
        assertEquals(2, updateRulesDTO.rules.size)
        assertEquals(updateRulesList, updateRulesDTO.rules)
        assertEquals("LINTING", updateRulesDTO.type)
    }

    @Test
    fun `ConfigInput getJson returns correct JsonObject`() {
        val json = ConfigInput.getJson(userId = "user123", version = "v1", language = "Kotlin")
        assertEquals("user123", json.get("userId").asString)
        assertEquals("v1", json.get("version").asString)
        assertEquals("Kotlin", json.get("language").asString)
    }
}
