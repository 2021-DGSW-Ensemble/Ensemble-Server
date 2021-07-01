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
    @Transactional
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
    @Transactional(readOnly = true)
    public Optional<VideoData> findById(long id) {
        VideoData videoData = em.find(VideoData.class, id);
        return Optional.ofNullable(videoData);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VideoData> getList(int offset, int amount) {
        // https://thorben-janssen.com/pagination-jpa-hibernate/
        return em.createQuery("SELECT v FROM video_data v", VideoData.class)
                .setFirstResult(offset)
                .setMaxResults(amount)
                .getResultList();
    }
}
