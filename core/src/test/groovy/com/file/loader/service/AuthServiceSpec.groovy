package com.file.loader.service

import com.sapsystem.api.rest.v1.dto.auth.AuthenticationRequest
import com.sapsystem.security.generator.AuthTokenGenerator
import com.sapsystem.security.pojo.JwtConfig
import com.sapsystem.service.auth.AuthServiceImpl
import org.springframework.security.authentication.BadCredentialsException
import spock.lang.Specification

class AuthServiceSpec extends Specification {

    def jwtConfig = Mock(JwtConfig)
    def tokenGenerator = Mock(AuthTokenGenerator)
    def service = new AuthServiceImpl(jwtConfig, tokenGenerator)

    def "should authenticate user"() {
        setup:
        def authentication = new AuthenticationRequest("test")

        when:
        def response = service.authenticate(authentication)

        then:
        1 * jwtConfig.getAdminPassword() >> "test"
        1 * tokenGenerator.generate() >> "token"
        1 * jwtConfig.getExpiration() >> 1L
        response.getToken() == "token"
        response.getExpiration() == 1L
    }

    def "should throw exception during authentication user"() {
        setup:
        def authentication = new AuthenticationRequest("test")

        when:
        service.authenticate(authentication)

        then:
        1 * jwtConfig.getAdminPassword() >> "test1"
        thrown(BadCredentialsException)
    }
}
