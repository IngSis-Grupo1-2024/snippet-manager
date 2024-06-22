package manager.rest

import org.springframework.http.HttpHeaders

class BasicRest {
    companion object {
        fun getAuthHeaders(token: String): HttpHeaders {
            val headers = HttpHeaders();
            headers.setBearerAuth(token)
            return headers
        }
    }
}