package com.file.loader.converter

import com.sapsystem.api.rest.v1.dto.resource.AssetDTO
import com.sapsystem.converter.AssetConverter
import com.sapsystem.domain.AssetEntity
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import org.springframework.web.util.WebUtils
import spock.lang.Specification

import javax.servlet.http.HttpServletRequest
import java.time.Instant

class AssetConverterSpec extends Specification {

    def request = Mock(HttpServletRequest) {
        getRequestURL() >> new StringBuffer("http://localhost:8080")
        getHeaderNames() >> Collections.emptyEnumeration()
        getAttribute(WebUtils.INCLUDE_CONTEXT_PATH_ATTRIBUTE) >> "http://localhost:8080"
        getAttribute(WebUtils.INCLUDE_REQUEST_URI_ATTRIBUTE) >> "http://localhost:8080"
        getAttribute(WebUtils.INCLUDE_SERVLET_PATH_ATTRIBUTE) >> "http://localhost:8080"
    }
    def converter = new AssetConverter()

    def "should convert entity"() {
        setup:
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request))
        def entity = new AssetEntity(id: 1L, tagName: "22Y2071", serialNumber: 34817954, type: "Samson", manufacturer: "Samson",
                productNumber: "123-54-33320000", lastMaintenanceDate: Instant.ofEpochMilli(1535443954232), longTag: "V022 AB22 Y5090")

        when:
        def dto = converter.convertToDto(entity)

        then:
        dto.getAssetId() == 1
        dto.getTagName() == "22Y2071"
        dto.getSerialNumber() == 34817954
        dto.getType() == "Samson"
        dto.getManufacturer() == "Samson"
        dto.getProductNumber() == "123-54-33320000"
        dto.getLastMaintenanceDate() == 1535443954232L
        dto.getLongTag() == "V022 AB22 Y5090"
    }

    def "should convert dto"() {
        setup:
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request))
        def dto = new AssetDTO(1L, "22Y2071", 34817954, "Samson", "Samson",
                "123-54-33320000", 1535443954232L, "V022 AB22 Y5090")

        when:
        def entity = converter.convertFromDto(dto)

        then:
        entity.getTagName() == "22Y2071"
        entity.getSerialNumber() == 34817954
        entity.getType() == "Samson"
        entity.getManufacturer() == "Samson"
        entity.getProductNumber() == "123-54-33320000"
        entity.getLastMaintenanceDate() == Instant.ofEpochMilli(1535443954232)
        entity.getLongTag() == "V022 AB22 Y5090"
    }
}
