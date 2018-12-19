package com.labrador.accountservice.service;

import com.labrador.accountservice.entity.Role;
import com.labrador.accountservice.entity.User;
import com.labrador.commons.exception.BusinessException;
import com.labrador.commons.exception.ResourceNotFoundException;
import com.labrador.accountservice.repository.RoleRepository;
import com.labrador.accountservice.repository.UserRepository;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    public Page<User> findAll(@NonNull Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public Page<User> findAll(String criteria, @NonNull Pageable pageable) {
        if (!StringUtils.hasText(criteria)) {
            return findAll(pageable);
        } else {
            return userRepository.findAll(criteria, pageable);
        }
    }

    public User get(@NonNull String id) {
        return userRepository.findById(id).orElseThrow(
                () -> {
                    log.warn("getting an nonexistent user with id:{}", id);
                    return new ResourceNotFoundException(User.class.getName(), "id=" + id);
                }
        );
    }

    public User getByUsername(@NonNull String username){
        return userRepository.findByUsername(username).orElseThrow(
                () -> new ResourceNotFoundException(User.class.getName(), "username=" + username)
        );
    }

    @Transactional(readOnly = false)
    public User create(@NonNull User newUser) {
        return userRepository.save(newUser);
    }

    @Transactional(readOnly = false)
    public Optional<User> update(@NonNull User modifiedUser) {
        return userRepository.update(modifiedUser);
    }

    @Transactional(readOnly = false)
    public void delete(@NonNull String id) {
        try{
            userRepository.delete(userRepository.getOne(id));
        } catch (EntityNotFoundException ex){
            log.warn("deleting a nonexistent user");
        }
    }

    @Transactional(readOnly = false)
    public void disableOrEnable(String id, boolean isEnable) {
        try{
            User user = userRepository.getOne(id);
            user.setEnabled(isEnable);
            userRepository.save(user);
        }catch (EntityNotFoundException ex){
            log.warn("disable/enable a nonexistent user");
        }
    }

    @Transactional(readOnly = false)
    public void assignToRoles(String userId, String... roleIds) {
        try {
            User user = userRepository.getOne(userId);
            List<Role> roles = roleRepository.findAllByIdIn(roleIds);
            user.getRoles().addAll(roles);
            userRepository.save(user);
        } catch (EntityNotFoundException ex) {
            log.warn("unable get the user with id {}", userId);
            throw new com.labrador.commons.exception.EntityNotFoundException(User.class.getName(), userId);
        }
    }

    @Transactional(readOnly = false)
    public void removeFromRoles(@NonNull String userId, @NonNull String... roleIds) {
        try {
            User user = userRepository.getOne(userId);
            List<Role> roles = roleRepository.findAllByIdIn(roleIds);
            user.getRoles().removeAll(roles);
            userRepository.save(user);
        } catch (EntityNotFoundException ex) {
            log.warn("unable get the user with id {}", userId);
            throw new com.labrador.commons.exception.EntityNotFoundException(User.class.getName(), userId);
        }
    }

    @Transactional(readOnly = false)
    public void changePassword(@NonNull String userId, @NonNull String oldPassword, @NonNull String newPassword) {
        try {
            User user = userRepository.getOne(userId);
            if (passwordEncoder.matches(oldPassword, user.getPassword())) {
                userRepository.changePassword(userId, passwordEncoder.encode(newPassword));
            } else {
                log.warn("the old password is incorrect");
                throw new BusinessException("user.password.notmatch");
            }
        } catch (EntityNotFoundException ex) {
            log.warn("unable get the user with id {}", userId);
            throw new com.labrador.commons.exception.EntityNotFoundException(User.class.getName(), userId);
        }
    }
}
