package com.file.loader.service

import com.sapsystem.api.exception.ObjectNotFoundException
import com.sapsystem.domain.AssetEntity
import com.sapsystem.repository.AssetRepository
import com.sapsystem.repository.filter.asset.AssetSpecificationsBuilder
import com.sapsystem.service.asset.AssetServiceImpl
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import spock.lang.Specification

import java.time.Instant

class PictureServiceSpec extends Specification {

    def repository = Mock(AssetRepository)
    def service = new AssetServiceImpl(repository)

    def "should get data by id"() {
        setup:
        def id = 1L
        def asset =  new AssetEntity(id: 1L, tagName: "22Y2071", serialNumber: 34817954, type: "Samson", manufacturer: "Samson",
                productNumber: "123-54-33320000", lastMaintenanceDate: Instant.ofEpochMilli(1535443954232), longTag: "V022 AB22 Y5090")

        when:
        def result = service.getById(id)

        then:
        1 * repository.findById(id) >> Optional.of(asset)
        result.isPresent()
        result.get() == asset
    }

    def "should call get all assets"() {
        setup:
        def filter = ""
        def builder = new AssetSpecificationsBuilder()
        def spec = builder.build()
        def pageable = new PageRequest(0, 10)
        def asset =  new AssetEntity(id: 1L, tagName: "22Y2071", serialNumber: 34817954, type: "Samson", manufacturer: "Samson",
                productNumber: "123-54-33320000", lastMaintenanceDate: Instant.ofEpochMilli(1535443954232), longTag: "V022 AB22 Y5090")
        def page = new PageImpl([asset], pageable, 1)

        when:
        def returnedPage = service.getAll(filter, pageable)

        then:
        1 * repository.findAll(spec, pageable) >> page
        returnedPage.getContent() == page.getContent()
    }

    def "should add asset"() {
        setup:
        def id = 1L
        def asset =  new AssetEntity(id: 1L, tagName: "22Y2071", serialNumber: 34817954, type: "Samson", manufacturer: "Samson",
                productNumber: "123-54-33320000", lastMaintenanceDate: Instant.ofEpochMilli(1535443954232), longTag: "V022 AB22 Y5090")

        when:
        def createdID = service.add(asset)

        then:
        1 * repository.save(asset) >> asset
        createdID == id
    }

    def "should update asset"() {
        setup:
        def id = 1L
        def asset =  new AssetEntity(id: 1L, tagName: "22Y2071", serialNumber: 34817954, type: "Samson", manufacturer: "Samson",
                productNumber: "123-54-33320000", lastMaintenanceDate: Instant.ofEpochMilli(1535443954232), longTag: "V022 AB22 Y5090")

        when:
        def createdID = service.update(id, asset)

        then:
        1 * repository.existsById(id) >> true
        1 * repository.save(asset) >> asset
        createdID == id
    }

    def "should throw exception when data does not exist(update)"() {
        setup:
        def id = 1L
        def asset =  new AssetEntity(id: 1L, tagName: "22Y2071", serialNumber: 34817954, type: "Samson", manufacturer: "Samson",
                productNumber: "123-54-33320000", lastMaintenanceDate: Instant.ofEpochMilli(1535443954232), longTag: "V022 AB22 Y5090")

        when:
        def createdID = service.update(id, asset)

        then:
        1 * repository.existsById(id) >> false
        thrown(ObjectNotFoundException)
    }

    def "should delete asset"() {
        setup:
        def id = 1L

        when:
        service.delete(id)

        then:
        1 * repository.existsById(id) >> true
        1 * repository.deleteById(id)
    }

    def "should throw exception when data does not exist(deletion)"() {
        setup:
        def id = 1L

        when:
        service.delete(id)

        then:
        1 * repository.existsById(id) >> false
        thrown(ObjectNotFoundException)
    }

    def "should delete all assets"() {
        when:
        service.deleteAll()

        then:
        1 * repository.deleteAll()
    }
}
