package manager.logs

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.slf4j.Logger
import org.slf4j.MDC
import org.springframework.http.HttpStatus
import org.springframework.mock.http.server.reactive.MockServerHttpRequest
import org.springframework.mock.web.server.MockServerWebExchange
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import kotlin.reflect.full.memberProperties

@ExtendWith(MockitoExtension::class)
class CorrelationIdFilterTest {

    @Mock
    private lateinit var request: HttpServletRequest

    @Mock
    private lateinit var response: HttpServletResponse

    @Mock
    private lateinit var filterChain: FilterChain

    @InjectMocks
    private lateinit var correlationIdFilter: CorrelationIdFilter

    @Captor
    private lateinit var stringCaptor: ArgumentCaptor<String>

    private val logger: Logger = mock()
    private val webFilterChain: WebFilterChain = mock {
        on { filter(any()) } doReturn Mono.empty()
    }

    @BeforeEach
    fun setUp() {
        MDC.clear()
    }

    @Test
    fun `should remove correlation id from MDC after request`() {
        val correlationId = "test-correlation-id"
        `when`(request.getHeader(CorrelationIdFilter.CORRELATION_ID_HEADER)).thenReturn(correlationId)

        correlationIdFilter.doFilterInternal(request, response, filterChain)

        assertNull(MDC.get(CorrelationIdFilter.CORRELATION_ID_KEY))
    }

}
