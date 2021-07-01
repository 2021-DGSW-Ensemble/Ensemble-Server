package org.dgsw.ensemble.service;

import org.dgsw.ensemble.domain.VideoData;
import org.dgsw.ensemble.repository.VideoRepository;
import org.hibernate.annotations.common.util.impl.Log;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Transactional
@SpringBootTest
public class VideoDataServiceTest {

    @Autowired VideoService videoService;
    @Autowired VideoRepository videoRepository;

    @Test
    void add_video() {
        // given
        VideoData videoData = new VideoData();
        videoData.setName("test1234");

        // when
        long id = videoService.save(videoData).getId();

        videoData.setUrl(id + "-" + videoData.getName());
        videoService.update(videoData);

        // then
        Optional<VideoData> result = videoService.findById(id);
        if(result.get() == null) {
            throw new NullPointerException();
        }
        System.out.println("success!!, " + id);



    }

}
