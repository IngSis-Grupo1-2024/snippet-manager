package manager.rules.dto

class RulesDTO(
    var name: String,
    var isActive: Boolean,
    var value: Int?,
    val parent: String,
    val id: Long
)
