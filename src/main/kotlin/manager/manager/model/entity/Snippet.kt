package manager.manager.model.entity

import jakarta.persistence.*
import manager.common.entity.CommonEntity
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
    @ManyToOne
    @JoinColumn(name = "status_id", nullable = false)
    var status: SnippetStatus,
) : CommonEntity() {
    constructor() : this("", SnippetLanguage.PRINTSCRIPT, "", UserSnippet("", ""), SnippetStatus())
}
