package com.buxz.dev.combuxzjobxservice.service;

import com.buxz.dev.combuxzjobxservice.domain.UserAccountDto;
import com.buxz.dev.combuxzjobxservice.entity.UserAccountEntity;
import com.buxz.dev.combuxzjobxservice.entity.UserAccountFlatEntity;
import com.buxz.dev.combuxzjobxservice.entity.UserProfileEntity;
import com.buxz.dev.combuxzjobxservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserAccountServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserAccountService userAccountService;

    private UserAccountDto accountDto;
    private UserAccountEntity userAccount;

    @BeforeEach
    void setUp() {
        accountDto = new UserAccountDto();
        accountDto.setEmail("alice@example.com");
        accountDto.setFirstName("Alice");
        accountDto.setLastName("Smith");
        accountDto.setCellNumber("12345");

        userAccount = new UserAccountEntity();
        userAccount.setId(1);
        userAccount.setEmail("john@example.com");
        userAccount.setFirstName("John");
        userAccount.setLastName("Doe");
        userAccount.setCellNumber("555");
        userAccount.setUserName("JOHDOE0001");
        userAccount.setDateCreated(LocalDateTime.now());
        userAccount.setUserProfiles(new ArrayList<>());
    }

    @Test
    void createNewUserAccount_ShouldPopulateAndSaveEntity() {
        ArgumentCaptor<UserAccountEntity> captor = ArgumentCaptor.forClass(UserAccountEntity.class);

        UserAccountEntity result = userAccountService.createNewUserAccount(accountDto);

        verify(userRepository).save(captor.capture());
        UserAccountEntity savedEntity = captor.getValue();

        assertEquals(accountDto.getEmail(), result.getEmail());
        assertEquals(accountDto.getFirstName(), result.getFirstName());
        assertEquals(accountDto.getLastName(), result.getLastName());
        assertEquals(accountDto.getCellNumber(), result.getCellNumber());
        assertNotNull(result.getDateCreated());
        assertEquals(10, result.getUserName().length());
        assertTrue(result.getUserName().startsWith("ALISMI"));
        assertEquals(savedEntity, result);
    }

    @Test
    void getUserAccountById_ShouldReturnRepositoryValue() {
        when(userRepository.findById(1)).thenReturn(Optional.of(userAccount));

        Optional<UserAccountEntity> result = userAccountService.getUserAccountById(1);

        assertTrue(result.isPresent());
        assertEquals(userAccount, result.get());
        verify(userRepository).findById(1);
    }

    @Test
    void getAllUserAccount_ShouldReturnAll() {
        when(userRepository.findAll()).thenReturn(List.of(userAccount));

        List<UserAccountEntity> result = userAccountService.getAllUserAccount();

        assertEquals(1, result.size());
        verify(userRepository).findAll();
    }

    @Test
    void getAllUserAccountWithProfiles_ShouldFilterAccountsWithoutProfiles() {
        UserAccountEntity withProfile = new UserAccountEntity();
        withProfile.setId(2);
        withProfile.setEmail("with@profile.com");
        withProfile.setFirstName("With");
        withProfile.setLastName("Profile");
        withProfile.setDateCreated(LocalDateTime.now());
        withProfile.setUserProfiles(new ArrayList<>());
        UserProfileEntity profile = new UserProfileEntity();
        profile.setId(10);
        profile.setJobTitle("Engineer");
        profile.setProfileSummary("Summary");
        profile.setDateOfBirth(LocalDate.of(1990, 1, 1));
        profile.setCity("City");
        profile.setEducationHistory(new ArrayList<>());
        profile.setTestimonialEntity(new ArrayList<>());
        profile.setWorkExperienceEntity(new ArrayList<>());
        withProfile.getUserProfiles().add(profile);

        when(userRepository.findAll()).thenReturn(List.of(userAccount, withProfile));

        List<UserAccountEntity> result = userAccountService.getAllUserAccountWithProfiles();

        assertEquals(1, result.size());
        assertEquals(withProfile, result.get(0));
        verify(userRepository).findAll();
    }

    @Test
    void getAllUserAccountByName_ShouldDelegateToRepository() {
        when(userRepository.getAllByFirstNameContaining("Ali")).thenReturn(List.of(userAccount));

        List<UserAccountEntity> result = userAccountService.getAllUserAccountByName("Ali");

        assertEquals(1, result.size());
        verify(userRepository).getAllByFirstNameContaining("Ali");
    }

    @Test
    void updateUserAccount_ShouldUpdateFieldsAndSave() {
        when(userRepository.getById(1)).thenReturn(userAccount);
        when(userRepository.save(any(UserAccountEntity.class))).thenReturn(userAccount);

        UserAccountEntity result = userAccountService.updateUserAccount(1, accountDto);

        assertEquals(accountDto.getEmail(), result.getEmail());
        assertEquals(accountDto.getCellNumber(), result.getCellNumber());
        assertEquals(accountDto.getLastName(), result.getLastName());
        verify(userRepository).save(userAccount);
    }

    @Test
    void getListOfAllUserProfileInFlatJson_ShouldConvertAccounts() {
        UserAccountEntity account = new UserAccountEntity();
        account.setId(5);
        account.setEmail("flat@example.com");
        account.setFirstName("Flat");
        account.setLastName("User");
        account.setCellNumber("789");
        account.setUserName("FLAUSE1234");
        account.setDateCreated(LocalDateTime.of(2020, 1, 1, 10, 0));
        account.setUserProfiles(new ArrayList<>());

        UserProfileEntity profile = new UserProfileEntity();
        profile.setId(9);
        profile.setJobTitle("Designer");
        profile.setProfileSummary("Creative");
        profile.setDateOfBirth(LocalDate.of(1995, 5, 5));
        profile.setCity("Pretoria");
        profile.setEducationHistory(new ArrayList<>());
        profile.setWorkExperienceEntity(new ArrayList<>());
        profile.setTestimonialEntity(new ArrayList<>());
        account.getUserProfiles().add(profile);

        when(userRepository.findAll()).thenReturn(List.of(account));

        List<UserAccountFlatEntity> result = userAccountService.getListOfAllUserProfileInFlatJson();

        assertEquals(1, result.size());
        UserAccountFlatEntity flat = result.get(0);
        assertEquals(account.getId(), flat.getId());
        assertEquals(profile.getJobTitle(), flat.getJobTitle());
        assertEquals(profile.getProfileSummary(), flat.getProfileSummary());
        assertEquals(profile.getEducationHistory(), flat.getEducationHistory());
    }

    @Test
    void getUserAccountByIdToFlat_WhenAccountExists_ShouldReturnFlatOptional() {
        UserAccountEntity account = new UserAccountEntity();
        account.setId(7);
        account.setEmail("idflat@example.com");
        account.setFirstName("Id");
        account.setLastName("Flat");
        account.setCellNumber("987");
        account.setUserName("IDFLAT1234");
        account.setDateCreated(LocalDateTime.now());
        account.setUserProfiles(new ArrayList<>());
        UserProfileEntity profile = new UserProfileEntity();
        profile.setId(3);
        profile.setJobTitle("Writer");
        profile.setProfileSummary("Writes");
        profile.setDateOfBirth(LocalDate.of(1990, 2, 2));
        profile.setCity("Durban");
        profile.setEducationHistory(new ArrayList<>());
        profile.setWorkExperienceEntity(new ArrayList<>());
        profile.setTestimonialEntity(new ArrayList<>());
        account.getUserProfiles().add(profile);

        when(userRepository.findById(7)).thenReturn(Optional.of(account));

        Optional<UserAccountFlatEntity> result = userAccountService.getUserAccountByIdToFlat(7);

        assertTrue(result.isPresent());
        assertEquals("Writer", result.get().getJobTitle());
    }

    @Test
    void getUserAccountByIdToFlat_WhenAccountMissing_ShouldReturnEmptyOptional() {
        when(userRepository.findById(9)).thenReturn(Optional.empty());

        Optional<UserAccountFlatEntity> result = userAccountService.getUserAccountByIdToFlat(9);

        assertTrue(result.isEmpty());
    }

    @Test
    void deleteUserAccountById_ShouldDelegateToRepository() {
        userAccountService.deleteUserAccountById(11);

        verify(userRepository).deleteById(11);
    }
}
