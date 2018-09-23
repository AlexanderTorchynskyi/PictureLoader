package com.file.loader.api.rest.v1

import com.sapsystem.api.rest.aggregator.AssetAggregator
import com.sapsystem.api.rest.v1.dto.resource.AssetDTO
import com.sapsystem.api.rest.v1.dto.resource.DataPagedResources
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.web.HateoasPageableHandlerMethodArgumentResolver
import org.springframework.data.web.PagedResourcesAssembler
import org.springframework.hateoas.PagedResources
import org.springframework.http.ResponseEntity
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder
import org.springframework.web.util.UriComponentsBuilder
import org.springframework.web.util.WebUtils
import spock.lang.Specification

import javax.servlet.http.HttpServletRequest

class PictureControllerSpec extends Specification {

    def request = Mock(HttpServletRequest) {
        getRequestURL() >> new StringBuffer("http://localhost:8080")
        getHeaderNames() >> Collections.emptyEnumeration()
        getAttribute(WebUtils.INCLUDE_CONTEXT_PATH_ATTRIBUTE) >> "http://localhost:8080"
        getAttribute(WebUtils.INCLUDE_REQUEST_URI_ATTRIBUTE) >> "http://localhost:8080"
        getAttribute(WebUtils.INCLUDE_SERVLET_PATH_ATTRIBUTE) >> "http://localhost:8080"
    }
    def argumentResolver = Mock(HateoasPageableHandlerMethodArgumentResolver)
    def urlComponent = new UriComponentsBuilder().build()
    def assembler = Spy(PagedResourcesAssembler, constructorArgs: [argumentResolver, urlComponent])
    def aggregator = Mock(AssetAggregator.class)
    def controller = new PictureController(aggregator)

    def setup() {
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request))
    }

    def "should get all the assets(first page)"() {
        setup:
        def filter = ""
        def pageable = new PageRequest(0, 20)
        def assetDTO = new AssetDTO(1L, "22Y2071", 34817954, "Samson", "Samson",
                "123-54-33320000", 1535443954232L, "V022 AB22 Y5090")

        when:
        def data = controller.getAllAssets(filter, pageable, assembler)

        then:
        1 * aggregator.getAllAssets(filter, pageable) >> new PageImpl<>([assetDTO], pageable, 1)
        1 * assembler.toResource(new PageImpl<>([assetDTO], pageable, 1)) >> new PagedResources<>([assetDTO], new PagedResources.PageMetadata(1, 0, 1))
        data == new DataPagedResources([assetDTO], new PagedResources.PageMetadata(1, 0, 1), [])
    }

    def "should get the asset by id"() {
        setup:
        def id = 1L
        def assetDTO = new AssetDTO(1L, "22Y2071", 34817954, "Samson", "Samson",
                "123-54-33320000", 1535443954232L, "V022 AB22 Y5090")

        when:
        def data = controller.getAsset(id)

        then:
        1 * aggregator.getAsset(id) >> assetDTO
        data == assetDTO
    }

    def "should add the asset"() {
        def id = 1L
        def assetDTO = new AssetDTO(1L, "22Y2071", 34817954, "Samson", "Samson",
                "123-54-33320000", 1535443954232L, "V022 AB22 Y5090")
        def expectedResult = ResponseEntity.created(
                MvcUriComponentsBuilder.fromController(PictureController.class)
                        .path("/{id}")
                        .buildAndExpand(id)
                        .toUri()
        ).build()

        when:
        def result = controller.addAsset(assetDTO)

        then:
        1 * aggregator.addAsset(assetDTO) >> id
        expectedResult == result
    }

    def "should update the asset"() {
        def id = 1L
        def assetDTO = new AssetDTO(1L, "22Y2071", 34817954, "Samson", "Samson",
                "123-54-33320000", 1535443954232L, "V022 AB22 Y5090")
        def expectedResult = ResponseEntity.created(
                MvcUriComponentsBuilder.fromController(PictureController.class)
                        .path("/{id}")
                        .buildAndExpand(id)
                        .toUri()
        ).build()

        when:
        def result = controller.updateAsset(id, assetDTO)

        then:
        1 * aggregator.updateAsset(id, assetDTO) >> id
        expectedResult == result
    }

    def "should delete the asset"() {
        setup:
        def id = 1L

        when:
        def result = controller.deleteAsset(id)

        then:
        1 * aggregator.deleteAsset(id)
        result == ResponseEntity.noContent().build()
    }
}
