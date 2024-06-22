package manager.rules.model.dto

import manager.rest.dto.Output
import manager.rules.dto.RulesDTO

class RulesOutput(
    val rules: List<RulesDTO>
): Output