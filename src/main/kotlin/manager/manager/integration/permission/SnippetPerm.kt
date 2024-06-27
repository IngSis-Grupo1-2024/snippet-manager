package manager.manager.integration.permission

import manager.manager.model.dto.AddPermDto
import manager.manager.model.enums.PermissionType

interface SnippetPerm {
    fun addPermission(addPermDto: AddPermDto, token: String)
//    fun isAllowed(permissionType: PermissionType, snippetId: Int, userId: String): ResponseEntity<Boolean>
    fun getPermissionType(snippetId: String, userId: String, token: String): PermissionType
    fun getSharedSnippets(userId: String, token: String): List<Long>
}