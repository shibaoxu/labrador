package com.labrador.accountservice.utils;

import com.jayway.jsonpath.ReadContext;
import org.assertj.core.api.SoftAssertions;

public class LabradorSoftAssertions extends SoftAssertions {
    public JsonPathAssert assertThat(ReadContext actual){
        return proxy(JsonPathAssert.class, ReadContext.class, actual);
    }
}
