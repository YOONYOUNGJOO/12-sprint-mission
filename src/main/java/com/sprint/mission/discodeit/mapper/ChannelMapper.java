package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.entity.channel.Channel;
import org.mapstruct.Mapper;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface ChannelMapper {

    ChannelResponse toResponse(Channel channel, Instant lastMessageAt, List<UUID> participantIds);
}