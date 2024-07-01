package manager.manager.repository

import manager.manager.model.entity.SnippetStatus
import org.springframework.data.jpa.repository.JpaRepository

interface SnippetStatusRepository : JpaRepository<SnippetStatus, String>
