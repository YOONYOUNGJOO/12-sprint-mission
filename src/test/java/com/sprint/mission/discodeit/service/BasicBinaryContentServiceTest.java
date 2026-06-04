package com.sprint.mission.discodeit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BasicBinaryContentServiceTest {

    @Mock
    private BinaryContentRepository binaryContentRepository;

    @Mock
    private BinaryContentMapper binaryContentMapper;

    @Mock
    private BinaryContentStorage binaryContentStorage;

    @InjectMocks
    private BasicBinaryContentService binaryContentService;

    @Test
    @DisplayName("바이너리 콘텐츠 생성 성공")
    void create_success() {
        BinaryContentCreateRequest request = new BinaryContentCreateRequest(
                "file-data".getBytes(),
                "test.txt",
                "text/plain"
        );

        UUID binaryContentId = UUID.randomUUID();

        BinaryContent binaryContent = BinaryContent.builder()
                .fileName("test.txt")
                .size(9L)
                .contentType("text/plain")
                .build();

        BinaryContent savedBinaryContent = BinaryContent.builder()
                .id(binaryContentId)
                .fileName("test.txt")
                .size(9L)
                .contentType("text/plain")
                .build();

        BinaryContentResponse expectedResponse = new BinaryContentResponse(
                binaryContentId,
                "test.txt",
                9L,
                "text/plain"
        );

        when(binaryContentMapper.toEntity(request)).thenReturn(binaryContent);
        when(binaryContentRepository.save(binaryContent)).thenReturn(savedBinaryContent);
        when(binaryContentStorage.put(binaryContentId, request.data())).thenReturn(binaryContentId);
        when(binaryContentMapper.toResponse(savedBinaryContent)).thenReturn(expectedResponse);

        BinaryContentResponse result = binaryContentService.create(request);

        assertThat(result).isEqualTo(expectedResponse);

        verify(binaryContentMapper).toEntity(request);
        verify(binaryContentRepository).save(binaryContent);
        verify(binaryContentStorage).put(binaryContentId, request.data());
        verify(binaryContentMapper).toResponse(savedBinaryContent);
    }

    @Test
    @DisplayName("바이너리 콘텐츠 엔티티 생성 성공")
    void createBinaryContent_success() {
        BinaryContentCreateRequest request = new BinaryContentCreateRequest(
                "file-data".getBytes(),
                "test.txt",
                "text/plain"
        );

        UUID binaryContentId = UUID.randomUUID();

        BinaryContent binaryContent = BinaryContent.builder()
                .fileName("test.txt")
                .size(9L)
                .contentType("text/plain")
                .build();

        BinaryContent savedBinaryContent = BinaryContent.builder()
                .id(binaryContentId)
                .fileName("test.txt")
                .size(9L)
                .contentType("text/plain")
                .build();

        when(binaryContentMapper.toEntity(request)).thenReturn(binaryContent);
        when(binaryContentRepository.save(binaryContent)).thenReturn(savedBinaryContent);
        when(binaryContentStorage.put(binaryContentId, request.data())).thenReturn(binaryContentId);

        BinaryContent result = binaryContentService.createBinaryContent(request);

        assertThat(result).isEqualTo(savedBinaryContent);

        verify(binaryContentMapper).toEntity(request);
        verify(binaryContentRepository).save(binaryContent);
        verify(binaryContentStorage).put(binaryContentId, request.data());
    }

    @Test
    @DisplayName("바이너리 콘텐츠 단건 조회 성공")
    void findById_success() {
        UUID binaryContentId = UUID.randomUUID();

        BinaryContent binaryContent = BinaryContent.builder()
                .id(binaryContentId)
                .fileName("test.txt")
                .size(9L)
                .contentType("text/plain")
                .build();

        BinaryContentResponse expectedResponse = new BinaryContentResponse(
                binaryContentId,
                "test.txt",
                9L,
                "text/plain"
        );

        when(binaryContentRepository.findById(binaryContentId))
                .thenReturn(Optional.of(binaryContent));
        when(binaryContentMapper.toResponse(binaryContent))
                .thenReturn(expectedResponse);

        BinaryContentResponse result = binaryContentService.findById(binaryContentId);

        assertThat(result).isEqualTo(expectedResponse);

        verify(binaryContentRepository).findById(binaryContentId);
        verify(binaryContentMapper).toResponse(binaryContent);
    }

    @Test
    @DisplayName("바이너리 콘텐츠 단건 조회 실패 - 없음")
    void findById_fail_notFound() {
        UUID binaryContentId = UUID.randomUUID();

        when(binaryContentRepository.findById(binaryContentId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> binaryContentService.findById(binaryContentId))
                .isInstanceOf(BinaryContentNotFoundException.class);

        verify(binaryContentRepository).findById(binaryContentId);
    }

    @Test
    @DisplayName("바이너리 콘텐츠 목록 조회 성공")
    void findAllByIdIn_success() {
        UUID binaryContentId1 = UUID.randomUUID();
        UUID binaryContentId2 = UUID.randomUUID();

        BinaryContent binaryContent1 = BinaryContent.builder()
                .id(binaryContentId1)
                .fileName("a.txt")
                .size(1L)
                .contentType("text/plain")
                .build();

        BinaryContent binaryContent2 = BinaryContent.builder()
                .id(binaryContentId2)
                .fileName("b.txt")
                .size(1L)
                .contentType("text/plain")
                .build();

        BinaryContentResponse response1 = new BinaryContentResponse(
                binaryContentId1,
                "a.txt",
                1L,
                "text/plain"
        );

        BinaryContentResponse response2 = new BinaryContentResponse(
                binaryContentId2,
                "b.txt",
                1L,
                "text/plain"
        );

        List<UUID> ids = List.of(binaryContentId1, binaryContentId2);

        when(binaryContentRepository.findAllById(ids))
                .thenReturn(List.of(binaryContent1, binaryContent2));
        when(binaryContentMapper.toResponse(binaryContent1)).thenReturn(response1);
        when(binaryContentMapper.toResponse(binaryContent2)).thenReturn(response2);

        List<BinaryContentResponse> result = binaryContentService.findAllByIdIn(ids);

        assertThat(result).containsExactly(response1, response2);

        verify(binaryContentRepository).findAllById(ids);
        verify(binaryContentMapper).toResponse(binaryContent1);
        verify(binaryContentMapper).toResponse(binaryContent2);
    }

    @Test
    @DisplayName("바이너리 콘텐츠 삭제 성공")
    void delete_success() {
        UUID binaryContentId = UUID.randomUUID();

        BinaryContent binaryContent = BinaryContent.builder()
                .id(binaryContentId)
                .fileName("test.txt")
                .size(9L)
                .contentType("text/plain")
                .build();

        when(binaryContentRepository.findById(binaryContentId))
                .thenReturn(Optional.of(binaryContent));

        binaryContentService.delete(binaryContentId);

        verify(binaryContentRepository).findById(binaryContentId);
        verify(binaryContentRepository).delete(binaryContent);
        verify(binaryContentStorage).delete(binaryContentId);
    }

    @Test
    @DisplayName("바이너리 콘텐츠 삭제 실패 - 없음")
    void delete_fail_notFound() {
        UUID binaryContentId = UUID.randomUUID();

        when(binaryContentRepository.findById(binaryContentId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> binaryContentService.delete(binaryContentId))
                .isInstanceOf(BinaryContentNotFoundException.class);

        verify(binaryContentRepository).findById(binaryContentId);
    }
}