package manager.manager.integration.permission

import manager.manager.model.dto.AddPermDto

interface SnippetPerm {
    fun addPermission(addPermDto: AddPermDto, token: String)
//    fun isAllowed(permissionType: PermissionType, snippetId: Int, userId: String): ResponseEntity<Boolean>
//    fun getPermissionType(snippetId: Int, userId: String): ResponseEntity<PermissionType>
    fun getSharedSnippets(userId: String, token: String): List<Long>
}