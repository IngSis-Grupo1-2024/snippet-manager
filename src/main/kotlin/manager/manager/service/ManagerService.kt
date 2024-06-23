package manager.manager.service

import com.example.snippetmanager.snippet.CreateSnippet
import com.example.snippetmanager.snippet.UpdateSnippet
import manager.bucket.BucketAPI
import manager.common.rest.exception.NotFoundException
import manager.manager.model.dto.SnippetDto
import manager.manager.model.entity.Snippet
import manager.manager.model.enums.ComplianceSnippet
import manager.manager.repository.SnippetRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ManagerService
    @Autowired
    constructor(
        private val bucketAPI: BucketAPI,
        private val snippetRepository: SnippetRepository
    ): ManagerServiceSpec {
    override fun createSnippet(input: CreateSnippet): SnippetDto {
        val snippet = snippetRepository.save(Snippet(input.name, input.language, input.extension))
        bucketAPI.createSnippet(snippet.id.toString(), input.content)
        return SnippetDto(
            id=snippet.id!!,
            name=snippet.name,
            content = input.content,
            compliance = ComplianceSnippet.PENDING,
            author = "AUTHOR NAME TO DO",
            language = snippet.language,
            extension = snippet.extension,
        )
    }

    override fun getSnippet(snippetId: String): String {
        return bucketAPI.getSnippet(snippetId)
    }

    override fun deleteSnippet(snippetId: String) {
        bucketAPI.deleteSnippet(snippetId)
    }

    override fun updateSnippet(snippetId: String, input: UpdateSnippet): SnippetDto {
        if(input.content != null){
            val snippet = this.snippetRepository.findById(snippetId.toLong())
            if(snippet.isEmpty) throw NotFoundException("Snippet was not found")
            bucketAPI.deleteSnippet(snippetId)
            bucketAPI.createSnippet(snippetId, input.content)
            return SnippetDto(
                id=snippet.get().id!!,
                name=snippet.get().name,
                content = input.content,
                compliance = ComplianceSnippet.PENDING,
                author = "AUTHOR NAME TO DO",
                language = snippet.get().language,
                extension = snippet.get().extension,
            )
        }
        val snippet = this.snippetRepository.findById(snippetId.toLong())
        if(snippet.isEmpty) throw NotFoundException("Snippet was not found")
        return SnippetDto(
            id=snippet.get().id!!,
            name=snippet.get().name,
            content = "",
            compliance = ComplianceSnippet.PENDING,
            author = "AUTHOR NAME TO DO",
            language = snippet.get().language,
            extension = snippet.get().extension,
        )
    }
}