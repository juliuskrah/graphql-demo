package com.example.graph.security

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.access.hierarchicalroles.RoleHierarchy
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl
import org.springframework.security.authentication.DelegatingReactiveAuthenticationManager
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.core.GrantedAuthorityDefaults
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.config.web.server.invoke
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder
import org.springframework.security.web.server.SecurityWebFilterChain
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec


/**
 * @author Julius Krah
 */
@Configuration(proxyBeanMethods = false)
@EnableReactiveMethodSecurity(useAuthorizationManager = true)
class SecurityConfiguration {
    @Value("\${demo.shared.secret}")
    lateinit var sharedSecret: String

    @Bean
    fun securityChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http {
            authorizeExchange {
                authorize(anyExchange, permitAll)
            }
            csrf {
                disable()
            }
            oauth2ResourceServer {
                jwt { }
            }
        }
    }

    @Bean
    fun jwtDecoder(): ReactiveJwtDecoder {
        val key: SecretKey = SecretKeySpec(sharedSecret.toByteArray(), "HS256")
        return NimbusReactiveJwtDecoder.withSecretKey(key)
            .build()
    }

    @Bean
    fun userDetailsService(): MapReactiveUserDetailsService {
        val user: UserDetails = User.withDefaultPasswordEncoder()
            .username("user")
            .password("password")
            .roles("product:read")
            .build()
        val admin: UserDetails = User.withDefaultPasswordEncoder()
            .username("admin")
            .password("password")
            .roles("product:write")
            .build()
        return MapReactiveUserDetailsService(user, admin)
    }

    @Bean
    fun authenticationManager(userDetailsService: ReactiveUserDetailsService): ReactiveAuthenticationManager {
        val userDetailsAuthenticationManager = UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService)
        return DelegatingReactiveAuthenticationManager(userDetailsAuthenticationManager)
    }

    companion object {
        @Bean
        fun grantedAuthorityDefaults(): GrantedAuthorityDefaults {
            return GrantedAuthorityDefaults("")
        }

        @Bean
        fun roleHierarchy(): RoleHierarchy {
            val hierarchy = RoleHierarchyImpl()
            hierarchy.setHierarchy("product:write > product:read")
            return hierarchy
        }
    }
}