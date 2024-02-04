package com.example.graph.security

import com.example.graph.generated.types.AccessToken
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.JWSSigner
import com.nimbusds.jose.crypto.MACSigner
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.Instant
import java.util.Date


/**
 * @author Julius Krah
 */
@Component
class JwtTokenGenerator {
    @Value("\${demo.shared.secret}")
    lateinit var sharedSecret: String
    @Value("\${demo.issuer.url}")
    lateinit var issuerUrl: String
    val expiration = 3600L

    fun generateToken(username: String, roles: List<String>): AccessToken {
        val signer: JWSSigner = MACSigner(sharedSecret)
        val iat = Instant.now()
        val expirationTime = Date.from(
            iat.plus(Duration.ofSeconds(expiration))
        )

        // Prepare JWT with claims set
        val claimsSet = JWTClaimsSet.Builder()
            .subject(username)
            .issuer(issuerUrl)
            .audience("demo")
            .expirationTime(expirationTime)
            .issueTime(Date.from(iat))
            .notBeforeTime(Date.from(iat))
            .claim("scope", roles.joinToString(" "))
            .build()

        val signedJWT = SignedJWT(JWSHeader(JWSAlgorithm.HS256), claimsSet)

        // Apply the HMAC protection
        signedJWT.sign(signer)

        return AccessToken(
            signedJWT.serialize(),
            "Bearer",
            expiration.toInt(),
            "",
            roles.joinToString(" ")
        )
    }

}