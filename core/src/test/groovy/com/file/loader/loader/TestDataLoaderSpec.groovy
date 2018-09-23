package com.file.loader.loader

import com.sapsystem.loader.TestDataLoader
import com.sapsystem.service.asset.AssetService
import spock.lang.Specification

class TestDataLoaderSpec extends Specification {

    def assetService = Mock(AssetService)
    def loader = Spy(TestDataLoader, constructorArgs: [assetService]) {
        loadAssets(_) >> null
    }

    def "should load test data"() {
        when:
        loader.load()

        then:
        1 * loader.loadAssets(_) >> null
    }

    def "should call clear and load"() {
        setup:
        System.setProperty("loadData", "true")

        when:
        loader.init()

        then:
        1 * assetService.deleteAll()
        1 * loader.load()
    }
}
