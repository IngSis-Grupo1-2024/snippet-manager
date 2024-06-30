package manager.manager.model

import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import manager.common.entity.CommonEntity
import manager.manager.model.entity.Snippet
import manager.manager.model.enums.ComplianceSnippet

@Entity
data class SnippetStatus(
    @Enumerated(EnumType.STRING)
    var status: ComplianceSnippet,
    @ManyToOne
    @JoinColumn(name = "snippet_id", nullable = false)
    val snippet: Snippet,
) : CommonEntity() {
    constructor() : this(ComplianceSnippet.PENDING, Snippet())
}
