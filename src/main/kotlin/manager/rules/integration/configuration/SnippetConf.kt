package manager.rules.integration.configuration

import manager.rules.model.dto.RulesOutput
import org.springframework.http.ResponseEntity

interface SnippetConf {
    fun createDefaultConf(userId: String, token: String): ResponseEntity<String>
//    fun updateRule(updateRuleInput: UpdateRuleInput)
//    fun updateVersion(versionInput: VersionInput)
    fun getRules(userId: String, token: String, type: String): RulesOutput
}