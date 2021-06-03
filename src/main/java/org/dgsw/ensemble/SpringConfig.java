package org.dgsw.ensemble;

import org.dgsw.ensemble.repository.VideoRepository;
import org.dgsw.ensemble.service.VideoService;
import org.dgsw.ensemble.service.VideoStreamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringConfig {

    private final VideoRepository videoRepository;

    @Autowired
    public SpringConfig(VideoRepository videoRepository) {
        this.videoRepository = videoRepository;
    }

    @Bean
    public VideoService videoService() {
        return new VideoService(videoRepository);
    }


}
