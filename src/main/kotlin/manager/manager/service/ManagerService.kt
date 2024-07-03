package manager.manager.service

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import manager.common.bucket.BucketAPI
import manager.common.rest.exception.BadReqException
import manager.common.rest.exception.NotFoundException
import manager.manager.controller.ManagerController
import manager.manager.integration.permission.SnippetPerm
import manager.manager.model.dto.*
import manager.manager.model.entity.Snippet
import manager.manager.model.entity.SnippetStatus
import manager.manager.model.entity.UserSnippet
import manager.manager.model.enums.ComplianceSnippet
import manager.manager.model.enums.FileType
import manager.manager.model.enums.PermissionType
import manager.manager.model.input.CreateSnippet
import manager.manager.model.input.ShareSnippetInput
import manager.manager.repository.SnippetRepository
import manager.manager.repository.SnippetStatusRepository
import manager.manager.repository.UserRepository
import manager.redis.producer.LintProducer
import manager.rules.integration.configuration.SnippetConf
import manager.rules.model.dto.RulesOutput
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class ManagerService
    @Autowired
    constructor(
        private val bucketAPI: BucketAPI,
        private val snippetRepository: SnippetRepository,
        private val userRepository: UserRepository,
        private val snippetPerm: SnippetPerm,
        private val snippetStatusRepository: SnippetStatusRepository,
        private val lintProducer: LintProducer,
        private val snippetConf: SnippetConf,
    ) : ManagerServiceSpec {
        private val logger = LoggerFactory.getLogger(ManagerController::class.java)

        @Transactional
        override fun createSnippet(
            input: CreateSnippet,
            userId: String,
            token: String,
        ): SnippetDto {
            val user = userRepository.findByUserId(userId) ?: throw NotFoundException("User name was not found")
            val status = getSnippetStatus(ComplianceSnippet.PENDING)

            val snippet = saveSnippet(input, user, status, token)

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

        override fun getSnippet(snippetId: String): SnippetDto {
            val snippet = snippetRepository.findById(snippetId.toLong())
            logger.info("Getting snippet content from bucket, snippetId = $snippetId")
            val content = bucketAPI.getSnippet(snippetId)
            return snippetDto(snippet.get().id!!, snippet, content)
        }

        @Transactional
        override fun deleteSnippet(
            userId: String,
            token: String,
            snippetId: String,
        ) {
            if (userIsNotTheOwner(snippetId, userId, token)) {
                throw BadReqException("The user has no permissions for updating the snippet")
            }
            this.snippetRepository.deleteById(snippetId.toLong())
            logger.info("Deleting snippet content from bucket, snippetId = $snippetId")
            bucketAPI.deleteSnippet(snippetId)
        }

        override fun updateSnippet(
            snippetId: String,
            newContent: String,
            userId: String,
            token: String,
        ): SnippetDto {
            if (userIsNotTheOwner(snippetId, userId, token)) {
                throw BadReqException("The user has no permissions for updating the snippet")
            }

            val snippet = getSnippetByID(snippetId)

            updateContentInBucket(snippetId, newContent)
            publishEventBySnippet(snippetId, userId, token)
            return snippetDto(snippet.get().id!!, snippet, newContent)
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
            if (userIsNotTheOwner(shareSnippet.snippetId, userId, token)) {
                throw BadReqException("The user has no permissions for updating the snippet")
            }
            val addPermDto = AddPermDto(PermissionType.R, shareSnippet.snippetId.toLong(), shareSnippet.userId, userId)
            snippetPerm.addPermission(addPermDto, token)
            return getSnippet(shareSnippet.snippetId)
        }

        override fun updateSnippetStatus(
            userId: String,
            snippetId: String,
            complianceSnippet: ComplianceSnippet,
        ): ComplianceSnippet {
            logger.info("Updating snippet status for snippet $snippetId to $complianceSnippet")
            val snippet = getSnippetByID(snippetId)
            val status = getSnippetStatus(complianceSnippet)
            snippet.get().status = status
            this.snippetRepository.save(snippet.get())
            return snippet.get().status.status
        }

        fun getSnippetThatIOwn(user: UserSnippet): SnippetListDto =
            SnippetListDto(
                user.snippet.map { snippet: Snippet ->
                    val content = bucketAPI.getSnippet(snippet.id.toString())
                    SnippetDto(
                        id = snippet.id!!,
                        name = snippet.name,
                        content = content,
                        compliance = snippet.status.status,
                        author = user.name,
                        language = snippet.language,
                        extension = snippet.extension,
                    )
                },
            )

        private fun getSnippetStatus(complianceSnippet: ComplianceSnippet): SnippetStatus {
            val status =
                snippetStatusRepository.findByStatus(complianceSnippet) ?: snippetStatusRepository.save(
                    SnippetStatus(complianceSnippet),
                )
            return status
        }

        private fun userIsNotTheOwner(
            snippetId: String,
            userId: String,
            token: String,
        ): Boolean {
            val permissionType: PermissionType = snippetPerm.getPermissionType(snippetId, userId, token)
            return permissionType != PermissionType.OWNER
        }

        private fun publishEventBySnippet(
            snippetId: String,
            userId: String,
            token: String,
        ) {
            val snippet: Snippet = this.snippetRepository.findById(snippetId.toLong()).get()
            val snippetContent: String = bucketAPI.getSnippet(snippetId)
            publishLintEvent(userId, token, snippetContent, snippet)
        }

        private fun saveSnippet(
            input: CreateSnippet,
            user: UserSnippet,
            status: SnippetStatus,
            token: String,
        ): Snippet {
            logger.info("Saving snippet in database")
            val snippet = snippetRepository.save(getSnippet(input, user, status))

            logger.info("Saving snippet content in bucket")
            bucketAPI.createSnippet(snippet.id.toString(), input.content)

            addOwnerPermission(user.userId, snippet.id!!, token)

            publishLintEvent(user.userId, token, input.content, snippet)
            return snippet
        }

        private fun publishLintEvent(
            userId: String,
            token: String,
            content: String,
            snippet: Snippet,
        ) {
            val rules: RulesOutput = this.snippetConf.getRules(userId, token, "LINTING")

            val version = snippetConf.getVersion(token, snippet.language.toString())

            GlobalScope.launch {
                lintProducer.publishEvent(content, snippet, rules, version)
            }
        }

        private fun getSnippet(
            input: CreateSnippet,
            user: UserSnippet,
            status: SnippetStatus,
        ): Snippet =
            Snippet(
                input.name,
                input.language,
                input.extension,
                user,
                status,
            )

        private fun addOwnerPermission(
            userId: String,
            snippetId: Long,
            token: String,
        ) {
            logger.info("Calling permission to save user $userId as owner of the snippet $snippetId")
            val addPermDto =
                AddPermDto(
                    permissionType = PermissionType.OWNER,
                    snippetId = snippetId,
                    userId = userId,
                    sharerId = "",
                )
            this.snippetPerm.addPermission(addPermDto, token)
        }

        private fun updateContentInBucket(
            snippetId: String,
            newContent: String,
        ) {
            logger.info("Updating snippet content from bucket, snippetId = $snippetId")
            bucketAPI.deleteSnippet(snippetId)
            bucketAPI.createSnippet(snippetId, newContent)
        }

        private fun getSnippetByID(snippetId: String): Optional<Snippet> {
            val snippet = this.snippetRepository.findById(snippetId.toLong())
            if (snippet.isEmpty) throw NotFoundException("Snippet was not found")
            return snippet
        }

        private fun getSnippetsShared(
            userId: String,
            token: String,
        ): SnippetListDto {
            logger.info("Getting snippets that the user id $userId is a reader")
            val snippetIds = this.snippetPerm.getSharedSnippets(userId, token)
            return SnippetListDto(
                snippetIds.map { id: Long ->
                    val snippet = this.snippetRepository.findById(id)
                    val content = bucketAPI.getSnippet(id.toString())
                    snippetDto(id, snippet, content)
                },
            )
        }

        private fun snippetDto(
            id: Long,
            snippet: Optional<Snippet>,
            content: String,
        ) = SnippetDto(
            id = id,
            name = snippet.get().name,
            content = content,
            compliance = snippet.get().status.status,
            author = snippet.get().userSnippet.name,
            language = snippet.get().language,
            extension = snippet.get().extension,
        )
    }
