package manager.common.rest.exception

import manager.common.rest.dto.Output
import org.springframework.http.HttpStatus

class ErrorOutputWStatus(
    val message: String,
    val status: HttpStatus,
) : Output
