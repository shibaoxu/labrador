package com.labrador.accountservice.utils;

import com.jayway.jsonpath.ReadContext;
import org.assertj.core.api.Assertions;

public class LabradorAssertions extends Assertions {
    public static JsonPathAssert assertThat(ReadContext actual){
        return JsonPathAssert.assertThat(actual);
    }
}
