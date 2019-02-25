package com.file.loader.service.notifier;


import com.file.loader.domain.Picture;
import com.file.loader.model.PictureStatus;
import com.file.loader.service.picture.PictureService;
import com.file.loader.utils.ApiVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class NotifierServiceImpl implements NotifierService {

    private final Logger logger = LoggerFactory.getLogger(NotifierServiceImpl.class);

    @Value("${service.urn}")
    private String urn;
    @Value("${service.url}")
    private String url;
    @Value("${server.port}")
    private int serverPort;
    @Value("${service.pictures}")
    private String picturesPath;
    @Value("${service.retry.time}")
    private long maxRetry;

    private final PictureService pictureService;
    private final RestTemplate restTemplate;

    public NotifierServiceImpl(PictureService pictureService, RestTemplate restTemplate) {
        this.pictureService = pictureService;
        this.restTemplate = restTemplate;
    }

    @Override
    @Scheduled(fixedDelay = 1000 * 60 * 5)
    public void sendNotification() {
        List<Picture> pictures = getPicturesForSendingCallbacks();
        logger.info("Starting cron task");
        pictures.forEach(picture -> {
            String callbackUrl = picture.getCallbackUrl();

            UriComponents downloadUrl = UriComponentsBuilder.newInstance()
                    .scheme(urn).host(url).port(serverPort).path(ApiVersion.V1).path(picturesPath).path(picture.getId().toString()).query("size={keyword}").buildAndExpand("small");
            HttpEntity<?> entity = new HttpEntity<>(downloadUrl);
            try {
                restTemplate.exchange(callbackUrl, HttpMethod.POST, entity, String.class);
                logger.info("resource {} was successfully notified ", picture.getCallbackUrl());
                picture.setPictureStatus(PictureStatus.PROCESSED);
                updateDataAndTimeAdnSave(picture);
            } catch (HttpClientErrorException e) {
                if (picture.getSendCounter() <= maxRetry) {
                    logger.info("sending notification to resource {} was failed {} times", picture.getCallbackUrl(), picture.getSendCounter());
                    picture.setSendCounter(picture.getSendCounter() + 1);
                    updateDataAndTimeAdnSave(picture);

                } else {
                    logger.info("failed notifying the resource {}", picture.getCallbackUrl());
                    picture.setPictureStatus(PictureStatus.FAILED);
                    updateDataAndTimeAdnSave(picture);
                }
            }
        });
    }

    private List<Picture> getPicturesForSendingCallbacks() {
        return pictureService.findByStatus(PictureStatus.RESIZED);
    }

    private void updateDataAndTimeAdnSave(Picture picture) {
        picture.setUpdatedDate(LocalDateTime.now());
        pictureService.updatePicture(picture);
    }
}
