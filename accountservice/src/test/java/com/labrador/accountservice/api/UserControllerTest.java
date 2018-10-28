package com.labrador.accountservice.api;

import com.labrador.accountservice.AccountserviceApplication;
import com.labrador.commontests.BaseTest;
import com.labrador.commontests.SpringProfileActive;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import java.io.UnsupportedEncodingException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ActiveProfiles(resolver = SpringProfileActive.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testAddNewUser() throws Exception {
        MultiValueMap params = new LinkedMultiValueMap();
        params.add("id", "newuser");
        ResultActions result = mockMvc.perform(
                post("/api/users")
                    .param("id", "newUser")
        );
//        result.andDo(print());
        System.out.println(result.andReturn().getResponse().getContentAsString());
        System.out.println("test finished!");
    }

    @Test
    void testGetUserById() throws Exception {
        ResultActions result = mockMvc.perform(
                get("/api/users/297eaf7d508ebfe001508ebfefd20000")
        );
        System.out.println(result.andReturn().getResponse().getContentAsString());
    }
}
