package org.dgsw.ensemble.repository;

import org.dgsw.ensemble.domain.VideoData;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

@Repository
public class JpaVideoRepository implements VideoRepository {

    private final EntityManager em;

    public JpaVideoRepository(EntityManager em) {
        this.em = em;
    }

    @Override
    public VideoData save(VideoData videoData) {
        em.persist(videoData);
        return videoData;
    }

    @Override
    @Transactional
    public void update(VideoData videoData) {
        em.merge(videoData);
    }

    @Override
    @Transactional
    public void remove(VideoData videoData) {
        em.remove(videoData);
    }

    @Override
    public Optional<VideoData> findById(long id) {
        VideoData videoData = em.find(VideoData.class, id);
        return Optional.ofNullable(videoData);
    }

    @Override
    public List<VideoData> getList(long offset, long amount) {
        return em.createQuery("select v from video_data v OFFSET :offset LIMIT :amount", VideoData.class)
                .setParameter("offset", offset)
                .setParameter("amount", amount)
                .getResultList();
    }
}
