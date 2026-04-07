package com.sprint.mission.discodeit.domain;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class BinaryContent implements Serializable {
    private UUID id;
    private Instant createdAt;
    //
    private byte[] data;
    private String filename;

    public BinaryContent(byte[] data, String filename) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.data = data;
        this.filename = filename;
    }


}
