package manager.manager.service

import com.example.snippetmanager.snippet.CreateSnippet
import com.example.snippetmanager.snippet.UpdateSnippet
import manager.bucket.BucketAPI
import manager.common.rest.exception.NotFoundException
import manager.manager.model.dto.SnippetDto
import manager.manager.model.entity.Snippet
import manager.manager.repository.SnippetRepository
import manager.rules.integration.configuration.SnippetConf
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class ManagerService
    @Autowired
    constructor(
        private val bucketAPI: BucketAPI,
        private val snippetRepository: SnippetRepository
    ): ManagerServiceSpec {
    override fun createSnippet(snippetContent: CreateSnippet): SnippetDto {
        val snippet = snippetRepository.save(Snippet(snippetContent.name, snippetContent.language))
        bucketAPI.createSnippet(snippet.id.toString(), snippetContent.content)
        return SnippetDto(
            id=snippet.id!!,
            name=snippet.name,
            content = snippetContent.content,
            language = snippet.language,
            createdAt = snippet.createdAt!!,
            updateAt = snippet.updatedAt!!
        )
    }

    override fun getSnippet(snippetId: String): String {
        return bucketAPI.getSnippet(snippetId)
    }

    override fun deleteSnippet(snippetId: String) {
        bucketAPI.deleteSnippet(snippetId)
    }

    override fun updateSnippet(snippetId: String, snippetContent: UpdateSnippet): SnippetDto {
        if(snippetContent.content != null){
            val snippet = this.snippetRepository.findById(snippetId.toLong())
            if(snippet.isEmpty) throw NotFoundException("Snippet was not found")
            bucketAPI.deleteSnippet(snippetId)
            bucketAPI.createSnippet(snippetId, snippetContent.content)
            return SnippetDto(
                id=snippet.get().id!!,
                name=snippet.get().name,
                content = snippetContent.content,
                language = snippet.get().language,
                createdAt = snippet.get().createdAt!!,
                updateAt = snippet.get().updatedAt!!
            )
        }
        val snippet = this.snippetRepository.findById(snippetId.toLong())
        if(snippet.isEmpty) throw NotFoundException("Snippet was not found")
        return SnippetDto(
            id=snippet.get().id!!,
            name=snippet.get().name,
            content = "",
            language = snippet.get().language,
            createdAt = snippet.get().createdAt!!,
            updateAt = snippet.get().updatedAt!!
        )
    }
}