package manager.manager.service

import com.example.redisevents.LintRequest
import com.example.redisevents.LintRulesInput
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import manager.bucket.BucketAPI
import manager.common.rest.exception.BadReqException
import manager.common.rest.exception.NotFoundException
import manager.manager.integration.permission.SnippetPerm
import manager.manager.model.entity.SnippetStatus
import manager.manager.model.dto.*
import manager.manager.model.entity.Snippet
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
        private val snippetStatusRepository: SnippetStatusRepository,
        private val lintProducer: LintProducer,
        private val config: SnippetConf,
    ) : ManagerServiceSpec {
        @Transactional
        override fun createSnippet(
            input: CreateSnippet,
            userId: String,
            token: String,
        ): SnippetDto {
            val user = userRepository.findByUserId(userId) ?: throw NotFoundException("User name was not found")
            val snippet =
                snippetRepository.save(
                    Snippet(
                        input.name,
                        input.language,
                        input.extension,
                        user,
                        ComplianceSnippet.PENDING,
                    ),
                )
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
                compliance = snippet.get().status,
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
            input: String,
            userId: String,
            token: String,
        ): SnippetDto {
//        if (userIsNotTheOwner(snippetId, userId, token)) {
//            throw BadReqException("The user has no permissions for updating the snippet")
//        }
            val snippet = this.snippetRepository.findById(snippetId.toLong())
            if (snippet.isEmpty) throw NotFoundException("Snippet was not found")
            bucketAPI.deleteSnippet(snippetId)
            bucketAPI.createSnippet(snippetId, input)
            publishEventBySnippet(snippetId, userId, token)
            return SnippetDto(
                id = snippet.get().id!!,
                name = snippet.get().name,
                content = input,
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

        override fun updateSnippetStatus(
            userId: String,
            snippetId: String,
            complianceSnippet: ComplianceSnippet,
        ): ComplianceSnippet {
            val snippet = this.snippetRepository.findById(snippetId.toLong())
            val user = this.userRepository.findByUserId(userId)
            if (snippet.isEmpty) throw NotFoundException("Snippet was not found")
            if (user == null) throw NotFoundException("User was not found")
            val snippetCompliance: SnippetStatus =
                this.snippetStatusRepository.save(SnippetStatus(complianceSnippet, snippet.get()))
            return snippetCompliance.status
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
            val rules: List<LintRulesInput> = rulesParser(this.config.getRules(userId, token, "LINTING"))
            val snippet: Snippet = this.snippetRepository.findById(snippetId.toLong()).get()
            val snippetContent: String = bucketAPI.getSnippet(snippetId)
            val event = LintRequest(snippetContent, snippet.language.toString(), "v1", rules, listOf("Hello"), snippetId, userId)
            GlobalScope.launch {
                lintProducer.publishEvent(event)
            }
        }

        private fun rulesParser(rulesOutput: RulesOutput): List<LintRulesInput> {
            return rulesOutput.rules.map { rule ->
                LintRulesInput(
                    rule.parent,
                    rule.isActive,
                    rule.name == "expression",
                    rule.name == "identifier",
                    rule.name == "literal",
                    if (rule.name == "snake_case" || rule.name == "") "snake case" else "camel case",
                )
            }
        }
    }
