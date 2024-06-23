package manager.manager.model.entity

import jakarta.persistence.*
import manager.common.entity.BaseEntity
import manager.manager.model.enums.ComplianceSnippet
import manager.manager.model.enums.SnippetLanguage

@Entity
data class SnippetStatus(
    @ManyToOne
    @JoinColumn(name = "snippet_id", nullable = false)
    val snippet: Snippet,
    @Column
    @Enumerated(EnumType.STRING)
    var status: ComplianceSnippet
): BaseEntity() {
    protected constructor() :
            this(Snippet("", SnippetLanguage.PRINTSCRIPT, ""), ComplianceSnippet.PENDING)
}