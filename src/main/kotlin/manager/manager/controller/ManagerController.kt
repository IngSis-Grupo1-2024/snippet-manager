package manager.manager.controller

import com.example.snippetmanager.snippet.UpdateSnippet
import manager.common.rest.BasicRest.Companion.getUserId
import manager.common.rest.dto.Output
import manager.common.rest.exception.ErrorOutput
import manager.common.rest.exception.NotFoundException
import manager.manager.model.dto.*
import manager.manager.model.input.CreateSnippet
import manager.manager.model.input.ShareSnippetInput
import manager.manager.service.ManagerServiceSpec
import org.apache.coyote.BadRequestException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.HttpClientErrorException

@RestController
class ManagerController
    @Autowired
    constructor(
        private val service: ManagerServiceSpec,
    ) : ManagerControllerSpec {
        private val logger = LoggerFactory.getLogger(ManagerController::class.java)

        override fun saveName(
            jwt: Jwt,
            name: String,
        ): ResponseEntity<String> {
            logger.info("Saving name")
            val responseBody = this.service.saveName(name.substring(0, name.length - 1), getUserId(jwt.subject))
            return ResponseEntity.status(HttpStatus.CREATED).body(responseBody)
        }

        override fun createSnippet(
            jwt: Jwt,
            snippetContent: CreateSnippet,
        ): ResponseEntity<Output> {
            try {
                logger.info("Creating snippet")
                val responseBody = service.createSnippet(snippetContent, getUserId(jwt.subject), jwt.tokenValue)
                return ResponseEntity.status(HttpStatus.CREATED).body(responseBody)
            } catch (e: HttpClientErrorException) {
                return getErrorOutputResponse(e.statusCode, e.message!!)
            } catch (e: NotFoundException) {
                return getErrorOutputResponse(HttpStatus.NOT_FOUND, e.message!!)
            }
        }

        override fun getSnippet(snippetId: String): ResponseEntity<Output> {
            try {
                logger.info("Getting snippet")
                val responseBody = service.getSnippet(snippetId)
                return ResponseEntity.ok(responseBody)
            } catch (e: HttpClientErrorException) {
                return getErrorOutputResponse(e.statusCode, e.message!!)
            }
        }

        override fun deleteSnippet(
            jwt: Jwt,
            snippetId: String,
        ): ResponseEntity<String> {
            try {
                logger.info("Deleting snippet")
                service.deleteSnippet(getUserId(jwt.subject), jwt.tokenValue, snippetId)
                return ResponseEntity.ok("Snippet $snippetId deleted")
            } catch (e: HttpClientErrorException) {
                return getErrorStrResponse(e.statusCode, e.message!!)
            }
        }

        override fun updateSnippet(
            snippetId: String,
            snippetContent: UpdateSnippet,
            jwt: Jwt,
        ): ResponseEntity<Output> {
            try {
                logger.info("Updating snippet")
                val responseBody: SnippetDto = service.updateSnippet(snippetId, snippetContent, getUserId(jwt.subject), jwt.tokenValue)
                return ResponseEntity.ok(responseBody)
            } catch (e: HttpClientErrorException) {
                return getErrorOutputResponse(e.statusCode, e.message!!)
            } catch (e: NotFoundException) {
                return getErrorOutputResponse(HttpStatus.NOT_FOUND, e.message!!)
            } catch (e: BadRequestException) {
                return getErrorOutputResponse(HttpStatus.BAD_REQUEST, e.message!!)
            }
        }

        override fun getFileTypes(): ResponseEntity<List<FileTypeDto>> {
            logger.info("Getting file types")
            return ResponseEntity.status(HttpStatus.OK).body(this.service.getFileTypes())
        }

        override fun getSnippetDescriptors(jwt: Jwt): ResponseEntity<Output> {
            try {
                logger.info("Getting snippet from user ${jwt.subject}")
                val responseBody: SnippetListDto = service.getSnippetDescriptors(getUserId(jwt.subject), jwt.tokenValue)
                return ResponseEntity.ok(responseBody)
            } catch (e: HttpClientErrorException) {
                return getErrorOutputResponse(e.statusCode, e.message!!)
            } catch (e: NotFoundException) {
                return getErrorOutputResponse(HttpStatus.NOT_FOUND, e.message!!)
            }
        }

        override fun getUserFriends(jwt: Jwt): ResponseEntity<Output> {
            logger.info("Getting snippet searcher users")
            val responseBody: UsersDto = service.getUserFriends(getUserId(jwt.subject))
            return ResponseEntity.ok(responseBody)
        }

        override fun shareSnippet(
            jwt: Jwt,
            shareSnippet: ShareSnippetInput,
        ): ResponseEntity<Output> {
            try {
                logger.info("Sharing snippet from user ${jwt.subject} to ${shareSnippet.userId} for the snippet ${shareSnippet.snippetId}")
                val responseBody = service.shareSnippet(getUserId(jwt.subject), shareSnippet, jwt.tokenValue)
                return ResponseEntity.ok(responseBody)
            } catch (e: BadRequestException) {
                return getErrorOutputResponse(HttpStatus.BAD_REQUEST, e.message!!)
            }
        }

        private fun getErrorStrResponse(
            statusCode: HttpStatusCode,
            message: String,
        ): ResponseEntity<String> {
            logger.warn(message)
            return ResponseEntity.status(statusCode).body(message)
        }

        private fun getErrorOutputResponse(
            status: HttpStatusCode,
            message: String,
        ): ResponseEntity<Output> {
            logger.warn(message)
            return ResponseEntity.status(status).body(ErrorOutput(message))
        }
    }
