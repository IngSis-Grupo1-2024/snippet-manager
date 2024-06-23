package manager.manager.model.entity

import jakarta.persistence.*
import manager.common.entity.BaseEntity

@Entity
data class Snippet(
    @Column
    var name: String,
    @Column
    @Enumerated(EnumType.STRING)
    var language: SnippetLanguage
): BaseEntity() {
    protected constructor() : this("", SnippetLanguage.PRINTSCRIPT)
}