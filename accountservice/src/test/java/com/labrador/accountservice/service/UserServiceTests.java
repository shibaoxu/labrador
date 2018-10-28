package com.labrador.accountservice.service;

import com.labrador.accountservice.entity.Role;
import com.labrador.accountservice.entity.User;
import com.labrador.accountservice.service.UserService;
import com.labrador.accountservice.service.UserValidator;
import com.labrador.commontests.BaseTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class UserServiceTests extends BaseTest {

    @Autowired
    private UserService userService;

    @Autowired
    private ResourceBundleMessageSource messageSource;

    @Autowired
    private UserValidator userValidator;

    @Autowired
    private UserValidator validator;

    private static Pageable UNSORTED = PageRequest.of(0, 10);
    private static Pageable SORTED_USERNMAE_DESC = PageRequest.of(0, 10, new Sort(Sort.Direction.DESC, "username"));
    private static Pageable SORTED_USERNAME_ASC = PageRequest.of(0, 10, new Sort(Sort.Direction.ASC, "username"));;

    @Test
    void testFindAllNotSort(){
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
    void testFindAllSort(){
        Page<User> page = userService.findAll(SORTED_USERNMAE_DESC);
        assertThat(page.getContent()).extracting("username").containsExactly("user", "org:user", "admin");

        page = userService.findAll(SORTED_USERNAME_ASC);
        assertThat(page.getContent()).extracting("username").containsExactly("admin", "org:user", "user");
    }

    @Test
    void testFindAllByCondition(){
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
    void testFindById(){
        String id = "297eaf7d508ebfe001508ebfefd20000";
        Optional<User> user = userService.find(id);
        assertThat(user.get()).isNotNull();

        user = userService.find("Not Exists");
        assertThat(user.isPresent()).isFalse();
    }

    @Test
    void testValidate(){
        User user = new User();
//        defaultValidator.setValidationMessageSource(messageSource);
//        user.setUsername("ad");
//        user.setDisplayName("管理员");
//        user.setPlainPassword("password");
        Errors errors = new BeanPropertyBindingResult(user, "user");
//        userValidator.validate(user, errors);
        validator.validate(user, errors);
//        assertThat(errors.getErrorCount()).isEqualTo(4);
        List<String> errorMessages = errors.getAllErrors().stream().map(
                error -> messageSource.getMessage(error.getCode(), error.getArguments(), Locale.CHINA)
        ).collect(Collectors.toList());
        System.out.println("");
    }



}
