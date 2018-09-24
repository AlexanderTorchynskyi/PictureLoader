package com.file.loader.service.picture

import com.file.loader.config.StorageConfiguration
import com.file.loader.domain.Picture
import com.file.loader.model.PictureStatus
import com.file.loader.repository.PictureRepository
import org.bson.types.ObjectId
import org.springframework.mock.web.MockMultipartFile
import spock.lang.Specification

class PictureServiceTest extends Specification {

    def repository = Mock(PictureRepository)
    def storage = new StorageConfiguration("uploads-test")

    def service = new PictureServiceImpl(repository, storage)

    def "should save an image into database and file storage"() {

        setup:
        def file = new MockMultipartFile("test", "test.png", "image/png", new byte[5])
        def callback = "http://localhost:8080/call/back"

        when:
        service.store(file, callback)

        then:
        1 * repository.save(_)
        notThrown(IOException)

    }

    def "should update picture in db with no errors"() {

        setup:
        def picture = new Picture(pictureName: "test1", pictureStatus: PictureStatus.SET_UP)

        when:
        service.updatePicture(picture)

        then:
        1 * repository.save(picture)
    }

//    TODO: implent last test
    def "LoadAsResource"() {

    }

    def "should return list of two items with status SET_UP"() {

        setup:
        def status = PictureStatus.SET_UP
        def pictures = [new Picture(pictureName: "test1", pictureStatus: PictureStatus.SET_UP), new Picture(pictureName: "test2", pictureStatus: PictureStatus.SET_UP)]
        when:
        service.findByStatus(status)

        then:
        1 * repository.findFirst50ByPictureStatus(status) >> pictures

    }

    def "should run delete with no errors"() {

        setup:
        def id = new ObjectId()
        def picture = new Picture(id: id, pictureName: "test", path: "uploads/" + UUID.randomUUID())

        when:
        service.delete(id)

        then:
        1 * repository.findById(id) >> Optional.of(picture)
        1 * repository.deleteById(id)
    }
}
