package com.labrador.accountservice.service;

import com.labrador.accountservice.entity.Role;
import com.labrador.accountservice.entity.User;
import com.labrador.commons.entity.validation.NewEntityValidationGroup;
import com.labrador.commons.entity.validation.UpdateEntityValidationGroup;
import com.labrador.commontests.BaseTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Collection;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

public class UserServiceTests extends BaseTest {

    @Autowired
    private UserService userService;

    @Autowired
    private Validator validator;

    @Autowired
    private ResourceBundleMessageSource messageSource;

    private static Pageable UNSORTED = PageRequest.of(0, 10);
    private static Pageable SORTED_USERNMAE_DESC = PageRequest.of(0, 10, new Sort(Sort.Direction.DESC, "username"));
    private static Pageable SORTED_USERNAME_ASC = PageRequest.of(0, 10, new Sort(Sort.Direction.ASC, "username"));;

    @Test
    void test_find_all_not_sorted(){
        Page<User> users = userService.findAll(UNSORTED);
        assertThat(users.getTotalElements()).isEqualTo(3);
        assertThat(users.getTotalPages()).isEqualTo(1);
        assertThat(users.getSize()).isEqualTo(10);
        assertThat(users.getNumberOfElements()).isEqualTo(3);

        // 测试EntityGraph
        Collection<Role> roles = users.getContent().get(0).getRoles();
        assertThat(roles).extracting(Role::getName).isNotEmpty();

        assertThatThrownBy(() -> {
            ((Role)roles.toArray()[0]).getUsers().isEmpty();
        }).isInstanceOf(org.hibernate.LazyInitializationException.class);
    }

    @Test
    void test_find_all_and_sorted(){
        Page<User> page = userService.findAll(SORTED_USERNMAE_DESC);
        assertThat(page.getContent()).extracting("username").containsExactly("user", "org:user", "admin");

        page = userService.findAll(SORTED_USERNAME_ASC);
        assertThat(page.getContent()).extracting("username").containsExactly("admin", "org:user", "user");
    }

    @Test
    void test_find_all_by_condition(){
        // unsort
        Page<User> page = userService.findAll("use", UNSORTED);
        assertThat(page.getContent()).extracting("username").containsExactlyInAnyOrder("user", "org:user");

        //sort
        page = userService.findAll("use", SORTED_USERNAME_ASC);
        assertThat(page.getContent()).extracting("username").containsExactly("org:user", "user");

        //not found
        page = userService.findAll("notfound", UNSORTED);
        assertThat(page.getContent()).isEmpty();

        // empty condition
        page = userService.findAll(" ", UNSORTED);
        assertThat(page.getContent()).hasSize(3);
    }

    @Test
    void test_find_by_id(){
//        String id = "297eaf7d508ebfe001508ebfefd20000";
//        Optional<User> user = userService.get(id);
//        assertThat(user.get()).isNotNull();
//
//        user = userService.get("Not Exists");
//        assertThat(user.isPresent()).isFalse();
    }

    @Test
    void test_new_user_validate(){
        Locale.setDefault(Locale.ENGLISH);

        User user = new User();
        Set<ConstraintViolation<User>> violations = validator.validate(user, NewEntityValidationGroup.class);
        assertThat(
                violations.stream().map(it -> tuple(it.getPropertyPath().toString(), it.getMessageTemplate(), it.getMessage()))
                ).containsExactlyInAnyOrder(
                    tuple("plainPassword", "password is too weak", "password is too weak"),
                    tuple("password","{javax.validation.constraints.NotBlank.message}","must not be blank"),
                    tuple("username", "{javax.validation.constraints.NotBlank.message}", "must not be blank"),
                    tuple("displayName", "{javax.validation.constraints.NotBlank.message}", "must not be blank")
                    );

        user.setId("123");
        violations = validator.validate(user, NewEntityValidationGroup.class);
        assertThat(
                violations.stream().map(it -> tuple(it.getPropertyPath().toString(), it.getMessageTemplate(), it.getMessage()))
        ).containsExactlyInAnyOrder(
                tuple("id", "must be null or empty string", "must be null or empty string"),
                tuple("plainPassword", "password is too weak", "password is too weak"),
                tuple("password","{javax.validation.constraints.NotBlank.message}","must not be blank"),
                tuple("username", "{javax.validation.constraints.NotBlank.message}", "must not be blank"),
                tuple("displayName", "{javax.validation.constraints.NotBlank.message}", "must not be blank")
        );
    }

    @Test
    void test_update_user_validate(){
        Locale.setDefault(Locale.ENGLISH);
        User user = new User();
        Set<ConstraintViolation<User>> violations = validator.validate(user, UpdateEntityValidationGroup.class);
        assertThat(
                violations.stream().map(it -> tuple(it.getPropertyPath().toString(), it.getMessageTemplate(), it.getMessage()))
        ).containsExactlyInAnyOrder(
                tuple("id", "{javax.validation.constraints.NotBlank.message}", "must not be blank"),
                tuple("username", "{javax.validation.constraints.NotBlank.message}", "must not be blank"),
                tuple("displayName", "{javax.validation.constraints.NotBlank.message}", "must not be blank")
        );

    }

    @Test
    void test_create_password(){
        PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
//        passwordEncoder.
        System.out.println(passwordEncoder.encode("Abc123**"));
    }
}
