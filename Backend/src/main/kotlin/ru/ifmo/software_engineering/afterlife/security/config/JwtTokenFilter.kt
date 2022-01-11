package ru.ifmo.software_engineering.afterlife.security.config

import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import ru.ifmo.software_engineering.afterlife.security.utils.JwtTokenUtil
import java.io.IOException
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import kotlin.collections.toTypedArray


@Component
class JwtTokenFilter(
        private val jwtTokenUtil: JwtTokenUtil,
        private val userDetailsService: UserDetailsService
) : OncePerRequestFilter() {

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(request: HttpServletRequest,
                                  response: HttpServletResponse,
                                  chain: FilterChain) {
        // Get authorization header and validate
        val header = request.getHeader(HttpHeaders.AUTHORIZATION)
        if (header == null || header.isEmpty() || !header.startsWith("Bearer ")) {
            chain.doFilter(request, response)
            return
        }

        // Get jwt token and validate
        val token = header.split(" ").toTypedArray()[1].trim()
        val currentUser = jwtTokenUtil.getIdentity(token)
        if (currentUser == null) {
            chain.doFilter(request, response)
            return
        }

        // Get user identity and set it on the spring security context
        val userDetails: UserDetails? = userDetailsService.loadUserByUsername(currentUser.username)
        if (userDetails == null) {
            chain.doFilter(request, response)
            return
        }

        val authentication = UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.authorities)
        authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
        SecurityContextHolder.getContext().authentication = authentication

        chain.doFilter(request, response)
    }
}