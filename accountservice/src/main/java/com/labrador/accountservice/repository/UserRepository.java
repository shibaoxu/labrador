package com.labrador.accountservice.repository;

import com.labrador.accountservice.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    @EntityGraph(attributePaths = {"roles"})
    Page<User> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"roles"})
    Page<User> findAllByUsernameContainingOrDisplayNameContaining(String username, String displayName, Pageable pageable);

    @EntityGraph(attributePaths = {"roles"})
    Optional<User> findByUsername(String username);

    @EntityGraph(attributePaths = {"roles"})
    default Page<User> findAll(String criteria, Pageable pageable) {
        if (!StringUtils.hasText(criteria)) {
            return findAll(pageable);
        } else {
            return findAllByUsernameContainingOrDisplayNameContaining(criteria, criteria, pageable);
        }
    }

    @EntityGraph(attributePaths = {"roles"})
    default Optional<User> update(User user) {
        try {
            User originnUser = getOne(user.getId());
            originnUser.setDisplayName(user.getDisplayName());
            originnUser.setEnabled(user.isEnabled());
            return Optional.of(save(originnUser));
        } catch (EntityNotFoundException ex) {
            Logger logger = LoggerFactory.getLogger(UserRepository.class.getName());
            logger.warn("try update a nonexist user with id {}", user.getId());
            throw new com.labrador.commons.exception.EntityNotFoundException(user.getClass().getName(), user.getId());
        }
    }

    @Modifying
    @Query("update User set password = :password where id = :id")
    int changePassword(
            @Param("id") String userId,
            @Param("password") String password);
}
