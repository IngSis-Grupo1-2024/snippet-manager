package manager.rules.dto

class RulesDTO(
    val id: Long,
    var name: String,
    var isActive: Boolean,
    var value: Int?,
    val parent: String,
)
