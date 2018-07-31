package com.labrador.authservice

import groovy.json.JsonSlurper
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.ResultActions

import static org.assertj.core.api.AssertionsForClassTypes.assertThat
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class UserControllerTests extends AbstractTests {
    @Test
    void testGetUserInfoSuccess(){
        def resp = loginWithPassword(USER, USER_PASSWORD, TRUST_WEB_APP, CLIENT_PASSWORD)
        ResultActions result = mockMvc.perform(get(USER_INFO_URL).header("Authorization", "Bearer $resp.access_token"))
            .andExpect(status().isOk())
        def userinfo = new JsonSlurper().parseText(result.andReturn().getResponse().getContentAsString())
        assertThat(userinfo.id).isEqualTo('297eaf7d508ebfe001508ebfefd20000')
        assertThat(userinfo.username).isEqualTo('user')
        assertThat(userinfo.displayName).isEqualTo('张三')
        assertThat(userinfo.enabled).isTrue()
        assertThat(userinfo.authorities.size).isEqualTo(1)
    }
}
