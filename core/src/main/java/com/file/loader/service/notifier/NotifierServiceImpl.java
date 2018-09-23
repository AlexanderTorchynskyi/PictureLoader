package com.file.loader.service.notifier;


import com.file.loader.domain.Picture;
import com.file.loader.model.PictureStatus;
import com.file.loader.service.picture.PictureService;
import com.file.loader.utils.ApiVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Component
public class NotifierServiceImpl implements NotifierService {

    private final Logger logger = LoggerFactory.getLogger(NotifierServiceImpl.class);

    private final PictureService pictureService;
    private final RestTemplate restTemplate;
    private final long MAX_RETRY = 10;// TODO; set from properties

    public NotifierServiceImpl(PictureService pictureService, RestTemplate restTemplate) {
        this.pictureService = pictureService;
        this.restTemplate = restTemplate;
    }


    //TODO: move all hardcode to properties. Think where to keep all routes
    @Override
    @Scheduled(fixedDelay = 1000 * 60 * 5)
    public void sendNotification() {
        List<Picture> pictures = getPicturesForSendingCallbacks();
        pictures.forEach(picture -> {

            String callbackUrl = picture.getCallbackUrl();

            UriComponents downloadUrl = UriComponentsBuilder.newInstance()
                    .scheme("http").host("localhost").port(8080).path(ApiVersion.V1).path("/pictures/").path(picture.getId().toString()).query("size={keyword}").buildAndExpand("small");
            System.out.println(downloadUrl.toUriString());
            HttpEntity<?> entity = new HttpEntity<>("fsd");

            try {   
                restTemplate.exchange(callbackUrl, HttpMethod.POST, entity, String.class);
                logger.info("resource {} was successfully notified ", picture.getCallbackUrl());
                picture.setPictureStatus(PictureStatus.PROCESSED);
                pictureService.updatePicture(picture);
            } catch (HttpClientErrorException e) {
                if (picture.getSendCounter() <= MAX_RETRY) {
                    logger.info("sending notification to resource {} was failed {} times", picture.getCallbackUrl(), picture.getSendCounter());
                    picture.setSendCounter(picture.getSendCounter() + 1);
                    pictureService.updatePicture(picture);

                } else {
                    logger.info("failed notifying the resource {}", picture.getCallbackUrl());
                    picture.setPictureStatus(PictureStatus.FAILED);
                    pictureService.updatePicture(picture);
                }
            }
        });
    }

    private List<Picture> getPicturesForSendingCallbacks() {
        return pictureService.findByStatus(PictureStatus.RESIZED);
    }
}
