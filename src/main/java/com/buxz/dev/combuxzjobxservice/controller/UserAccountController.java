package com.buxz.dev.combuxzjobxservice.controller;

import com.buxz.dev.combuxzjobxservice.domain.UserAccountDto;
import com.buxz.dev.combuxzjobxservice.entity.UserAccountEntity;
import com.buxz.dev.combuxzjobxservice.entity.UserAccountFlatEntity;
import com.buxz.dev.combuxzjobxservice.service.UserAccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/v1/jobx/users")
public class UserAccountController {

    @Autowired
    private UserAccountService userService;

    @PostMapping
    public ResponseEntity<UserAccountEntity> createUserAccount(@RequestBody UserAccountDto userAccountDto) {
        log.info("Request to create user account received, {} {} {} {}", userAccountDto.getEmail(), userAccountDto.getCellNumber(), userAccountDto.getFirstName(), userAccountDto.getLastName());
        try {
            return new ResponseEntity<>(userService.createNewUserAccount(userAccountDto), HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("CreateUserAccount: Failed to create a user account : {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<List<UserAccountEntity>> getListOfUsers(@RequestParam(required = false) String name) {
        try {
            log.debug("Request to list all Users");
            List<UserAccountEntity> userAccountList;

            if(name == null) {
                log.info("Request: Get all Existing Users");
                userAccountList = userService.getAllUserAccountWithProfiles();
            } else {
                log.info("Request: Get all Users with name containing : {}", name);
                userAccountList = userService.getAllUserAccountByName(name);
            }

            if(userAccountList.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(userAccountList, HttpStatus.OK);
        } catch (Exception e) {
            log.error("GetListOfUsers: Failed to retrieve list of user account : {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserAccountEntity> getUserById(@PathVariable("id") int id) {
        Optional<UserAccountEntity> task = userService.getUserAccountById(id);
        if(task.isPresent()) {
            log.info("Users exists with id : {}", id);
            return new ResponseEntity<>(task.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserAccountEntity> updateUserAccount(@PathVariable("id") int id, @RequestBody UserAccountDto userAccountDto) {
        Optional<UserAccountEntity> userAccount = userService.getUserAccountById(id);
        if(userAccount.isPresent()) {
            return new ResponseEntity<>(userService.updateUserAccount(id, userAccountDto), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteUserAccount(@PathVariable("id") int id) {
        userService.deleteUserAccountById(id);
        return new ResponseEntity<>(HttpStatus.GONE);
    }

    @GetMapping("/flat")
    public ResponseEntity<List<UserAccountFlatEntity>> getListOfAllUserInFlatJson() {
        try {
            log.debug("Request to list all Users in flat form");
            List<UserAccountFlatEntity> userAccountList = userService.getListOfAllUserProfileInFlatJson();

            if(userAccountList.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(userAccountList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/flat/{id}")
    public ResponseEntity<UserAccountFlatEntity> getUserByIdToFlat(@PathVariable("id") int id) {
        Optional<UserAccountFlatEntity> userAccountFlat = userService.getUserAccountByIdToFlat(id);
        if(userAccountFlat.isPresent()) {
            log.info("Users exists with id : {}", id);
            return new ResponseEntity<>(userAccountFlat.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
