package com.labrador.accountservice.service;

import com.labrador.accountservice.entity.User;
import com.labrador.accountservice.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Optional;


@Service
@Slf4j
@Transactional(readOnly = true)
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public Page<User> findAll(@NonNull Pageable pageable){
        return userRepository.findAll(pageable);
    }

    public Page<User> findAll(String condition, Pageable pageable){
        if (!StringUtils.hasText(condition)) {
            return findAll(pageable);
        }else{
            return userRepository.findAll(condition, pageable);
        }
    }

    public Optional<User> find(String id){
        return userRepository.findById(id);
    }

    public void add(User newUser){

    }

    public void update(User modifiedUser){

    }

    public void delete(String id){

    }

    public void disable(String id){

    }

    public void assignUserToRole(String userId, String roleId){

    }

    public void removeUserFormRole(String userId, String roleId){

    }

    public void changePassword(String oldPassword, String newPassword){

    }
}
