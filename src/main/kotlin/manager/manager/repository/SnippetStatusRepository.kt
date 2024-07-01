package manager.manager.repository

import manager.manager.model.entity.SnippetStatus
import manager.manager.model.enums.ComplianceSnippet
import org.springframework.data.jpa.repository.JpaRepository

interface SnippetStatusRepository : JpaRepository<SnippetStatus, String>{
    fun findByStatus(status: ComplianceSnippet): SnippetStatus?
}
