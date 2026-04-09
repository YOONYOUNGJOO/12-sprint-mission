package com.sprint.mission.discodeit.dto;

public record BinaryContentCreateRequest (
        byte[] data,
        String filename,
        String mimeType
) {
}
