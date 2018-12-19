package com.labrador.authservice

import com.labrador.commons.test.BaseTest
import groovy.json.JsonSlurper
import org.junit.jupiter.api.BeforeAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.jwt.Jwt
import org.springframework.security.jwt.JwtHelper
import org.springframework.security.web.FilterChainProxy
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.context.WebApplicationContext

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

abstract class AbstractTests extends BaseTest{
    public static final String OAUTH_TOKEN_URL = "/oauth/token"
    public static final String REFRESH_TOKEN_URL = "/oauth/refresh_token"
    public static final String USER_INFO_URL = "/user/me"

    public static final String TRUST_WEB_APP = "trust-web";
    public static final String TRUST_MOBILE_APP = "trust-mobile";
    public static final String THIRD_PARTY_WEB_APP = "third-party-web-app"
    public static final String CLIENT_PASSWORD = 'password'

    public static final String GRANT_TYPE_OF_PASSWORD = "password"
    public static final String GRANT_TYPE_OF_REFRESH_TOKEN = "refresh_token"

    public static final String JSON_ACCEPT = 'application/json;charset=UTF-8'

    public static final String USER = 'user'
    public static final String USER_PASSWORD = 'password'
    public static final String ADMIN = 'admin'
    public static final String ADMIN_PASSWORD = 'password'
    public static final String ORG_USER = 'org:user'
    public static final String ORG_USER_PASSWORD = 'password'


    public MockMvc mockMvc

    @Autowired
    private WebApplicationContext wac;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    @BeforeAll
    void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .addFilter(springSecurityFilterChain).build()
    }

    def decodeJwtToken(accessToken){
        Jwt jwt = JwtHelper.decode(accessToken)
        return new JsonSlurper().parseText(jwt.getClaims())
    }
    def loginWithPassword(String username, String password, String clientId, String clientSecurity) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>()
        params.add("grant_type", GRANT_TYPE_OF_PASSWORD)
        params.add("username", username)
        params.add("password", password)

        ResultActions result = this.mockMvc.perform(post(OAUTH_TOKEN_URL)
                .params(params)
                .with(httpBasic(clientId, clientSecurity))
                .accept(JSON_ACCEPT))
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_ACCEPT))
        String resultString = result.andReturn().getResponse().getContentAsString()
        return new JsonSlurper().parseText(resultString)
    }

    def refreshToken(String refreshToken, String clientId, String clientSecret){
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>()
        params.add("grant_type", GRANT_TYPE_OF_REFRESH_TOKEN)
        params.add("refresh_token", refreshToken)
        ResultActions result = this.mockMvc.perform(post(OAUTH_TOKEN_URL)
                .params(params)
                .with(httpBasic(clientId, clientSecret))
                .accept(JSON_ACCEPT))
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_ACCEPT))
        String resultString = result.andReturn().getResponse().getContentAsString()
        return new JsonSlurper().parseText(resultString)
    }

    def loginWithPassword(username, password, clientId, clientSecret){
        def resp = loginWithPassword(username, password, clientId, clientSecret)
        if (resp.access_token != null && !resp.isEqual('')){
            return resp.access_token
        } else {
            throw RuntimeException('登录失败')
        }
    }

    def loginWithPassword(username, password){
        return loginWithPassword(username, password, TRUST_WEB_APP, CLIENT_PASSWORD)
    }
}
