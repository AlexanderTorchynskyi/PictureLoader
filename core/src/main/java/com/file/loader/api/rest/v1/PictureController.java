package com.file.loader.api.rest.v1;

import com.file.loader.model.FIleFormat;
import com.file.loader.service.picture.PictureService;
import com.file.loader.utils.ApiVersion;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(ApiVersion.V1 + "/pictures")
public class PictureController {

    private final static Logger logger = LoggerFactory.getLogger(PictureController.class);

    private final PictureService pictureService;

    public PictureController(PictureService pictureService) {
        this.pictureService = pictureService;
    }

    //TODO: Think about throwing error in case of wrong format
    @PostMapping
    public ResponseEntity<?> create(@RequestHeader(value = "callbackUrl") String callbackUrl, @RequestParam("file") MultipartFile file) {
        //Check file  format
        if (file.getContentType().equals(FIleFormat.JPEG.getFormat()) ||
                file.getContentType().equals(FIleFormat.JPG.getFormat()) ||
                file.getContentType().equals(FIleFormat.PNG.getFormat())) {

            pictureService.store(file, callbackUrl);
            String contentType = file.getContentType();
            logger.info("content type is {}", contentType);
            return ResponseEntity.accepted().build();

        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    //TODO: the size param might be better to make enum
    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable("id") ObjectId id, @RequestParam("size") String size) {
        Resource resource = pictureService.loadAsResource(id, size);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") ObjectId id) {
        pictureService.delete(id);
        return ResponseEntity.ok().build();
    }
}
