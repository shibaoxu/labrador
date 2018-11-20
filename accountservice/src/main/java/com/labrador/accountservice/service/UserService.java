package com.labrador.accountservice.service;

import com.labrador.accountservice.entity.Role;
import com.labrador.accountservice.entity.User;
import com.labrador.accountservice.repository.RoleRepository;
import com.labrador.accountservice.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;


@Service
@Slf4j
@Transactional(readOnly = true)
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

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

    @Transactional(readOnly = false)
    public User create(User newUser){
        return userRepository.save(newUser);
    }

    @Transactional(readOnly = false)
    public Optional<User> update(User modifiedUser){
        return userRepository.update(modifiedUser);
    }

    @Transactional(readOnly = false)
    public void delete(String id){

    }

    public void disable(String id){

    }

    @Transactional(readOnly = false)
    public void assignToRole(String userId, String... roleIds){
        try {
            User user = userRepository.getOne(userId);
            List<Role> roles = roleRepository.findAllByIdIn(roleIds);
            user.getRoles().addAll(roles);
            userRepository.save(user);
        } catch (EntityNotFoundException ex){
            throw new com.labrador.accountservice.exception.EntityNotFoundException(User.class.getName(), userId);
        }
    }

    public void removeFormRoles(String userId, String roleId){

    }

    public void changePassword(String oldPassword, String newPassword){

    }
}
