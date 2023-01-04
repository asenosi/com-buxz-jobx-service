package com.buxz.dev.combuxzjobxservice.service;

import com.buxz.dev.combuxzjobxservice.domain.UserAccountDto;
import com.buxz.dev.combuxzjobxservice.entity.UserAccountEntity;
import com.buxz.dev.combuxzjobxservice.entity.UserAccountFlatEntity;
import com.buxz.dev.combuxzjobxservice.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserAccountService {

    private final UserRepository userRepository;

    @Autowired
    public UserAccountService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserAccountEntity createNewUserAccount(UserAccountDto accountDto) {
        UserAccountEntity userAccount = new UserAccountEntity();
        userAccount.setEmail(accountDto.getEmail());
        userAccount.setFirstName(accountDto.getFirstName());
        userAccount.setLastName(accountDto.getLastName());
        userAccount.setCellNumber(accountDto.getCellNumber());
        userAccount.setDateCreated(LocalDateTime.now());
        userAccount.setUserName(createUserName(accountDto));
        log.info("CreatedNewUserAccount : Username {} created successfully", userAccount.getUserName());
        userRepository.save(userAccount);
        return userAccount;
    }

    public Optional<UserAccountEntity> getUserAccountById(int id) {
        log.info("GetUserAccountsById : Attempting to retrieve UserAccount by Id: {}", id);
        return userRepository.findById(id);
    }

    public List<UserAccountEntity> getAllUserAccount() {
        return userRepository.findAll();
    }

    public List<UserAccountEntity> getAllUserAccountWithProfiles() {
        log.info("GetUserAccountsWithProfile : Attempting to retrieve all UserAccounts with userProfiles");
        return userRepository.findAll()
                .parallelStream()
                .filter(userAccountEntity -> !userAccountEntity.getUserProfiles().isEmpty())
                .collect(Collectors.toList());
    }

    public List<UserAccountEntity> getAllUserAccountByName(String name) {
        log.info("GetUserAccountsByName : Attempting to retrieve UserAccounts containing -- {}", name);
        return userRepository.getAllByFirstNameContaining(name);
    }

    public UserAccountEntity updateUserAccount(int id, UserAccountDto userAccountDto) {
        UserAccountEntity accountToUpdate = userRepository.getById(id);
        accountToUpdate.setEmail(userAccountDto.getEmail());
        accountToUpdate.setCellNumber(userAccountDto.getCellNumber());
        accountToUpdate.setLastName(userAccountDto.getLastName());
        accountToUpdate.setFirstName(userAccountDto.getFirstName());
        userRepository.save(accountToUpdate);
        log.info("UpdateUserAccount : Record for Username {} updated successfully", accountToUpdate.getUserName());
        return accountToUpdate;
    }

    public List<UserAccountFlatEntity> getListOfAllUserProfileInFlatJson() {
        return userRepository.findAll()
                .parallelStream()
                .filter(userAccountEntity -> !userAccountEntity.getUserProfiles().isEmpty())
                .map(this::convertUserAccountToFlatJson)
                .collect(Collectors.toList());
    }

    private UserAccountFlatEntity convertUserAccountToFlatJson(UserAccountEntity userAccountEntity) {
        return new UserAccountFlatEntity().builder()
                .id(userAccountEntity.getId())
                .email(userAccountEntity.getEmail())
                .firstName(userAccountEntity.getFirstName())
                .lastName(userAccountEntity.getLastName())
                .cellNumber(userAccountEntity.getCellNumber())
                .userName(userAccountEntity.getUserName())
                .dateCreated(userAccountEntity.getDateCreated().toString())
                .jobTitle(userAccountEntity.getUserProfiles().get(0).getJobTitle())
                .profileSummary(userAccountEntity.getUserProfiles().get(0).getProfileSummary())
                .dateOfBirth(userAccountEntity.getUserProfiles().get(0).getDateOfBirth().toString())
                .city(userAccountEntity.getUserProfiles().get(0).getCity())
                .educationHistory(userAccountEntity.getUserProfiles().get(0).getEducationHistory())
//                .whatsappNumber(userAccountEntity.getUserProfiles().get(0).getContactDetails().getWhatsappNumber())
//                .linkedInLink(userAccountEntity.getUserProfiles().get(0).getContactDetails().getLinkedInLink())
//                .facebookLink(userAccountEntity.getUserProfiles().get(0).getContactDetails().getFacebookLink())
                .build();
    }

    public Optional<UserAccountFlatEntity> getUserAccountByIdToFlat(int id) {
        Optional<UserAccountEntity> userAccountEntity = userRepository.findById(id);
        return userAccountEntity.map(this::convertUserAccountToFlatJson);
    }

    private String createUserName(UserAccountDto accountDto) {
        Random random = new Random();
        String nameCut = accountDto.getFirstName().toUpperCase();
        String surNameCut = accountDto.getLastName().toUpperCase();
        String randomized = String.valueOf(1000 + random.nextInt(9999));
        return nameCut.substring(0,3).concat(surNameCut.substring(0,3)).concat(randomized);
    }
}
