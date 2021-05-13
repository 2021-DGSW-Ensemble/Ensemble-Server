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
import java.util.List;
import java.util.Objects;

@Controller
public class VideoController {

    private final VideoService videoService;

    @Autowired
    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }

    @RequestMapping("/form")
    public String form() {
        return "form";
    }

    @RequestMapping(value = "/video_upload", method = RequestMethod.POST)
    public String video_upload(@RequestParam("file") MultipartFile multipartFile, @RequestParam("name") String name) {

        VideoData videoData = new VideoData();
        videoData.setName(name);
        long id = videoService.save(videoData).getId();

        String fileName = id + "-" + name + Objects.requireNonNull(multipartFile.getOriginalFilename()).substring(multipartFile.getOriginalFilename().lastIndexOf('.'));
        File targetFile = new File("./video/" + fileName);
        videoData.setUrl("video/" + fileName);

        try {
            InputStream fileStream = multipartFile.getInputStream();
            FileUtils.copyInputStreamToFile(fileStream, targetFile);
            videoService.update(videoData);
        } catch (IOException e) {
            FileUtils.deleteQuietly(targetFile);
            videoService.remove(videoData);
            e.printStackTrace();
        }

        return "redirect:/form";
    }

    @GetMapping("/api/v1/video_list")
    public List<VideoData> video_list(@RequestParam("offset") Long offset, @RequestParam("amount") Long amount) {

        return null;
    }

}
