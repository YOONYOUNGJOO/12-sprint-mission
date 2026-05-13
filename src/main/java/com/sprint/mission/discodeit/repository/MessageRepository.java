package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.message.Message;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, UUID> {

    List<Message> findAllByChannel_Id(UUID channelId);

    List<Message> findAllByChannel_IdOrderByCreatedAtDesc(UUID channelId, Pageable pageable);

    List<Message> findAllByChannel_IdAndCreatedAtLessThanOrderByCreatedAtDesc(UUID channelId, Instant cursor,
                                                                              Pageable pageable);
}