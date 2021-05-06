package org.dgsw.ensemble.controller;

import org.apache.commons.io.FileUtils;
import org.dgsw.ensemble.domain.VideoData;
import org.dgsw.ensemble.repository.VideoRepository;
import org.dgsw.ensemble.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@Controller
public class VideoController {

    private VideoService videoService;

    @Autowired
    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }

    @RequestMapping("/form")
    public String form() {
        return "form";
    }

    @RequestMapping(value = "/video_upload", method = RequestMethod.POST)
    public String video_upload(@RequestParam("file") MultipartFile multipartFile) {



        File targetFile = new File("./video/" + multipartFile.getOriginalFilename());
        try {
            InputStream fileStream = multipartFile.getInputStream();
            FileUtils.copyInputStreamToFile(fileStream, targetFile);
        } catch (IOException e) {
            FileUtils.deleteQuietly(targetFile);
            e.printStackTrace();
        }

        return "redirect:/form";
    }

    @GetMapping("/api/v1/video_list")
    public VideoData video_list(@RequestParam("offset") Long offset, @RequestParam("amount") Long amount) {

        return null;
    }

}
