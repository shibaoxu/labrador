package com.labrador.authservice;

import com.google.common.io.CharStreams;
import com.jayway.jsonpath.JsonPath;
import com.labrador.commons.test.MockMvcTestUtils;
import com.labrador.commons.test.SpringProfileActive;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

import static com.labrador.commons.test.asserts.LabradorAssertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.within;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@ActiveProfiles(resolver = SpringProfileActive.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class AuthTests {
    @Autowired
    private MockMvc mockMvc;

    private static final String OAUTH_TOKEN_URL = "/oauth/token";
    private static final String USER_INFO_URL = "/user/me";

    private static final String TRUST_WEB_APP = "trust-web";
    private static final String CLIENT_PASSWORD = "password";

    private static final String GRANT_TYPE_OF_PASSWORD = "password";
    private static final String GRANT_TYPE_OF_REFRESH_TOKEN = "refresh_token";

    private static final String JSON_ACCEPT = "application/json;charset=UTF-8";

    private static final String USER_NAME = "user";
    private static final String USER_PASSWORD = "password";
    private static final String WRONG_PASSWORD = "wrongpassword";
    private static final String ADMIN_NAME = "admin";
    private static final String ADMIN_PASSWORD = "password";
    private static final String ORG_USER = "org:user";
    private static final String ORG_USER_PASSWORD = "password";

    @Test
    @DisplayName("登录成功")
    public void test_login_success() throws Exception {
        ResultActions actions = loginWithPassword(USER_NAME, USER_PASSWORD)
                .andExpect(status().isOk());

        // 检查response内容
        assertThat(actions)
                .hasValue("displayName", "张三")
                .hasValue("scope", "read write")
                .isEmpty("organization")
                .hasValue("token_type", "bearer")
                .hasValue("expires_in", 12 * 60 * 60 - 1)
                .hasPaths("access_token","refresh_token", "expires_in", "jti");

        // 检查access_token内容
        String accessToken = extractAccessToken(actions);
        assertThat(JsonPath.parse(decodeJwtToken(accessToken)))
                .hasValue("user_name", "user")
                .hasValue("displayName", "张三")
                .hasValue("client_id", "trust-web")
                .isEmpty("organization")
                .contains("aud", "all")
                .contains("scope", "read", "write")
                .contains("authorities", "ROLE_USER");
        int instant = JsonPath.read(decodeJwtToken(accessToken), "exp");
        LocalDateTime exp = LocalDateTime.ofEpochSecond(instant, 0, OffsetDateTime.now().getOffset());
        assertThat(exp).isCloseTo(LocalDateTime.now().plusHours(12), within(1, ChronoUnit.SECONDS));

        // 验证签名
        String publicKey = CharStreams.toString(new InputStreamReader(new ClassPathResource("public.txt").getInputStream()));
        assertThatCode(() ->{
            JwtHelper.decode(accessToken).verifySignature(new RsaVerifier(publicKey));
        }).doesNotThrowAnyException();
    }

    @Test
    public void test_login_with_wrong_password_failure() throws Exception {
        ResultActions actions = loginWithPassword(USER_NAME, WRONG_PASSWORD)
                .andExpect(status().isBadRequest());
        assertThat(MockMvcTestUtils.parseResponseToMap(actions))
                .containsEntry("error", "invalid_grant")
                .containsEntry("error_description", "Bad credentials");
    }

    @Test
    public void test_login_with_wrong_client_secret_failure() throws Exception {
        ResultActions actions = loginWithPassword(USER_NAME, USER_PASSWORD, TRUST_WEB_APP, WRONG_PASSWORD)
                .andExpect(status().isUnauthorized());
        assertThat(actions.andReturn().getResponse().getContentAsString()).isEmpty();
    }

    @Test
    public void test_refresh_token_success() throws Exception {
        String response = loginWithPassword(USER_NAME, USER_PASSWORD).andReturn().getResponse().getContentAsString();
        String accessToken = JsonPath.read(response, "access_token");
        String refreshToken = JsonPath.read(response, "refresh_token");
        response = refreshToken(refreshToken).andReturn().getResponse().getContentAsString();
        String refreshedAccessToken = JsonPath.read(response, "access_token");
        String refreshedRefreshtoken = JsonPath.read(response, "refresh_token");

        assertThat(accessToken).isNotEqualToIgnoringCase(refreshedAccessToken);
        assertThat(refreshToken).isNotEqualToIgnoringCase(refreshedRefreshtoken);

    }
    private ResultActions loginWithPassword(String username, String password, String clientId, String clientSecret) throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", GRANT_TYPE_OF_PASSWORD);
        params.add("username", username);
        params.add("password", password);
        return mockMvc.perform(
                post(OAUTH_TOKEN_URL)
                    .params(params)
                    .accept(JSON_ACCEPT)
                    .with(httpBasic(clientId, clientSecret))
        );
    }

    private ResultActions refreshToken(String refreshToken, String clientId, String clientSecret) throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", GRANT_TYPE_OF_REFRESH_TOKEN);
        params.add("refresh_token", refreshToken);
        return mockMvc.perform(post(OAUTH_TOKEN_URL)
                .params(params)
                .with(httpBasic(clientId, clientSecret))
                .accept(JSON_ACCEPT));
    }


    private ResultActions refreshToken(String refreshToken) throws Exception {
        return refreshToken(refreshToken, TRUST_WEB_APP, CLIENT_PASSWORD);
    }
    private ResultActions loginWithPassword(String username, String password) throws Exception {
        return loginWithPassword(username, password, TRUST_WEB_APP, CLIENT_PASSWORD);
    }

    private String decodeJwtToken(String token){
        Jwt jwt = JwtHelper.decode(token);
        return jwt.getClaims();
    }

    private String extractAccessToken(ResultActions actions) throws UnsupportedEncodingException {
        String resp = actions.andReturn().getResponse().getContentAsString();
        return JsonPath.read(resp, "access_token");
    }
}
