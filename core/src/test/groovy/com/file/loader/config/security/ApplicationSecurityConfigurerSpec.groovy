package com.file.loader.config.security

import com.sapsystem.config.security.ApplicationSecurityConfigurer
import com.sapsystem.security.JwtAuthenticationEntryPoint
import com.sapsystem.security.filter.CorsFilter
import com.sapsystem.security.pojo.JwtConfig
import org.springframework.context.ApplicationContext
import org.springframework.core.env.Environment
import org.springframework.security.config.annotation.ObjectPostProcessor
import org.springframework.security.config.annotation.web.builders.WebSecurity
import spock.lang.Specification

class ApplicationSecurityConfigurerSpec extends Specification {

    def jwtConfig = Mock(JwtConfig)
    def corsFilter = Mock(CorsFilter)
    def jwtEntryPoint = Mock(JwtAuthenticationEntryPoint)
    def environment = Mock(Environment)
    def applicationContext = Mock(ApplicationContext) {
        getEnvironment() >> environment
        getBean(ObjectPostProcessor) >> Mock(ObjectPostProcessor)
    }
    def configurer = new ApplicationSecurityConfigurer(jwtConfig, corsFilter, jwtEntryPoint)

    def setup() {
        configurer.setApplicationContext(applicationContext)
    }

    def "should ignore swagger resources"() {
        setup:
        def postProcessor = Mock(ObjectPostProcessor)
        def webSecurity = GroovyMock(WebSecurity, constructorArgs: [postProcessor])

        when:
        configurer.configure(webSecurity)

        then:
        1 * environment.getActiveProfiles() >> ["dev1"]
        0 * webSecurity._
    }
}
