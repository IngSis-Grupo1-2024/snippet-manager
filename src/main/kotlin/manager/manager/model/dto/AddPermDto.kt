package manager.manager.model.dto

import manager.manager.model.enums.PermissionType

data class AddPermDto(
    val permissionType: PermissionType,
    val snippetId: Long,
    val userId: String,
    val sharerId: String,
)