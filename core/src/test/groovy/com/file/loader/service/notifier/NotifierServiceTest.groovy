package com.file.loader.service.notifier

import com.file.loader.model.PictureStatus
import com.file.loader.service.picture.PictureService
import org.springframework.web.client.RestTemplate
import spock.lang.Specification

class NotifierServiceTest extends Specification {

    def pictureService = Mock(PictureService)
    def restTemplate = new RestTemplate()
    def service = new NotifierServiceImpl(pictureService, restTemplate)

    def "should run cron job and do nothing  as there is no elements"() {
        when:
        service.sendNotification()

        then:
        1 * pictureService.findByStatus(PictureStatus.RESIZED) >> []

    }
}
