package org.dgsw.ensemble.repository;

import org.dgsw.ensemble.domain.VideoData;

import java.util.List;
import java.util.Optional;

public interface VideoRepository {

    VideoData save(VideoData videoData);
    void update(VideoData videoData);
    void remove(VideoData videoData);

    Optional<VideoData> findById(long id);
    List<VideoData> getList(int offset, int amount);


}
