package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BasicBinaryContentService implements BinaryContentService {

    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentMapper binaryContentMapper;
    private final BinaryContentStorage binaryContentStorage;

    @Override
    @Transactional
    public BinaryContentResponse create(BinaryContentCreateRequest request) {
        BinaryContent saved = saveBinaryContent(request);
        return binaryContentMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public BinaryContent createBinaryContent(BinaryContentCreateRequest request) {
        return saveBinaryContent(request);
    }

    @Override
    @Transactional(readOnly = true)
    public BinaryContentResponse findById(UUID id) {
        BinaryContent binaryContent = getBinaryContentOrThrow(id);
        return binaryContentMapper.toResponse(binaryContent);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BinaryContentResponse> findAllByIdIn(List<UUID> ids) {
        return binaryContentRepository.findAllById(ids).stream()
                .map(binaryContentMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        BinaryContent binaryContent = getBinaryContentOrThrow(id);

        binaryContentRepository.delete(binaryContent);
        binaryContentStorage.delete(id);
    }

    private BinaryContent getBinaryContentOrThrow(UUID id) {
        return binaryContentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "BinaryContent with id " + id + " not found"
                ));
    }

    private BinaryContent saveBinaryContent(BinaryContentCreateRequest request) {
        BinaryContent binaryContent = binaryContentMapper.toEntity(request);
        BinaryContent saved = binaryContentRepository.save(binaryContent);

        binaryContentStorage.put(saved.getId(), request.data());

        return saved;
    }
}