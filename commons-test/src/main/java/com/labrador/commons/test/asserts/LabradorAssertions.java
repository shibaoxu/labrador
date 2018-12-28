package com.labrador.commons.test.asserts;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;
import org.assertj.core.api.Assertions;
import org.springframework.test.web.servlet.ResultActions;

import java.io.UnsupportedEncodingException;

public class LabradorAssertions extends Assertions {
    public static JsonPathAssert assertThat(ReadContext actual){
        return JsonPathAssert.assertThat(actual);
    }
    public static JsonPathAssert assertThat(ResultActions actions) throws UnsupportedEncodingException {
        String response = actions.andReturn().getResponse().getContentAsString();
        ReadContext context = JsonPath.parse(response);
        return JsonPathAssert.assertThat(context);
    }
}
