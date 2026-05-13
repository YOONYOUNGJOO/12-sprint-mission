package com.sprint.mission.discodeit.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Component
public class LocalBinaryContentStorage implements BinaryContentStorage{

    private final Path rootPath;

    public LocalBinaryContentStorage(
            @Value("${discodeit.storage.local.root-path}") String rootPath
    ) {
        this.rootPath = Path.of(rootPath);
        createDirectoryIfNotExists();
    }

    @Override
    public void put(UUID id, byte[] bytes) {
        try {
            Path path =resolvePath(id);
            Files.write(path, bytes);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store binary content : " + id, e);
        }

    }

    @Override
    public byte[] get(UUID id) {
        try {
            Path path =resolvePath(id);
            return Files.readAllBytes(path);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read binary content: " + id, e);
        }
    }

    @Override
    public void delete(UUID id) {
        try {
            Path path = resolvePath(id);
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete binary content: " + id, e);
        }

    }

    private Path resolvePath(UUID id) {
        return rootPath.resolve(id.toString());
    }

    private void createDirectoryIfNotExists() {
        try {
            Files.createDirectories(rootPath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create storage directory : ", e);
        }
    }
}
