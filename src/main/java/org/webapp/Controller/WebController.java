package org.webapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.webapp.dataset.S3Connector;

import java.io.IOException;

@Controller
class WebController {
    @Autowired
    private S3Connector s3Connector;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping(value = "/UploadController", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public String upload(@RequestPart("file") MultipartFile file) throws IOException {
        try {
            return s3Connector.upload(file, "static");
        } catch(Exception e) {
            System.out.println(e.getCause());
            return "";
        }
    }
}

