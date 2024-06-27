package manager.common.rest.exception

import org.springframework.http.HttpStatus

class BadReqException(message: String): RuntimeException(message)