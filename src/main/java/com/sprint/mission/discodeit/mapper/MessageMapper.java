package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.channel.Channel;
import com.sprint.mission.discodeit.entity.message.Message;
import com.sprint.mission.discodeit.entity.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface MessageMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "content", source = "request.content")
    @Mapping(target = "channel", source = "channel")
    @Mapping(target = "author", source = "author")
    @Mapping(target = "attachments", source = "attachments")
    Message toEntity(MessageCreateRequest request, Channel channel, User author, List<BinaryContent> attachments);

    @Mapping(target = "id", source = "message.id")
    @Mapping(target = "content", source = "message.content")
    @Mapping(target = "channelId", source = "message.channel.id")
    @Mapping(target = "authorId", source = "message.author.id")
    @Mapping(target = "createdAt", source = "message.createdAt")
    @Mapping(target = "updatedAt", source = "message.updatedAt")
    @Mapping(target = "attachmentIds", source = "attachmentIds")
    MessageResponse toResponse(Message message, List<UUID> attachmentIds);
}