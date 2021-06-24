package org.dgsw.ensemble.controller;

import org.apache.commons.io.FileUtils;
import org.dgsw.ensemble.domain.VideoData;
import org.dgsw.ensemble.repository.VideoRepository;
import org.dgsw.ensemble.service.VideoService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
public class VideoController {

    private final VideoService videoService;

    @Autowired
    public VideoController(VideoService videoService) {
        this.videoService = videoService;
        LoggerFactory.getLogger(this.getClass()).info("VideoController init");
    }

    @RequestMapping("/form")
    public String form() {
        return "form";
    }

    @RequestMapping(value = "/video_upload", method = RequestMethod.POST)
    public String video_upload(@RequestParam("file") MultipartFile multipartFile, @RequestParam("name") String name) {

        VideoData videoData = new VideoData();
        videoData.setName(name);
        videoData.setTime(System.currentTimeMillis());
        long id = videoService.save(videoData).getId();

        String ext = Objects.requireNonNull(multipartFile.getOriginalFilename()).substring(multipartFile.getOriginalFilename().lastIndexOf('.'));
        String path = videoService.getPath(id, name, ext);
        videoData.setUrl(path);
        File targetFile = new File(path);

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

    // @RequestMapping(value = "/video_upload", method = RequestMethod.POST)

    @RequestMapping(value = "/convert_video")
    public String convertVideo(@RequestParam("id") Long id) {
        Optional<VideoData> videoDataOptional = videoService.findById(id);

        if (videoDataOptional.isEmpty()) {
            return "{\"status\":\"fail\"}";
        }

        VideoData videoData = videoDataOptional.get();

        try {
            String absolutePath = new java.io.File(".").getCanonicalPath();

            String outPath = absolutePath + "\\converted\\" + videoData.getUrl();

            Runtime.getRuntime().exec("python " + absolutePath + " \\converter\\converter.py -I " + videoData.getId() + " -P " + videoData.getUrl(),
                    null, new File(absolutePath + "\\converter\\"));
        } catch (IOException e) {
            e.printStackTrace();
            return "{\"status\":\"fail\"}";
        }

        return "{\"status\":\"success\"}";
    }

    @RequestMapping(value = "/convert_video_progress")
    public String convertVideoProgress(@RequestParam("id") Long id, @RequestParam("progress") Long progress) {
        Optional<VideoData> videoDataOptional = videoService.findById(id);

        if (videoDataOptional.isEmpty()) {
            return "{\"status\":\"fail\"}";
        }

        VideoData videoData = videoDataOptional.get();
        videoData.setProgress((float) progress);
        videoService.update(videoData);

        return "{\"status\":\"success\"}";
    }

    @ResponseBody
    @GetMapping("/api/v1/video_list")
    public List<VideoData> video_list(@RequestParam("offset") Long offset, @RequestParam("amount") Long amount) {
        return videoService.getList((int)(long) offset, (int)(long) amount);
    }

}
