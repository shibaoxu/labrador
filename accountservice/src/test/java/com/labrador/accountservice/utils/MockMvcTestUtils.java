package com.labrador.accountservice.utils;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;
import lombok.NonNull;
import org.springframework.test.web.servlet.ResultActions;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public class MockMvcTestUtils {
    public static Map<String, Object> parseResponseToMap(@NonNull ResultActions resultActions) throws UnsupportedEncodingException {
        ReadContext readContext = JsonPath.parse(resultActions.andReturn().getResponse().getContentAsString());
        return readContext.read("$");
    }

    public static ReadContext parseResponseToReadContext(@NonNull ResultActions resultActions) throws UnsupportedEncodingException {
        return JsonPath.parse(resultActions.andReturn().getResponse().getContentAsString());
    }
}
