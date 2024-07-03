package manager.manager.model.entity

import jakarta.persistence.*
import manager.common.entity.CommonEntity

@Entity
data class UserSnippet(
    @Column
    var userId: String,
    @Column
    var name: String,
    @OneToMany(mappedBy = "userSnippet")
    var snippet: List<Snippet> = ArrayList(),
) : CommonEntity() {
    protected constructor() : this("", "")
}
