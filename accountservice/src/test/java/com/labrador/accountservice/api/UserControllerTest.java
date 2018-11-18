package com.labrador.accountservice.api;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;
import com.labrador.accountservice.utils.MockMvcTestUtils;
import com.labrador.commontests.SpringProfileActive;
import org.assertj.core.api.AbstractInstantAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ActiveProfiles(resolver = SpringProfileActive.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class UserControllerTest {

    private final static String USER_BASE_URL = "/api/users";
    private final static String ID_OF_USER = "297eaf7d508ebfe001508ebfefd20000";
    private final static String USERNAME_OF_USER = "user";
    private final static String DISPLAY_NAME_OF_USER = "张三";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

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
                tuple("NotBlank", "field can not be empty","username", "user name"),
                tuple("NotBlank", "field can not be empty","displayName", "display name"),
                tuple("PasswordStrength", "password does not meet the requirements of the rule", "plainPassword", "plain password"),
                tuple("NotBlank", "field can not be empty","password", "password")
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
                tuple("Length", "length must be between 3 and 50","username", "user name"),
                tuple("Length", "length must be between 3 and 50", "displayName", "display name"),
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
                tuple("user.username.unique", "user(user) already exists","username", "user name")
        );

        objectErrors = context.read("$.details.objectErrors");
        assertThat(objectErrors).isEmpty();
    }

    @Test
    public void test_add_new_user_validate_failure_i18n() throws Exception{
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
                tuple("NotBlank", "值不能为空","username", "登录名"),
                tuple("NotBlank", "值不能为空","displayName", "姓名"),
                tuple("PasswordStrength", "密码不符合规则要求", "plainPassword", "密码"),
                tuple("NotBlank", "值不能为空","password", "密码")
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
                tuple("Length", "长度必须在3和50之间","username", "登录名"),
                tuple("Length", "长度必须在3和50之间", "displayName", "姓名"),
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
                tuple("user.username.unique", "用户名(user)已经存在","username", "登录名")
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
        rows = JdbcTestUtils.countRowsInTable(jdbcTemplate, "users");
        assertThat(rows).isEqualTo(4);

        Map<String, Object> resp = JsonPath.parse(result.andReturn().getResponse().getContentAsString()).read("$");
        assertThat(resp)
                .containsOnlyKeys("id", "username", "displayName", "enabled", "roles", "createdDate", "createdBy", "lastModifiedDate", "lastModifiedBy")
                .containsEntry("username", "newuser")
                .containsEntry("displayName", "新用户")
                .containsEntry("createdBy", "anonymousUser")
                .containsEntry("lastModifiedBy","anonymousUser");
        assertThat(resp.get("id").toString()).isNotBlank();
        assertThat(Instant.parse(resp.get("createdDate").toString()))
                .isEqualTo(Instant.parse(resp.get("lastModifiedDate").toString()))
                .isBefore(Instant.now())
                .isCloseTo(Instant.now(), within(1, ChronoUnit.SECONDS));
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
        rowCount = JdbcTestUtils.countRowsInTable(jdbcTemplate, "users");
        assertThat(rowCount).isEqualTo(3);

        rowCount = JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "users_roles",
                "users_id='" + ID_OF_USER + "'");
        assertThat(rowCount).isEqualTo(1);

        ReadContext context = JsonPath.parse(result.andReturn().getResponse().getContentAsString());
        Map<String, Object> resp = context.read("$");
        assertThat(resp)
                .containsEntry("id", ID_OF_USER)
                .containsEntry("username", "user")
                .containsEntry("displayName", "modified-displayname")
                .containsEntry("enabled", false)
                .containsEntry("createdBy", "297eaf7d508ebfe001508ebff0aa0001")
                .containsEntry("createdDate", "2018-07-31T15:49:56.985Z")
                .containsEntry("lastModifiedBy", "anonymousUser")
                .containsKeys("lastModifiedDate");
        assertThat(Instant.parse(resp.get("lastModifiedDate").toString()))
                .isBefore(Instant.now())
                .isCloseTo(Instant.now(), within(1, ChronoUnit.SECONDS));
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
    void testGetUserById() throws Exception {
        ResultActions result = mockMvc.perform(
                get("/api/users/297eaf7d508ebfe001508ebfefd20000")
        );
        System.out.println(result.andReturn().getResponse().getContentAsString());
    }
}
