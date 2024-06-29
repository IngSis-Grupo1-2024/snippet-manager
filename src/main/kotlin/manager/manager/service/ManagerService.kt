package manager.manager.service

import com.example.snippetmanager.snippet.UpdateSnippet
import manager.bucket.BucketAPI
import manager.common.rest.exception.BadReqException
import manager.common.rest.exception.NotFoundException
import manager.manager.integration.permission.SnippetPerm
import manager.manager.model.dto.*
import manager.manager.model.entity.Snippet
import manager.manager.model.entity.UserSnippet
import manager.manager.model.enums.ComplianceSnippet
import manager.manager.model.enums.FileType
import manager.manager.model.enums.PermissionType
import manager.manager.model.input.CreateSnippet
import manager.manager.model.input.ShareSnippetInput
import manager.manager.repository.SnippetRepository
import manager.manager.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ManagerService
    @Autowired
    constructor(
        private val bucketAPI: BucketAPI,
        private val snippetRepository: SnippetRepository,
        private val userRepository: UserRepository,
        private val snippetPerm: SnippetPerm,
    ) : ManagerServiceSpec {
        @Transactional
        override fun createSnippet(
            input: CreateSnippet,
            userId: String,
            token: String,
        ): SnippetDto {
            val user = userRepository.findByUserId(userId) ?: throw NotFoundException("User name was not found")
            val snippet = snippetRepository.save(Snippet(input.name, input.language, input.extension, user, ComplianceSnippet.PENDING))
            bucketAPI.createSnippet(snippet.id.toString(), input.content)
            addOwnerPermission(userId, snippet.id!!, token)
            return SnippetDto(
                id = snippet.id!!,
                name = snippet.name,
                content = input.content,
                compliance = ComplianceSnippet.PENDING,
                author = user.name,
                language = snippet.language,
                extension = snippet.extension,
            )
        }

        private fun addOwnerPermission(
            userId: String,
            snippetId: Long,
            token: String,
        ) {
            val addPermDto =
                AddPermDto(
                    permissionType = PermissionType.OWNER,
                    snippetId = snippetId,
                    userId = userId,
                    sharerId = "",
                )
            this.snippetPerm.addPermission(addPermDto, token)
        }

        override fun getSnippet(snippetId: String): SnippetDto {
            val snippet = snippetRepository.findById(snippetId.toLong())
            val content = bucketAPI.getSnippet(snippetId)
            return SnippetDto(
                id = snippet.get().id!!,
                name = snippet.get().name,
                content = content,
                compliance = ComplianceSnippet.PENDING,
                author = snippet.get().userSnippet.name,
                language = snippet.get().language,
                extension = snippet.get().extension,
            )
        }

        override fun deleteSnippet(
            userId: String,
            token: String,
            snippetId: String,
        ) {
            if (userIsNotTheOwner(snippetId, userId, token)) {
                throw BadReqException("The user has no permissions for updating the snippet")
            }
            this.snippetRepository.deleteById(snippetId.toLong())
            bucketAPI.deleteSnippet(snippetId)
        }

        override fun updateSnippet(
            snippetId: String,
            input: UpdateSnippet,
            userId: String,
            token: String,
        ): SnippetDto {
            if (userIsNotTheOwner(snippetId, userId, token)) {
                throw BadReqException("The user has no permissions for updating the snippet")
            }
            val snippet = this.snippetRepository.findById(snippetId.toLong())
            if (snippet.isEmpty) throw NotFoundException("Snippet was not found")
            bucketAPI.deleteSnippet(snippetId)
            bucketAPI.createSnippet(snippetId, input.content!!)
            return SnippetDto(
                id = snippet.get().id!!,
                name = snippet.get().name,
                content = input.content,
                compliance = ComplianceSnippet.PENDING,
                author = snippet.get().userSnippet.name,
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

        override fun saveName(
            name: String,
            userId: String,
        ): String {
            val user = userRepository.findByUserId(userId) ?: this.userRepository.save(UserSnippet(userId, name))
            user.name = name
            userRepository.save(user)
            return "User saved successfully"
        }

        override fun getSnippetDescriptors(
            userId: String,
            token: String,
        ): SnippetListDto {
            val user = userRepository.findByUserId(userId) ?: throw NotFoundException("User id was not found")
            val snippetsThatIOwn: SnippetListDto = getSnippetThatIOwn(user)
            val snippetShared: SnippetListDto = getSnippetsShared(userId, token)
            return snippetsThatIOwn.merge(snippetShared)
        }

        private fun getSnippetThatIOwn(user: UserSnippet): SnippetListDto =
            SnippetListDto(
                user.snippet.map { snippet: Snippet ->
                    val content = bucketAPI.getSnippet(snippet.id.toString())
                    SnippetDto(
                        id = snippet.id!!,
                        name = snippet.name,
                        content = content,
                        compliance = ComplianceSnippet.PENDING,
                        author = user.name,
                        language = snippet.language,
                        extension = snippet.extension,
                    )
                },
            )

        private fun getSnippetsShared(
            userId: String,
            token: String,
        ): SnippetListDto {
            val snippetIds = this.snippetPerm.getSharedSnippets(userId, token)
            return SnippetListDto(
                snippetIds.map { id: Long ->
                    val snippet = this.snippetRepository.findById(id)
                    val content = bucketAPI.getSnippet(id.toString())
                    SnippetDto(
                        id = id,
                        name = snippet.get().name,
                        content = content,
                        compliance = ComplianceSnippet.PENDING,
                        author = snippet.get().userSnippet.name,
                        language = snippet.get().language,
                        extension = snippet.get().extension,
                    )
                },
            )
        }

        override fun getUserFriends(userId: String): UsersDto {
            val users: List<UserSnippet> = this.userRepository.findAll()
            val userDtoList =
                users.filter { user -> user.userId != userId }
                    .map { user: UserSnippet ->
                        UserDto(user.name, user.userId)
                    }
            return UsersDto(userDtoList)
        }

        override fun shareSnippet(
            userId: String,
            shareSnippet: ShareSnippetInput,
            token: String,
        ): SnippetDto {
            val addPermDto = AddPermDto(PermissionType.R, shareSnippet.snippetId.toLong(), shareSnippet.userId, userId)
            snippetPerm.addPermission(addPermDto, token)
            return getSnippet(shareSnippet.snippetId)
        }

        private fun userIsNotTheOwner(
            snippetId: String,
            userId: String,
            token: String,
        ): Boolean {
            val permissionType: PermissionType = snippetPerm.getPermissionType(snippetId, userId, token)
            return permissionType != PermissionType.OWNER
        }
    }
