package manager.manager.model.entity

import jakarta.persistence.*
import manager.common.entity.BaseEntity

@Entity
data class UserSnippet(
    @Column
    var userId: String,
    @Column
    var name: String,
    @OneToMany(mappedBy = "userSnippet")
    val snippet: List<Snippet> = ArrayList()

): BaseEntity() {
    protected constructor() : this("", "")
}