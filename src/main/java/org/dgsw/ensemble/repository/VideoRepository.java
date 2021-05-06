package org.dgsw.ensemble.repository;

import org.dgsw.ensemble.domain.VideoData;

import java.util.List;
import java.util.Optional;

public interface VideoRepository {

    VideoData save(VideoData videoData);
    Optional<VideoData> findById(long id);
    List<VideoData> getList(long offset, long amount);


}
