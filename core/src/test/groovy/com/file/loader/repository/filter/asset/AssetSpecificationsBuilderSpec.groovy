package com.file.loader.repository.filter.asset

import com.sapsystem.repository.filter.asset.AssetSpecificationsBuilder
import spock.lang.Specification

class AssetSpecificationsBuilderSpec extends Specification {

    def params = Mock(List)
    def builder = new AssetSpecificationsBuilder(params)

    def "should add to list"() {
        setup:
        def key = "field1"
        def operation = "="
        def value = "test"

        when:
        builder.with(key, operation, value)

        then:
        1 * params.add({ it.key == key && it.operation == operation && it.value == value })
    }

//    def "should return specification after build"() {
//        setup:
//
//        when:
//        def result = builder.build()
//
//        then:
//        1 * params.size() >> 1
//    }

    def "should return null after build"() {
        setup:

        when:
        def result = builder.build()

        then:
        1 * params.size() >> 0
        result == null
    }
}
