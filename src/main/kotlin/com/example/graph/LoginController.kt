package com.example.graph

import com.example.graph.generated.types.AccessToken
import com.example.graph.security.JwtTokenGenerator
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.stereotype.Controller

/**
 * @author Julius Krah
 */
@Controller
@PreAuthorize("permitAll()")
class LoginController(
    private val authenticationManager: ReactiveAuthenticationManager,
    private val jwtTokenGenerator: JwtTokenGenerator
) {
    private val log: System.Logger = System.getLogger(javaClass.canonicalName)

    @MutationMapping
    suspend fun login(@Argument username: String, @Argument password: String): AccessToken {
        log.log(System.Logger.Level.INFO, "User: {0} requested login", username)
        val authenticationRequest = UsernamePasswordAuthenticationToken.unauthenticated(username, password)
        return authenticationManager.authenticate(authenticationRequest).map {authentication ->
            val roles = authentication.authorities.map(GrantedAuthority::getAuthority)
            jwtTokenGenerator.generateToken(username, roles)
        }.awaitSingle()
    }
}