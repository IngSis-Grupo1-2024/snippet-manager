package manager.manager.model.entity

import jakarta.persistence.*
import manager.common.entity.CommonEntity
import manager.manager.model.enums.ComplianceSnippet
import manager.manager.model.enums.SnippetLanguage

@Entity
data class Snippet(
    @Column
    var name: String,
    @Column
    @Enumerated(EnumType.STRING)
    var language: SnippetLanguage,
    @Column
    var extension: String,
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    var userSnippet: UserSnippet,
    @Column
    @Enumerated(EnumType.STRING)
    var status: ComplianceSnippet
): CommonEntity() {
    protected constructor() : this("", SnippetLanguage.PRINTSCRIPT, "", UserSnippet("", ""), ComplianceSnippet.PENDING)
}