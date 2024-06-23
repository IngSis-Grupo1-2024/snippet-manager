package manager.manager.model.entity

import jakarta.persistence.*
import manager.common.entity.BaseEntity
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
): BaseEntity() {
    protected constructor() : this("", SnippetLanguage.PRINTSCRIPT, "")
}