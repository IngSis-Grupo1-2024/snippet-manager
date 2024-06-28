package manager.manager.repository

import manager.manager.model.SnippetStatusByUser
import manager.manager.model.enums.ComplianceSnippet
import org.springframework.data.jpa.repository.JpaRepository

interface SnippetStatusByUserRepository : JpaRepository<SnippetStatusByUser, String> {
    fun updateSnippetForUser(
        userId: String,
        snippetId: String,
    ): ComplianceSnippet
}
