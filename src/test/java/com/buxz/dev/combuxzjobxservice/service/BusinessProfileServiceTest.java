package com.buxz.dev.combuxzjobxservice.service;

import static org.junit.jupiter.api.Assertions.*;

import com.buxz.dev.combuxzjobxservice.domain.BusinessProfileDto;
import com.buxz.dev.combuxzjobxservice.domain.ResponseMessage;
import com.buxz.dev.combuxzjobxservice.entity.BusinessProfileEntity;
import com.buxz.dev.combuxzjobxservice.entity.embeddables.*;
import com.buxz.dev.combuxzjobxservice.exception.NotFoundExeption;
import com.buxz.dev.combuxzjobxservice.repository.BusinessProfileRepository;
import com.buxz.dev.combuxzjobxservice.repository.ImageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BusinessProfileServiceTest {

    @Mock
    private BusinessProfileRepository businessProfileRepository;

    @Mock
    private ImageRepository imageRepository;

    @Mock
    private UploadService uploadService;

    @InjectMocks
    private BusinessProfileService businessProfileService;

    private BusinessProfileDto testBusinessProfileDto;
    private BusinessProfileEntity testBusinessProfileEntity;
    private final int TEST_PROFILE_ID = 1;

    @BeforeEach
    void setUp() {
        testBusinessProfileDto = BusinessProfileDto.builder()
                .businessName("Test Business")
                .businessOwner("John Doe")
                .businessCategory(BusinessCategory.FASHION)
                .address(BusinessAddress.builder().build())
                .contactDetails(ContactDetails.builder().build())
                .businessStartDate(LocalDateTime.now().minusYears(1).toString())
                .operationStartDate(LocalDateTime.now().toString())
                .operationEndDate(LocalDateTime.now().plusHours(8).toString())
                .deliver(true)
                .build();

        testBusinessProfileEntity = BusinessProfileEntity.builder()
                .id(TEST_PROFILE_ID)
                .businessName("Test Business")
                .businessOwner("John Doe")
                .businessCategory(BusinessCategory.FASHION)
                .address(BusinessAddress.builder().build())
                .contactDetails(ContactDetails.builder().build())
                .businessStartDate(LocalDateTime.now().minusYears(1).toString())
                .operationStartDate(LocalDateTime.now().toString())
                .operationEndDate(LocalDateTime.now().plusHours(8).toString())
                .profileStatus(ProfileStatus.ACTIVE)
                .showProfile(true)
                .deliver(true)
                .build();
    }

    @Test
    void createBusinessProfile_ShouldCreateAndSaveBusinessProfile() {
        // Arrange
        when(businessProfileRepository.save(any(BusinessProfileEntity.class)))
                .thenReturn(testBusinessProfileEntity);

        // Act
        BusinessProfileEntity result = businessProfileService.createBusinessProfile(testBusinessProfileDto);

        // Assert
        assertNotNull(result);
        assertEquals(testBusinessProfileDto.getBusinessName(), result.getBusinessName());
        assertEquals(testBusinessProfileDto.getBusinessOwner(), result.getBusinessOwner());
        assertEquals(ProfileStatus.ACTIVE, result.getProfileStatus());
        assertTrue(result.isShowProfile());

        verify(businessProfileRepository, times(1)).save(any(BusinessProfileEntity.class));
    }

    @Test
    void getListOfAllBusinessProfiles_ShouldReturnAllProfiles() {
        // Arrange
        List<BusinessProfileEntity> expectedProfiles = List.of(testBusinessProfileEntity);
        when(businessProfileRepository.findAll()).thenReturn(expectedProfiles);

        // Act
        List<BusinessProfileEntity> result = businessProfileService.getListOfAllBusinessProfiles();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(businessProfileRepository, times(1)).findAll();
    }

    @Test
    void getListOfBusinessProfilesContaining_ShouldFilterProfilesByName() {
        // Arrange
        String searchTerm = "Test";
        List<BusinessProfileEntity> allProfiles = List.of(
                testBusinessProfileEntity,
                BusinessProfileEntity.builder().businessName("Another Business").build()
        );
        when(businessProfileRepository.findAll()).thenReturn(allProfiles);

        // Act
        List<BusinessProfileEntity> result = businessProfileService.getListOfBusinessProfilesContaining(searchTerm);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).getBusinessName().contains(searchTerm));
    }

    @Test
    void getBusinessProfilesByCategory_ShouldFilterProfilesByCategory() {
        // Arrange
        BusinessCategory category = BusinessCategory.FASHION;
        List<BusinessProfileEntity> allProfiles = List.of(
                testBusinessProfileEntity,
                BusinessProfileEntity.builder().businessCategory(BusinessCategory.CAPENTRY).build()
        );
        when(businessProfileRepository.findAll()).thenReturn(allProfiles);

        // Act
        List<BusinessProfileEntity> result = businessProfileService.getBusinessProfilesByCategory(category);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(category, result.get(0).getBusinessCategory());
    }

    @Test
    void getBusinessProfileById_WhenProfileExists_ShouldReturnProfile() {
        // Arrange
        when(businessProfileRepository.findById(TEST_PROFILE_ID))
                .thenReturn(Optional.of(testBusinessProfileEntity));

        // Act
        Optional<BusinessProfileEntity> result = businessProfileService.getBusinessProfileById(TEST_PROFILE_ID);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(TEST_PROFILE_ID, result.get().getId());
        verify(businessProfileRepository, times(1)).findById(TEST_PROFILE_ID);
    }

    @Test
    void getBusinessProfileById_WhenProfileNotExists_ShouldReturnEmpty() {
        // Arrange
        when(businessProfileRepository.findById(TEST_PROFILE_ID)).thenReturn(Optional.empty());

        // Act
        Optional<BusinessProfileEntity> result = businessProfileService.getBusinessProfileById(TEST_PROFILE_ID);

        // Assert
        assertTrue(result.isEmpty());
        verify(businessProfileRepository, times(1)).findById(TEST_PROFILE_ID);
    }

    @Test
    void updateBusinessProfile_WhenProfileExists_ShouldUpdateAndSave() {
        // Arrange
        BusinessProfileDto updateDto = BusinessProfileDto.builder()
                .businessName("Updated Business Name")
                .businessOwner("Jane Doe")
                .businessCategory(BusinessCategory.GARDEN)
                .address(BusinessAddress.builder().build())
                .contactDetails(ContactDetails.builder().build())
                .businessStartDate(LocalDateTime.now().minusYears(2).toString())
                .operationStartDate(LocalDateTime.now().plusHours(1).toString())
                .operationEndDate(LocalDateTime.now().plusHours(10).toString())
                .deliver(false)
                .build();

        when(businessProfileRepository.findById(TEST_PROFILE_ID))
                .thenReturn(Optional.of(testBusinessProfileEntity));
        when(businessProfileRepository.save(any(BusinessProfileEntity.class)))
                .thenReturn(testBusinessProfileEntity);

        // Act
        BusinessProfileEntity result = businessProfileService.updateBusinessProfile(TEST_PROFILE_ID, updateDto);

        // Assert
        assertNotNull(result);
        assertEquals(updateDto.getBusinessName(), result.getBusinessName());
        assertEquals(updateDto.getBusinessOwner(), result.getBusinessOwner());
        assertEquals(updateDto.getBusinessCategory(), result.getBusinessCategory());
        assertEquals(ProfileStatus.ACTIVE, result.getProfileStatus());

        verify(businessProfileRepository, times(1)).findById(TEST_PROFILE_ID);
        verify(businessProfileRepository, times(1)).save(testBusinessProfileEntity);
    }

    @Test
    void updateBusinessProfile_WhenProfileNotExists_ShouldThrowException() {
        // Arrange
        when(businessProfileRepository.findById(TEST_PROFILE_ID)).thenReturn(Optional.empty());


        // Act
        Exception exception =assertThrows(NotFoundExeption.class, () -> {
            businessProfileService.updateBusinessProfile(TEST_PROFILE_ID, testBusinessProfileDto);
        });
        // Assert
        assertEquals("Business Profile not found", exception.getMessage());
        verify(businessProfileRepository, times(1)).findById(TEST_PROFILE_ID);
        verify(businessProfileRepository, never()).save(any(BusinessProfileEntity.class));
    }

    @Test
    void updateBusinessProfile_WhenProfileExists_ShouldUpdate() {
        // Arrange
        BusinessProfileEntity businessProfile = BusinessProfileEntity.builder()
                .businessCategory(BusinessCategory.FASHION)
                .businessName("Some Business I was supposed to start")
                .showProfile(true)
                .profileStatus(ProfileStatus.ACTIVE)
                .build();
        when(businessProfileRepository.findById(TEST_PROFILE_ID)).thenReturn(Optional.of(businessProfile));


        // Act
        BusinessProfileEntity result = businessProfileService.updateBusinessProfile(TEST_PROFILE_ID, testBusinessProfileDto);

        // Assert
        assertNotNull(result);
        verify(businessProfileRepository, times(1)).findById(TEST_PROFILE_ID);
        verify(businessProfileRepository, times(1)).save(any(BusinessProfileEntity.class));
    }

    @Test
    void addSupportingImages_WhenProfileExists_ShouldAddImage() throws Exception {
        // Arrange
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getOriginalFilename()).thenReturn("test-image.jpg");

        when(businessProfileRepository.findById(TEST_PROFILE_ID))
                .thenReturn(Optional.of(testBusinessProfileEntity));
        doNothing().when(uploadService).uploadFile(mockFile);
        when(imageRepository.saveAndFlush(any(SupportingImages.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        SupportingImages result = businessProfileService.addSupportingImages(TEST_PROFILE_ID, mockFile);

        // Assert
        assertNotNull(result);
        assertEquals("test-image.jpg", result.getImageFileName());
        assertEquals("/jobxstore/test-image.jpg", result.getImageUrl());
        assertNotNull(result.getUploadedDate());
        assertEquals("ImageTag", result.getTag());

        verify(uploadService, times(1)).uploadFile(mockFile);
        verify(imageRepository, times(1)).saveAndFlush(any(SupportingImages.class));
        verify(businessProfileRepository, times(1)).save(testBusinessProfileEntity);
    }

    @Test
    void addSupportingImages_WhenProfileNotExists_ShouldLogWarning() {
        // Arrange
        MultipartFile mockFile = mock(MultipartFile.class);
        when(businessProfileRepository.findById(TEST_PROFILE_ID)).thenReturn(Optional.empty());

        // Act
        SupportingImages result = businessProfileService.addSupportingImages(TEST_PROFILE_ID, mockFile);

        // Assert
        assertNotNull(result); // Returns empty SupportingImages object
        verify(businessProfileRepository, times(1)).findById(TEST_PROFILE_ID);
        verify(uploadService, never()).uploadFile(any(MultipartFile.class));
        verify(imageRepository, never()).saveAndFlush(any(SupportingImages.class));
    }

    @Test
    void deactivateBusinessProfile_WhenProfileExistsAndActive_ShouldDeactivate() {
        // Arrange
        when(businessProfileRepository.findById(TEST_PROFILE_ID))
                .thenReturn(Optional.of(testBusinessProfileEntity));
        when(businessProfileRepository.save(any(BusinessProfileEntity.class)))
                .thenReturn(testBusinessProfileEntity);

        // Act
        ResponseMessage result = businessProfileService.deactivateBusinessProfile(TEST_PROFILE_ID);

        // Assert
        assertNotNull(result);
        assertTrue(result.getMessage().contains("Deactivated successfully"));
        assertFalse(testBusinessProfileEntity.isShowProfile());
        assertEquals(ProfileStatus.DEACTIVATED, testBusinessProfileEntity.getProfileStatus());

        verify(businessProfileRepository, times(1)).findById(TEST_PROFILE_ID);
        verify(businessProfileRepository, times(1)).save(testBusinessProfileEntity);
    }

    @Test
    void deactivateBusinessProfile_WhenProfileNotExists_ShouldReturnErrorMessage() {
        // Arrange
        when(businessProfileRepository.findById(TEST_PROFILE_ID)).thenReturn(Optional.empty());

        // Act
        ResponseMessage result = businessProfileService.deactivateBusinessProfile(TEST_PROFILE_ID);

        // Assert
        assertNotNull(result);
        assertTrue(result.getMessage().contains("Already Deactivated or Suspended"));
        verify(businessProfileRepository, times(1)).findById(TEST_PROFILE_ID);
        verify(businessProfileRepository, never()).save(any(BusinessProfileEntity.class));
    }
}