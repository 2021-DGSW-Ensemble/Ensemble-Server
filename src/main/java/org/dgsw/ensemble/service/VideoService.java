package org.dgsw.ensemble.service;

import org.dgsw.ensemble.domain.VideoData;
import org.dgsw.ensemble.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
public class VideoService {

    private final VideoRepository videoRepository;

    @Autowired
    public VideoService(VideoRepository videoRepository) {
        this.videoRepository = videoRepository;
    }

    public Long add(VideoData videoData) {
        videoRepository.save(videoData);
        return videoData.getId();
    }

    public List<VideoData> getList(long offset, long amount) {
        return videoRepository.getList(offset, amount);
    }

    public Optional<VideoData> findById(long id) {
        return videoRepository.findById(id);
    }


}
