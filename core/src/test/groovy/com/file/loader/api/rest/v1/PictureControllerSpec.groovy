package com.file.loader.api.rest.v1

import com.file.loader.service.picture.PictureService
import org.bson.types.ObjectId
import org.springframework.core.io.UrlResource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.mock.web.MockMultipartFile
import spock.lang.Specification

class PictureControllerSpec extends Specification {

    def service = Mock(PictureService.class)
    def controller = new PictureController(service)

    def "should return status accepted"() {
        setup:
        def file = new MockMultipartFile("test", "test.png", "image/png", new byte[5])
        def callback = "http://localhost:8080/call/back"

        when:
        def result = controller.create(callback, file)

        then:
        1 * service.store(file, callback)
        result == new ResponseEntity(HttpStatus.ACCEPTED)
    }

    def "should return status bad request"() {
        setup:
        def file = new MockMultipartFile("test", "test.pdf", "file/pdf", new byte[5])
        def callback = "http://localhost:8080/call/back"

        when:
        def result = controller.create(callback, file)

        then:
        result == new ResponseEntity(HttpStatus.BAD_REQUEST)
    }

    def "should return status 200 on delete "() {
        setup:
        def id = new ObjectId()

        when:
        def result = controller.delete(id)

        then:
        1 * service.delete(id)
        result == new ResponseEntity(HttpStatus.OK)
    }

    def "should return resource"() {
        setup:
        def id = new ObjectId()
        def size = "small"
        def resource = new UrlResource("http://localhost:8080/test/url")

        when:
        def result = controller.get(id, size)

        then:
        service.loadAsResource(id, size) >> resource
        result == ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"").body(resource)
    }
}
