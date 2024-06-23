package manager.manager.service

import manager.manager.model.input.CreateSnippet
import com.example.snippetmanager.snippet.UpdateSnippet
import manager.bucket.BucketAPI
import manager.common.rest.exception.NotFoundException
import manager.manager.model.dto.FileTypeDto
import manager.manager.model.dto.SnippetDto
import manager.manager.model.dto.SnippetListDto
import manager.manager.model.entity.Snippet
import manager.manager.model.entity.UserSnippet
import manager.manager.model.enums.ComplianceSnippet
import manager.manager.model.enums.FileType
import manager.manager.repository.SnippetRepository
import manager.manager.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ManagerService
    @Autowired
    constructor(
        private val bucketAPI: BucketAPI,
        private val snippetRepository: SnippetRepository,
        private val userRepository: UserRepository
    ): ManagerServiceSpec {
    override fun createSnippet(input: CreateSnippet, userId: String): SnippetDto {
        val user = userRepository.findByUserId(userId) ?: throw NotFoundException("User name was not found")
        val snippet = snippetRepository.save(Snippet(input.name, input.language, input.extension, user, ComplianceSnippet.PENDING))
        bucketAPI.createSnippet(snippet.id.toString(), input.content)
        return SnippetDto(
            id=snippet.id!!,
            name=snippet.name,
            content = input.content,
            compliance = ComplianceSnippet.PENDING,
            author = user.name,
            language = snippet.language,
            extension = snippet.extension,
        )
    }

    override fun getSnippet(snippetId: String): SnippetDto {
        val snippet = snippetRepository.findById(snippetId.toLong())
        val content =  bucketAPI.getSnippet(snippetId)
        return SnippetDto(
            id=snippet.get().id!!,
            name=snippet.get().name,
            content = content,
            compliance = ComplianceSnippet.PENDING,
            author = snippet.get().userSnippet.name,
            language = snippet.get().language,
            extension = snippet.get().extension,
        )
    }

    override fun deleteSnippet(snippetId: String) {
        this.snippetRepository.deleteById(snippetId.toLong())
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
                author = snippet.get().userSnippet.name,
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

    override fun getFileTypes(): List<FileTypeDto> {
        val fileTypes = FileType.getFileTypes()
        return fileTypes.map { (language, extension) ->
            FileTypeDto(language.toString(), extension)
        }
    }

    override fun saveName(name: String, userId: String): String {
        val user = userRepository.findByUserId(userId) ?: this.userRepository.save(UserSnippet(userId, name))
        user.name = name
        userRepository.save(user)
        return "User saved successfully"
    }

    override fun getSnippetDescriptors(userId: String): SnippetListDto {
        val user = userRepository.findByUserId(userId) ?: throw NotFoundException("User id was not found")
        return SnippetListDto(user.snippet.map { snippet: Snippet ->
            val content = bucketAPI.getSnippet(snippet.id.toString())
            SnippetDto(
                id=snippet.id!!,
                name=snippet.name,
                content = content,
                compliance = ComplianceSnippet.PENDING,
                author = user.name,
                language = snippet.language,
                extension = snippet.extension,
            )
        })
    }
}