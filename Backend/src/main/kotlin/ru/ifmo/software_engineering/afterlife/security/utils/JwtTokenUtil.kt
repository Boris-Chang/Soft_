package ru.ifmo.software_engineering.afterlife.security.utils

import io.jsonwebtoken.*
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import ru.ifmo.software_engineering.afterlife.security.Identity
import ru.ifmo.software_engineering.afterlife.security.IdentityImpl
import ru.ifmo.software_engineering.afterlife.utils.toDateUtc
import java.time.LocalDateTime
import java.util.*

interface JwtTokenUtil {
    fun getIdentity(token: String?): Identity?
    fun generateAccessToken(identity: Identity): String
}

@Component
class JwtTokenUtilImpl : JwtTokenUtil {
    @Value("\${auth.jwt-secret}")
    private lateinit var jwtSecret: String

    private val jwtIssuer = "afterlife-backend"
    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun generateAccessToken(identity: Identity): String {
        return Jwts.builder()
                .setSubject(identity.username)
                .setId(identity.id)
                .setIssuer(jwtIssuer)
                .setIssuedAt(LocalDateTime.now().toDateUtc())
                .setExpiration(LocalDateTime.now().plusWeeks(1).toDateUtc()) // 1 week
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact()
    }

    override fun getIdentity(token: String?): Identity? {
        val jwt = parseToken(token) ?: return null
        val claims = jwt.body

        val expirationDate = this.getExpirationDate(claims)
        if (expirationDate.before(LocalDateTime.now().toDateUtc())) {
            logger.error("JWT expired")
            return null
        }

        return IdentityImpl(id = claims.id, username = getUsername(claims))
    }

    private fun getUsername(claims: Claims): String =
        claims.subject

    private fun getExpirationDate(claims: Claims): Date =
        claims.expiration

    fun parseToken(token: String?): Jws<Claims>? {
        try {
            return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token)
        } catch (ex: SignatureException) {
            logger.error("Invalid JWT signature - {}", ex.message)
            return null
        } catch (ex: MalformedJwtException) {
            logger.error("Invalid JWT token - {}", ex.message)
            return null
        } catch (ex: ExpiredJwtException) {
            logger.error("Expired JWT token - {}", ex.message)
            return null
        } catch (ex: UnsupportedJwtException) {
            logger.error("Unsupported JWT token - {}", ex.message)
            return null
        } catch (ex: IllegalArgumentException) {
            logger.error("JWT claims string is empty - {}", ex.message)
            return null
        }
    }
}