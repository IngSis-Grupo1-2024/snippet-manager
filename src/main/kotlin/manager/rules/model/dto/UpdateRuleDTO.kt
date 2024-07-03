package manager.rules.model.dto

class UpdateRuleDTO(
    val id: Int,
    val name: String,
    var value: Int?,
    var isActive: Boolean = false,
    var parent: String,
)
