package com.labrador.accountservice.api;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;
import com.labrador.accountservice.entity.User;
import com.labrador.accountservice.utils.MockMvcTestUtils;
import com.labrador.commontests.SpringProfileActive;
import net.minidev.json.JSONArray;
import org.assertj.core.groups.Tuple;
import org.assertj.core.util.Strings;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import static com.labrador.accountservice.utils.LabradorAssertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ActiveProfiles(resolver = SpringProfileActive.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class UserControllerTest {

    private final static String USER_BASE_URL = "/api/users";
    private final static String ID_OF_ROLE_USER = "2d2994219a14476eba13c5036ecda147";
    private final static String NAME_OF_ROLE_USER = "ROLE_USER";
    private final static String ID_OF_ROLE_ADMIN = "f2a26d2090624570b6bb630ab546c98f";
    private final static String NAME_OF_ROLE_ADMIN = "ROLE_ADMIN";
    private final static String ID_OF_ROLE_SALES = "f2a26d2090624570b6bb630ab546c99f";
    private final static String NAME_OF_ROLE_SALES = "ROLE_SALES";
    private final static String ID_OF_USER = "297eaf7d508ebfe001508ebfefd20000";
    private final static String USERNAME_OF_USER = "user";
    private final static String DISPLAY_NAME_OF_USER = "张三";
    private final static boolean ENABLE_OF_USER = true;
    private final static String ID_OF_NONEXISTENT = "ID_OF_NON_EXISTENT";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void test_add_new_user_validate_failure() throws Exception {
        ResultActions result = mockMvc.perform(
                post(USER_BASE_URL).locale(Locale.ENGLISH)
        ).andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));

        String resp = result.andReturn().getResponse().getContentAsString();
        ReadContext context = JsonPath.parse(resp);
        Map<String, Object> errors = context.read("$");
        assertThat(errors)
                .containsKey("timestamp")
                .containsEntry("status", HttpStatus.BAD_REQUEST.value())
                .containsEntry("message", "Validation failed for object=user. Error count: 4")
                .containsKey("path")
                .containsEntry("error", HttpStatus.BAD_REQUEST.getReasonPhrase())
                .containsKey("details");

        List<Map<String, Object>> fieldErrors = context.read("$.details.fieldErrors");

        assertThat(fieldErrors.stream().map(
                it -> tuple(it.get("code"), it.get("message"), it.get("fieldName"), it.get("fieldDescription"))
                )
        ).containsExactlyInAnyOrder(
                tuple("NotBlank", "can not be empty", "username", "user name"),
                tuple("NotBlank", "can not be empty", "displayName", "display name"),
                tuple("PasswordStrength", "password does not meet the requirements of the rule", "plainPassword", "plain password"),
                tuple("NotBlank", "can not be empty", "password", "password")
        );

        List<Object> objectErrors = context.read("$.details.objectErrors");
        assertThat(objectErrors).isEmpty();

        /*==================*/
        result = mockMvc.perform(
                post(USER_BASE_URL)
                        .locale(Locale.ENGLISH)
                        .param("username", "u")
                        .param("displayName", "LongNameLongNameLongNameLongNameLongNameLongNameLongName")
                        .param("plainPassword", "Abc123**")
                        .param("enabled", "false")
        ).andExpect(status().isBadRequest()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));

        context = JsonPath.parse(result.andReturn().getResponse().getContentAsString());
        errors = context.read("$");
        assertThat(errors)
                .containsKey("timestamp")
                .containsEntry("status", HttpStatus.BAD_REQUEST.value())
                .containsEntry("message", "Validation failed for object=user. Error count: 3")
                .containsKey("path")
                .containsEntry("error", HttpStatus.BAD_REQUEST.getReasonPhrase())
                .containsKey("details");
        fieldErrors = context.read("$.details.fieldErrors");
        assertThat(fieldErrors.stream().map(
                it -> tuple(it.get("code"), it.get("message"), it.get("fieldName"), it.get("fieldDescription"))
                )
        ).containsExactlyInAnyOrder(
                tuple("Length", "length must be between 3 and 50", "username", "user name"),
                tuple("Length", "length must be between 0 and 50", "displayName", "display name"),
                tuple("AssertTrue", "must be true", "enabled", "enabled")
        );

        objectErrors = context.read("$.details.objectErrors");
        assertThat(objectErrors).isEmpty();

        /*==========================*/
        result = mockMvc.perform(
                post(USER_BASE_URL)
                        .locale(Locale.ENGLISH)
                        .param("username", "user")
                        .param("displayName", "displayName")
                        .param("plainPassword", "Abc123**")
                        .param("enabled", "true")
        ).andExpect(status().isBadRequest()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));

        context = JsonPath.parse(result.andReturn().getResponse().getContentAsString());
        errors = context.read("$");
        assertThat(errors)
                .containsKey("timestamp")
                .containsEntry("status", HttpStatus.BAD_REQUEST.value())
                .containsEntry("message", "Validation failed for object=user. Error count: 1")
                .containsKey("path")
                .containsEntry("error", HttpStatus.BAD_REQUEST.getReasonPhrase())
                .containsKey("details");
        fieldErrors = context.read("$.details.fieldErrors");
        assertThat(fieldErrors.stream().map(
                it -> tuple(it.get("code"), it.get("message"), it.get("fieldName"), it.get("fieldDescription"))
                )
        ).containsExactlyInAnyOrder(
                tuple("user.username.unique", "user(user) already exists", "username", "user name")
        );

        objectErrors = context.read("$.details.objectErrors");
        assertThat(objectErrors).isEmpty();
    }

    @Test
    void test_add_new_user_validate_failure_i18n() throws Exception {
        ResultActions result = mockMvc.perform(
                post(USER_BASE_URL).locale(Locale.CHINA)
        ).andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));

        String resp = result.andReturn().getResponse().getContentAsString();
        ReadContext context = JsonPath.parse(resp);
        Map<String, Object> errors = context.read("$");
        assertThat(errors)
                .containsKey("timestamp")
                .containsEntry("status", HttpStatus.BAD_REQUEST.value())
                .containsEntry("message", "对象(user)验证失败，总共发现4项错误")
                .containsKey("path")
                .containsEntry("error", HttpStatus.BAD_REQUEST.getReasonPhrase())
                .containsKey("details");

        List<Map<String, Object>> fieldErrors = context.read("$.details.fieldErrors");

        assertThat(fieldErrors.stream().map(
                it -> tuple(it.get("code"), it.get("message"), it.get("fieldName"), it.get("fieldDescription"))
                )
        ).containsExactlyInAnyOrder(
                tuple("NotBlank", "值不能为空", "username", "登录名"),
                tuple("NotBlank", "值不能为空", "displayName", "姓名"),
                tuple("PasswordStrength", "密码不符合规则要求", "plainPassword", "密码"),
                tuple("NotBlank", "值不能为空", "password", "密码")
        );

        List<Object> objectErrors = context.read("$.details.objectErrors");
        assertThat(objectErrors).isEmpty();

        /*==================*/
        result = mockMvc.perform(
                post(USER_BASE_URL)
                        .locale(Locale.CHINA)
                        .param("username", "u")
                        .param("displayName", "LongNameLongNameLongNameLongNameLongNameLongNameLongName")
                        .param("plainPassword", "Abc123**")
                        .param("enabled", "false")
        ).andExpect(status().isBadRequest()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));

        context = JsonPath.parse(result.andReturn().getResponse().getContentAsString());
        errors = context.read("$");
        assertThat(errors)
                .containsKey("timestamp")
                .containsEntry("status", HttpStatus.BAD_REQUEST.value())
                .containsEntry("message", "对象(user)验证失败，总共发现3项错误")
                .containsKey("path")
                .containsEntry("error", HttpStatus.BAD_REQUEST.getReasonPhrase())
                .containsKey("details");
        fieldErrors = context.read("$.details.fieldErrors");
        assertThat(fieldErrors.stream().map(
                it -> tuple(it.get("code"), it.get("message"), it.get("fieldName"), it.get("fieldDescription"))
                )
        ).containsExactlyInAnyOrder(
                tuple("Length", "长度必须在3和50之间", "username", "登录名"),
                tuple("Length", "长度必须在0和50之间", "displayName", "姓名"),
                tuple("AssertTrue", "必须为真", "enabled", "启用")
        );

        objectErrors = context.read("$.details.objectErrors");
        assertThat(objectErrors).isEmpty();
        /*==========================*/
        result = mockMvc.perform(
                post(USER_BASE_URL)
                        .locale(Locale.CHINA)
                        .param("username", "user")
                        .param("displayName", "displayName")
                        .param("plainPassword", "Abc123**")
                        .param("enabled", "true")
        ).andExpect(status().isBadRequest()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));

        context = JsonPath.parse(result.andReturn().getResponse().getContentAsString());
        errors = context.read("$");
        assertThat(errors)
                .containsKey("timestamp")
                .containsEntry("status", HttpStatus.BAD_REQUEST.value())
                .containsEntry("message", "对象(user)验证失败，总共发现1项错误")
                .containsKey("path")
                .containsEntry("error", HttpStatus.BAD_REQUEST.getReasonPhrase())
                .containsKey("details");
        fieldErrors = context.read("$.details.fieldErrors");
        assertThat(fieldErrors.stream().map(
                it -> tuple(it.get("code"), it.get("message"), it.get("fieldName"), it.get("fieldDescription"))
                )
        ).containsExactlyInAnyOrder(
                tuple("user.username.unique", "用户名(user)已经存在", "username", "登录名")
        );

        objectErrors = context.read("$.details.objectErrors");
        assertThat(objectErrors).isEmpty();
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void test_add_new_user_success() throws Exception {
        ResultActions result = mockMvc.perform(
                post(USER_BASE_URL)
                        .param("username", "newuser")
                        .param("displayName", "新用户")
                        .param("plainPassword", "Abc123**")
                        .locale(Locale.ENGLISH)
        ).andExpect(status().isOk());

        int rows = JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "users", "username='newuser'");
        assertThat(rows).isEqualTo(1);

        Map<String, Object> resp = MockMvcTestUtils.parseResponseToMap(result);
        assertThat(resp)
                .containsOnlyKeys("id", "username", "displayName", "enabled", "roles", "createdDate", "createdBy", "lastModifiedDate", "lastModifiedBy")
                .containsEntry("username", "newuser")
                .containsEntry("displayName", "新用户")
                .containsEntry("createdBy", "anonymousUser")
                .containsEntry("lastModifiedBy", "anonymousUser");
        assertThat(resp.get("id").toString()).isNotBlank();
        assertThat(LocalDateTime.parse(resp.get("createdDate").toString()))
                .isEqualTo(LocalDateTime.parse(resp.get("lastModifiedDate").toString()))
                .isBefore(LocalDateTime.now())
                .isCloseTo(LocalDateTime.now(), within(1, ChronoUnit.SECONDS));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void test_modify_user_success() throws Exception {
        ResultActions result = mockMvc.perform(
                put(USER_BASE_URL)
                        .param("id", ID_OF_USER)
                        .param("username", "modified-username")
                        .param("displayName", "modified-displayname")
                        .param("enabled", "false")
        ).andExpect(status().isOk());

        int rowCount = JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "users", "username='modified-username'");
        assertThat(rowCount).isEqualTo(0);
        rowCount = JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "users", "username='user'");
        assertThat(rowCount).isEqualTo(1);
        rowCount = JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "users",
                "username='user' and display_name='modified-displayname' and enabled=false"
        );
        assertThat(rowCount).isEqualTo(1);

        rowCount = JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "users_roles",
                "users_id='" + ID_OF_USER + "'");
        assertThat(rowCount).isEqualTo(2);

        Map<String, Object> resp = MockMvcTestUtils.parseResponseToMap(result);
        assertThat(resp)
                .containsEntry("id", ID_OF_USER)
                .containsEntry("username", "user")
                .containsEntry("displayName", "modified-displayname")
                .containsEntry("enabled", false)
                .containsEntry("createdBy", "297eaf7d508ebfe001508ebff0aa0001")
                .containsEntry("createdDate", "2018-07-31T15:49:56.985")
                .containsEntry("lastModifiedBy", "anonymousUser")
                .containsKeys("lastModifiedDate");
        assertThat(LocalDateTime.parse(resp.get("lastModifiedDate").toString()))
                .isBefore(LocalDateTime.now())
                .isCloseTo(LocalDateTime.now(), within(1, ChronoUnit.SECONDS));

        JSONArray roles = (JSONArray) resp.get("roles");

        @SuppressWarnings("unchecked")
        List<String> roleNames = roles.stream().map(it -> ((Map<String, Object>) it).get("name").toString()).collect(Collectors.toList());
        assertThat(roleNames).containsExactly("ROLE_USER", "ROLE_SALES");
    }

    @Test
    void test_modify_nonexist_user() throws Exception {
        ResultActions result = mockMvc.perform(
                put(USER_BASE_URL)
                        .param("id", "NonExistId")
                        .param("username", "user")
                        .param("displayName", "displayName")
        ).andExpect(status().isBadRequest());

        Map<String, Object> resp = MockMvcTestUtils.parseResponseToMap(result);
        assertThat(resp)
                .containsEntry("status", HttpStatus.BAD_REQUEST.value())
                .containsEntry("error", HttpStatus.BAD_REQUEST.getReasonPhrase())
                .containsEntry("details", "unable find com.labrador.accountservice.entity.User with id NonExistId")
                .containsEntry("message", "unable find the entity");
    }

    @Test
    @DirtiesContext
    void test_assign_to_roles_success() throws Exception {
        assignToRoles(ID_OF_USER, ID_OF_ROLE_ADMIN, ID_OF_ROLE_SALES).andExpect(status().isOk());
        assertThat(isUserAssignedToRole(ID_OF_USER, ID_OF_ROLE_ADMIN)).isTrue();
        assertThat(isUserAssignedToRole(ID_OF_USER, ID_OF_ROLE_SALES)).isTrue();
    }

    @Test
    @DirtiesContext
    void test_assign_to_exist_role_success() throws Exception {
        assignToRoles(ID_OF_USER, ID_OF_ROLE_ADMIN, ID_OF_ROLE_USER).andExpect(status().isOk());
        assertThat(isUserAssignedToRole(ID_OF_USER, ID_OF_ROLE_ADMIN)).isTrue();
        assertThat(isUserAssignedToRole(ID_OF_USER, ID_OF_ROLE_USER)).isTrue();
    }

    @Test
    void test_assign_to_roles_validate_failure() throws Exception {
        ResultActions actions = assignToRoles(ID_OF_NONEXISTENT, ID_OF_ROLE_SALES).andExpect(status().isBadRequest());
        Map<String, Object> resp = MockMvcTestUtils.parseResponseToMap(actions);
        assertThat(resp)
                .containsEntry("message", "unable find the entity")
                .containsEntry("details", "unable find com.labrador.accountservice.entity.User with id ID_OF_NON_EXISTENT");
    }

    @Test
    void test_assign_to_roles_with_empty_params() throws Exception {
        ResultActions actions = mockMvc.perform(
                put(USER_BASE_URL + "/assignToRoles")
        ).andExpect(status().isBadRequest());
        assertThat(MockMvcTestUtils.parseResponseToMap(actions))
                .containsEntry("status", 400)
                .containsEntry("error", HttpStatus.BAD_REQUEST.getReasonPhrase())
                .containsEntry("message", "Required parameter is not present")
                .containsEntry("httpMethod", "PUT")
                .containsEntry("details", "Required String parameter userId is not present");
    }

    @Test
    void test_assign_to_roles_with_empty_roles() throws Exception {
        ResultActions actions = mockMvc.perform(
                put(USER_BASE_URL + "/assignToRoles")
                        .param("userId", ID_OF_USER)
        ).andExpect(status().isBadRequest());
        assertThat(MockMvcTestUtils.parseResponseToMap(actions))
                .containsEntry("status", 400)
                .containsEntry("error", HttpStatus.BAD_REQUEST.getReasonPhrase())
                .containsEntry("message", "Required parameter is not present")
                .containsEntry("httpMethod", "PUT")
                .containsEntry("details", "Required String[] parameter roleId is not present");
    }

    @Test
    void test_assign_to_roles_with_blank_user_id() throws Exception {
        ResultActions actions = mockMvc.perform(
                put(USER_BASE_URL + "/assignToRoles")
                        .param("userId", "")
                        .param("roleId", "")
        ).andExpect(status().isBadRequest());
        Map<String, Object> resp = MockMvcTestUtils.parseResponseToMap(actions);
        assertThat(resp)
                .containsEntry("message", "request parameter validate failure")
                .containsEntry("status", HttpStatus.BAD_REQUEST.value())
                .containsKey("details");

        List<Map<String, Object>> details = JsonPath.read(actions.andReturn().getResponse().getContentAsString(), "$.details");
        assertThat(extractParameterValidateError(details)).containsExactly(
                tuple(
                        "com.labrador.accountservice.api.UserController", "assignToRoles", "userId", "NotBlank", "can not be empty")
        );
    }

    @Test
    void test_remove_from_roles_success() throws Exception {
        removeFromRoles(ID_OF_USER, ID_OF_ROLE_SALES, ID_OF_ROLE_USER)
                .andExpect(status().isOk());
        int rows = JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "users_roles", String.format("users_id='%s'", ID_OF_USER));
        assertThat(rows).isEqualTo(0);
    }

    @Test
    @DirtiesContext
    void test_change_password_success() throws Exception {
        String oldPassword = "Abc123**";
        String newPassword = "Cba123**";
        changePassword(ID_OF_USER, oldPassword, newPassword)
                .andExpect(status().isOk());
        String password = jdbcTemplate.queryForObject("select password from users where id = ?", String.class, ID_OF_USER);
        assertThat(passwordEncoder.matches(newPassword, password)).isTrue();
    }

    @Test
    void test_change_password_with_not_match_old_password() throws Exception {
        String oldPassword = "Abc123***";
        String newPassword = "Cba123**";
        ResultActions actions = changePassword(ID_OF_USER, oldPassword, newPassword)
                .andExpect(status().isConflict());

        assertThat(MockMvcTestUtils.parseResponseToMap(actions))
                .containsEntry("status", HttpStatus.CONFLICT.value())
                .containsEntry("error", HttpStatus.CONFLICT.getReasonPhrase())
                .containsEntry("message", "the old password is incorrect")
                .containsEntry("details", null);
    }

    @Test
    void test_change_password_with_weak_password() throws Exception {
        String oldPassword = "Abc123***";
        String newPassword = "abc";
        ResultActions actions = changePassword(ID_OF_USER, oldPassword, newPassword)
                .andExpect(status().isBadRequest());
        Map<String, Object> resp = MockMvcTestUtils.parseResponseToMap(actions);
        assertThat(resp)
                .containsEntry("status", HttpStatus.BAD_REQUEST.value())
                .containsEntry("error", HttpStatus.BAD_REQUEST.getReasonPhrase())
                .containsEntry("message", "request parameter validate failure");

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> details = (List<Map<String, Object>>) resp.get("details");
        List<Tuple> paramErrors = details.stream().map(it -> tuple(
                it.get("className"),
                it.get("methodName"),
                it.get("paramName"),
                it.get("messageCode"),
                it.get("message")
        )).collect(Collectors.toList());
        assertThat(paramErrors).containsExactly(
                tuple(
                        "com.labrador.accountservice.api.UserController", "changePassword", "newPassword", "PasswordStrength", "password does not meet the requirements of the rule")
        );
    }

    @Test
    @DirtiesContext
    void test_delete_user_success() throws Exception {
        deleteUser(ID_OF_USER).andExpect(status().isOk());
        int rows = JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "users", String.format("id='%s'", ID_OF_USER));
        assertThat(rows).isEqualTo(0);
        rows = JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "users_roles", String.format("users_id = '%s'", ID_OF_USER));
        assertThat(rows).isEqualTo(0);
    }

    @Test
    @DirtiesContext
    void test_enable_user_success() throws Exception {
        mockMvc.perform(
                put(String.format("%s/%s/enable", USER_BASE_URL, ID_OF_USER))
        ).andExpect(status().isOk());

        int rows = JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "users",
                String.format("id='%s' and enabled = %b", ID_OF_USER, true));
        assertThat(rows).isEqualTo(1);
    }

    @Test
    @DirtiesContext
    void test_disable_user_success() throws Exception {
        mockMvc.perform(
                put(String.format("%s/%s/disable", USER_BASE_URL, ID_OF_USER))
        ).andExpect(status().isOk());

        int rows = JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "users",
                String.format("id='%s' and enabled = %b", ID_OF_USER, false));
        assertThat(rows).isEqualTo(1);

    }

    @Test
    void test_get_user_by_id_success() throws Exception {
        ResultActions actions = getUserById(ID_OF_USER).andExpect(status().isOk());
        assertUser(actions);
    }

    @Test
    void test_get_user_by_nonexistent_id() throws Exception {
        ResultActions actions = getUserById(ID_OF_NONEXISTENT).andExpect(status().isNotFound());
        Map<String, Object> resp = MockMvcTestUtils.parseResponseToMap(actions);
        assertThat(resp)
                .containsEntry("message", "resource not found")
                .containsEntry("details", String.format("resource %s with criteria:%s not found", User.class.getName(), "id=" + ID_OF_NONEXISTENT));
    }

    @Test
    void test_get_user_by_username_success() throws Exception {
        ResultActions actions = getUserByUsername(USERNAME_OF_USER).andExpect(status().isOk());
        assertUser(actions);
    }

    @Test
    void test_get_user_by_blank_username() throws Exception {
        ResultActions actions = getUserByUsername("").andExpect(status().isBadRequest());
        Map<String, Object> resp = MockMvcTestUtils.parseResponseToMap(actions);
        assertThat(resp).containsEntry("status", HttpStatus.BAD_REQUEST.value())
                .containsEntry("message", "request parameter validate failure");

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> details = (List<Map<String, Object>>) resp.get("details");
        assertThat(extractParameterValidateError(details))
                .containsExactly(tuple(
                        UserController.class.getName(),
                        "getByUsername",
                        "username",
                        "NotBlank",
                        "can not be empty"
                ));
    }

    @Test
    void test_get_user_by_nonexistent_username() throws Exception {
        ResultActions actions = getUserByUsername("NonExistentUsername").andExpect(status().isNotFound());
        assertResourceNotFound(actions, User.class, "username=NonExistentUsername");
    }

    @Test
    void test_find_all_not_sort_success() throws Exception{
        ResultActions actions = findAll(0, 10).andExpect(status().isOk());
        ReadContext ctx = JsonPath.parse(actions.andReturn().getResponse().getContentAsString());
        assertThat(ctx)
                .hasValue("$.totalPages", 3)
                .hasValue("$.totalElements", 23)
                .hasValue("$.number", 0)
                .hasValue("$.size", 10)
                .isFalse("$.sort.sorted")
                .isTrue("$.sort.unsorted")
                .hasSize("$.content", 10);
    }

    @Test
    void test_find_all_by_criteria_success() throws Exception{
        ResultActions actions = findAllUsingCriteria(0, 10, "test", "username,desc").andExpect(status().isOk());
        ReadContext ctx = JsonPath.parse(actions.andReturn().getResponse().getContentAsString());

        assertThat(ctx).hasSize("$.content", 10);

        List<Map<String, Object>> users = ctx.read("$.content");
        assertThat(users).allMatch((it) -> it.get("username").toString().contains("test") || it.get("displayName").toString().contains("test"));
    }

    @Test
    void test_find_all_with_no_parameter_success() throws Exception{
        ResultActions actions = mockMvc.perform(
                get(USER_BASE_URL)
        ).andExpect(status().isOk());

        ReadContext ctx = JsonPath.parse(actions.andReturn().getResponse().getContentAsString());
        assertThat(ctx)
                .hasSize("$.content", 20)
                .hasValue("$.size", 20)
                .hasValue("$.totalPages", 2)
                .hasValue("$.totalElements", 23)
                .hasValue("$.number", 0)
                .isFalse("$.sort.sorted")
                .isTrue("$.sort.unsorted");

    }
    @Test
    void test_find_all_sorted_by_username_desc_success() throws Exception {
        ResultActions actions = findAll(0, 5, "username,desc").andExpect(status().isOk());
        ReadContext ctx = JsonPath.parse(actions.andReturn().getResponse().getContentAsString());
        assertThat(ctx)
                .contains("$.content[*].username",
                        "user", "test-user-k", "test-user-j", "test-user-i", "test-user-h");

    }

    private ResultActions assignToRoles(String userId, String... roleIds) throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("userId", userId);
        for (String roleId : roleIds) {
            params.add("roleId", roleId);
        }
        return mockMvc.perform(
                put(USER_BASE_URL + "/assignToRoles")
                        .params(params)
        );
    }

    private ResultActions removeFromRoles(String userId, String... roleIds) throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("userId", userId);
        for (String roleId : roleIds) {
            params.add("roleId", roleId);
        }
        return mockMvc.perform(
                put(USER_BASE_URL + "/removeFromRoles")
                        .params(params)
        );
    }

    private ResultActions changePassword(String userId, String oldPassword, String newPassword) throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("oldPassword", oldPassword);
        params.add("newPassword", newPassword);
        return mockMvc.perform(
                post(Strings.formatIfArgs(USER_BASE_URL + "/%s/password", userId))
                        .params(params)
        );
    }

    private ResultActions deleteUser(String userId) throws Exception {
        return mockMvc.perform(
                delete(USER_BASE_URL + "/" + userId)
        );
    }

    private ResultActions getUserById(String userId) throws Exception {
        return mockMvc.perform(
                get(String.format("%s/%s", USER_BASE_URL, userId))
        );
    }

    private ResultActions getUserByUsername(String username) throws Exception {
        return mockMvc.perform(
                get(String.format("%s?username=%s", USER_BASE_URL, username))
        );
    }

    private ResultActions findAll(int page, int size, String... sorts) throws Exception{
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("page", String.valueOf(page));
        params.add("size", String.valueOf(size));
        for(String sort : sorts){
            params.add("sort", sort);
        }

        return mockMvc.perform(
                get(USER_BASE_URL).params(params)
        );
    }

    private ResultActions findAllUsingCriteria(int page, int size, String criteria, String... sorts) throws Exception{
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("page", String.valueOf(page));
        params.add("size", String.valueOf(size));
        params.add("criteria", criteria);
        for(String sort : sorts){
            params.add("sort", sort);
        }

        return mockMvc.perform(
                get(USER_BASE_URL).params(params)
        );
    }

    private List<Tuple> extractParameterValidateError(List<Map<String, Object>> details) {
        return details.stream().map(it -> tuple(
                it.get("className"),
                it.get("methodName"),
                it.get("paramName"),
                it.get("messageCode"),
                it.get("message")
        )).collect(Collectors.toList());
    }

    private void assertResourceNotFound(ResultActions actions, Class<?> resourceType, String criteria) throws UnsupportedEncodingException {
        Map<String, Object> resp = MockMvcTestUtils.parseResponseToMap(actions);
        assertThat(resp)
                .containsEntry("message", "resource not found")
                .containsEntry("details", String.format("resource %s with criteria:%s not found", resourceType.getName(), criteria))
                .containsEntry("status", HttpStatus.NOT_FOUND.value())
                .containsEntry("error", HttpStatus.NOT_FOUND.getReasonPhrase());


    }

    private void assertUser(ResultActions actions) throws Exception {
        Map<String, Object> resp = MockMvcTestUtils.parseResponseToMap(actions);
        assertThat(resp)
                .containsEntry("id", ID_OF_USER)
                .containsEntry("username", USERNAME_OF_USER)
                .containsEntry("displayName", DISPLAY_NAME_OF_USER)
                .containsEntry("enabled", ENABLE_OF_USER);

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> roles = (List<Map<String, Object>>) resp.get("roles");
        assertThat(roles.stream().map(it -> tuple(it.get("id"), it.get("name"))).collect(Collectors.toList()))
                .containsExactlyInAnyOrder(
                        tuple(ID_OF_ROLE_USER, NAME_OF_ROLE_USER),
                        tuple(ID_OF_ROLE_SALES, NAME_OF_ROLE_SALES)
                );

    }

    private boolean isUserAssignedToRole(String userId, String roleId) {
        return JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "users_roles", String.format("users_id='%s' and roles_id='%s'", userId, roleId)) == 1;
    }
}
