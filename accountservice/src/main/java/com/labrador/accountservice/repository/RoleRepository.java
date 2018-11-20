package com.labrador.accountservice.repository;

import com.labrador.accountservice.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoleRepository extends JpaRepository<Role, String> {
    List<Role> findAllByIdIn(String... id);
}
