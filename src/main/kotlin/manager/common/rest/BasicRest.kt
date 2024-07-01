package manager.common.rest

import modules.common.logs.CorrelationIdFilter.Companion.CORRELATION_ID_KEY
import org.slf4j.MDC
import org.springframework.http.HttpHeaders

class BasicRest {
    companion object {
        fun getAuthHeaders(token: String): HttpHeaders {
            val headers = HttpHeaders()
            val correlationId = MDC.get(CORRELATION_ID_KEY)
            headers.setBearerAuth(token)
            headers.set("X-Correlation-ID", correlationId)
            return headers
        }

        fun getUserId(token: String): String {
            return token
        }
    }
}
