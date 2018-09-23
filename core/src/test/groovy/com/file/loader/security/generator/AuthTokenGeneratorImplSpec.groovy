package com.file.loader.security.generator

import com.sapsystem.security.generator.AuthTokenGeneratorImpl
import com.sapsystem.security.pojo.JwtConfig
import spock.lang.Specification

import java.time.Instant
import java.time.temporal.ChronoUnit

class AuthTokenGeneratorImplSpec extends Specification {

    def jwtConfig = Mock(JwtConfig) {
        getSecret() >> "test"
    }
    def tokenGenerator = new AuthTokenGeneratorImpl(jwtConfig)

    def "should generate jwt token"() {
        when:
        def token = tokenGenerator.generate()

        then:
        token != null
    }

    def "should add seconds to expire date"() {
        setup:
        def timestamp = 512595922922

        when:
        def expireDate = tokenGenerator.getTokenExpirationDate(new Date(timestamp))

        then:
        jwtConfig.getExpiration() >> 100
        100L == ChronoUnit.SECONDS.between(Instant.ofEpochMilli(timestamp), expireDate.toInstant())
    }
}
