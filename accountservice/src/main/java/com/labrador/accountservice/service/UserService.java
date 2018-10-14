package com.labrador.accountservice.service;

import com.labrador.accountservice.entity.User;
import com.labrador.accountservice.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserService {
    @Autowired
    private UserRepository userRepository;
    public Page<User> findAll(Pageable pageable){
        return userRepository.findAll(pageable);
    }
}
