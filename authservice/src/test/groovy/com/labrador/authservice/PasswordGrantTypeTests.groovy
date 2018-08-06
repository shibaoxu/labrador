package com.labrador.authservice

import groovy.json.JsonSlurper
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.ResultActions
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap

import static org.assertj.core.api.AssertionsForClassTypes.assertThat
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest(classes = AutherserviceApplication.class)
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName('测试Password授权模式')
class PasswordGrantTypeTests extends AbstractTests {

    @Test
    void testLoginWithUserSuccess() throws Exception {
        def resp = loginWithPassword(USER, USER_PASSWORD, TRUST_WEB_APP, CLIENT_PASSWORD)
        assertThat(resp.access_token).isNotBlank()
        assertThat(resp.token_type).isEqualTo('bearer')
        assertThat(resp.refresh_token).isNotBlank()
        assertThat(resp.expires_in).isGreaterThan(0)
        assertThat(resp.organization).isEqualTo('')
        assertThat(resp.scope).isEqualTo('all-web')
        assertThat(resp.displayName).isEqualTo('张三')


        def claims = decodeJwtToken(resp.access_token)
        assertThat((String[]) claims.aud).containsOnly('newtouch.com')
        assertThat(claims.user_name).isEqualTo('user')
        assertThat((String[]) claims.scope).containsOnly('all-web')
        assertThat((String[]) claims.authorities).containsOnly('ROLE_USER')
        assertThat(claims.jti).isNotBlank()
        assertThat(claims.client_id).isEqualTo(TRUST_WEB_APP)
        assertThat(claims.organization).isEqualTo('')
        assertThat(claims.displayName).isEqualTo('张三')
    }

    @Test
    void testLoginWithAdminSuccess() {
        def resp = loginWithPassword(ADMIN, ADMIN_PASSWORD, TRUST_WEB_APP, CLIENT_PASSWORD)
        assertThat(resp.access_token).isNotBlank()
        assertThat(resp.token_type).isEqualTo('bearer')
        assertThat(resp.refresh_token).isNotBlank()
        assertThat(resp.expires_in).isGreaterThan(0)
        assertThat(resp.organization).isEqualTo('')
        assertThat(resp.scope).isEqualTo('all-web')
        def claims = decodeJwtToken(resp.access_token)
        assertThat((String[]) claims.aud).containsOnly('newtouch.com')
        assertThat(claims.user_name).isEqualTo('admin')
        assertThat((String[]) claims.scope).containsOnly('all-web')
        assertThat((String[]) claims.authorities).containsOnly('ROLE_ADMIN')
        assertThat(claims.jti).isNotBlank()
        assertThat(claims.client_id).isEqualTo(TRUST_WEB_APP)
        assertThat(claims.organization).isEqualTo('')

    }

    @Test
    void testLoginWithOrgUserScucess(){
        def resp = loginWithPassword(ORG_USER, ORG_USER_PASSWORD, TRUST_WEB_APP, CLIENT_PASSWORD)
        assertThat(resp.access_token).isNotBlank()
        assertThat(resp.token_type).isEqualTo('bearer')
        assertThat(resp.refresh_token).isNotBlank()
        assertThat(resp.expires_in).isGreaterThan(0)
        assertThat(resp.organization).isEqualTo('ORG')
        assertThat(resp.scope).isEqualTo('all-web')
        assertThat(resp.displayName).isEqualTo('李四:新致软件')
        def claims = decodeJwtToken(resp.access_token)
        assertThat((String[]) claims.aud).containsOnly('newtouch.com')
        assertThat(claims.user_name).isEqualTo('org:user')
        assertThat((String[]) claims.scope).containsOnly('all-web')
        assertThat((String[]) claims.authorities).containsOnly('ROLE_USER')
        assertThat(claims.jti).isNotBlank()
        assertThat(claims.client_id).isEqualTo(TRUST_WEB_APP)
        assertThat(claims.organization).isEqualTo('ORG')
        assertThat(claims.displayName).isEqualTo('李四:新致软件')
    }
    @Test
    void testLoginWithUserAndWrongPassword() {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>()
        params.add("grant_type", GRANT_TYPE_OF_PASSWORD)
        params.add("username", USER)
        params.add("password", 'wrongPassword')

        ResultActions result = this.mockMvc.perform(post(OAUTH_TOKEN_URL)
                .params(params)
                .with(httpBasic(TRUST_WEB_APP, CLIENT_PASSWORD))
                .accept(JSON_ACCEPT)
        ).andExpect(status().is(400))
        def resp = new JsonSlurper().parseText(result.andReturn().getResponse().getContentAsString())
        assertThat(resp.error).isEqualTo('invalid_grant')
        assertThat(resp.error_description).isEqualTo('Bad credentials')
    }

    @Test
    void testLoginWithWrongUsername(){
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>()
        params.add("grant_type", GRANT_TYPE_OF_PASSWORD)
        params.add("username", "WrongUserName")
        params.add("password", 'wrongPassword')

        ResultActions result = this.mockMvc.perform(post(OAUTH_TOKEN_URL)
                .params(params)
                .with(httpBasic(TRUST_WEB_APP, CLIENT_PASSWORD))
                .accept(JSON_ACCEPT)
        ).andExpect(status().is(400))
        def resp = new JsonSlurper().parseText(result.andReturn().getResponse().getContentAsString())
        assertThat(resp.error).isEqualTo('invalid_grant')
        assertThat(resp.error_description).isEqualTo('Bad credentials')

    }

    @Test
    void testTestLoginWithUserAndWrongClientSecret(){
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>()
        params.add("grant_type", GRANT_TYPE_OF_PASSWORD)
        params.add("username", USER)
        params.add("password", USER_PASSWORD)
        ResultActions result = this.mockMvc.perform(post(OAUTH_TOKEN_URL)
                .params(params)
                .with(httpBasic(TRUST_WEB_APP, 'WrongPassword'))
                .accept(JSON_ACCEPT)
        ).andExpect(status().is(401))
    }

    @Test
    void testRefreshToken(){
        def resp = loginWithPassword(ADMIN, ADMIN_PASSWORD, TRUST_WEB_APP, CLIENT_PASSWORD)
        def accessToken = resp.access_token
        String refreshToken = resp.refresh_token

        def refreshResp = this.refreshToken(refreshToken, TRUST_WEB_APP, CLIENT_PASSWORD)
        assertThat(refreshResp.access_token).isNotEqualToIgnoringCase(accessToken).isNotBlank()
        assertThat(refreshResp.refresh_token).isNotBlank()
    }

}
