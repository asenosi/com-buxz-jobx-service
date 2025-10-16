package com.buxz.dev.combuxzjobxservice.service;

import com.buxz.dev.combuxzjobxservice.domain.ResponseMessage;
import com.buxz.dev.combuxzjobxservice.domain.TestimonialDto;
import com.buxz.dev.combuxzjobxservice.entity.TestimonialEntity;
import com.buxz.dev.combuxzjobxservice.entity.UserProfileEntity;
import com.buxz.dev.combuxzjobxservice.repository.TestimonialRepository;
import com.buxz.dev.combuxzjobxservice.repository.UserProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.mock.web.MockMultipartFile;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TestimonialServiceTest {

    @Mock
    private TestimonialRepository testimonialRepository;

    @Mock
    private UserProfileRepository userProfileRepository;

    @Mock
    private UploadService uploadService;

    @InjectMocks
    private TestimonialService testimonialService;

    private TestimonialDto testimonialDto;
    private UserProfileEntity userProfile;

    @BeforeEach
    void setUp() {
        testimonialDto = new TestimonialDto();
        testimonialDto.setTestimonialBy("Jane");
        testimonialDto.setTestimonialSummary("Great work");
        testimonialDto.setTestimonialDescription("Detailed feedback");

        userProfile = new UserProfileEntity();
        userProfile.setId(1);
        userProfile.setTestimonialEntities(new ArrayList<>());
    }

    @Test
    void addTestimonial_WhenProfileExists_ShouldPersistTestimonial() {
        when(userProfileRepository.findById(1)).thenReturn(Optional.of(userProfile));
        when(testimonialRepository.saveAndFlush(any(TestimonialEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userProfileRepository.save(any(UserProfileEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TestimonialEntity result = testimonialService.addTestimonial(1, testimonialDto);

        assertEquals(testimonialDto.getTestimonialBy(), result.getTestimonialBy());
        assertEquals(testimonialDto.getTestimonialSummary(), result.getTestimonialSummary());
        assertEquals(testimonialDto.getTestimonialDescription(), result.getTestimonialDescription());
        assertTrue(userProfile.getTestimonialEntities().contains(result));
        verify(testimonialRepository).saveAndFlush(result);
        verify(userProfileRepository).save(userProfile);
    }

    @Test
    void addTestimonial_WhenProfileMissing_ShouldReturnEmptyEntity() {
        when(userProfileRepository.findById(1)).thenReturn(Optional.empty());

        TestimonialEntity result = testimonialService.addTestimonial(1, testimonialDto);

        assertNull(result.getTestimonialBy());
        verify(testimonialRepository, never()).saveAndFlush(any());
        verify(userProfileRepository, never()).save(any());
    }

    @Test
    void updateTestimonial_WhenEntityExists_ShouldUpdateAndSave() {
        TestimonialEntity testimonial = new TestimonialEntity();
        when(testimonialRepository.findById(2)).thenReturn(Optional.of(testimonial));
        when(testimonialRepository.save(any(TestimonialEntity.class))).thenReturn(testimonial);

        TestimonialEntity result = testimonialService.updateTestimonial(1, 2, testimonialDto);

        assertEquals(testimonialDto.getTestimonialBy(), result.getTestimonialBy());
        assertEquals(testimonialDto.getTestimonialSummary(), result.getTestimonialSummary());
        verify(testimonialRepository).save(testimonial);
    }

    @Test
    void updateTestimonial_WhenEntityMissing_ShouldThrowException() {
        when(testimonialRepository.findById(3)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> testimonialService.updateTestimonial(1, 3, testimonialDto));
    }

    @Test
    void deleteTestimonial_ShouldInvokeRepositoryDelete() {
        ResponseMessage response = testimonialService.deleteTestimonial(1, 5);

        assertEquals("DeleteTestimonial : Testimonial deleted successfully", response.getMessage());
        verify(testimonialRepository).deleteById(5);
    }

    @Test
    void getTestimonialById_ShouldReturnRepositoryOptional() {
        TestimonialEntity testimonial = new TestimonialEntity();
        when(testimonialRepository.findById(4)).thenReturn(Optional.of(testimonial));

        Optional<TestimonialEntity> result = testimonialService.getTestimonialById(4);

        assertTrue(result.isPresent());
        assertEquals(testimonial, result.get());
    }

    @Test
    void uploadTestimonial_WhenProfileExists_ShouldUploadAndPersist() {
        MultipartFile file = new MockMultipartFile("file", "testimonial.txt", "text/plain", "data".getBytes());
        when(userProfileRepository.findById(1)).thenReturn(Optional.of(userProfile));
        when(testimonialRepository.saveAndFlush(any(TestimonialEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userProfileRepository.save(any(UserProfileEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseMessage message = testimonialService.uploadTestimonial(1, file);

        assertEquals("Uploaded the file successfully: testimonial.txt", message.getMessage());
        assertEquals(1, userProfile.getTestimonialEntities().size());
        assertEquals("testimonial.txt", userProfile.getTestimonialEntities().get(0).getFileName());
        verify(uploadService).uploadFile(file);
    }

    @Test
    void uploadTestimonial_WhenProfileMissing_ShouldReturnFailureMessage() {
        MultipartFile file = new MockMultipartFile("file", "testimonial.txt", "text/plain", "data".getBytes());
        when(userProfileRepository.findById(1)).thenReturn(Optional.empty());

        ResponseMessage message = testimonialService.uploadTestimonial(1, file);

        assertEquals("Failed to add&upload testimonial, because profile not found", message.getMessage());
        verify(uploadService, never()).uploadFile(any());
        verify(testimonialRepository, never()).saveAndFlush(any());
    }
}
