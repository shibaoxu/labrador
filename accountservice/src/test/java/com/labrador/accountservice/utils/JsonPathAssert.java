package com.labrador.accountservice.utils;

import com.jayway.jsonpath.ReadContext;
import org.assertj.core.api.AbstractAssert;

import java.util.Arrays;
import java.util.List;

public class JsonPathAssert extends AbstractAssert<JsonPathAssert, ReadContext> {

    public JsonPathAssert(ReadContext actual) {
        super(actual, JsonPathAssert.class);
    }

    public static JsonPathAssert assertThat(ReadContext actual){
        return new JsonPathAssert(actual);
    }

    public JsonPathAssert hasPath(String path){
        if (actual.read(path) == null){
            failWithMessage("Expeted path %s's value is <not null>, but was <null>", path);
        }
        return this;
    }

    public JsonPathAssert hasValue(String path, int expectedValue){
        int actualValue = actual.read(path);
        if (actualValue != expectedValue){
            failWithMessage("Expected value of %s to be <%d> but was <%d>", path, expectedValue, actualValue);
        }
        return this;
    }

    public JsonPathAssert hasValue(String path, String expectedValue){
        String actualValue = actual.read(path);
        if (!actualValue.equals(expectedValue)){
            failWithMessage("Expected value of %s to be <%s> but was <%s>",path, expectedValue, actualValue);
        }
        return this;
    }

    public JsonPathAssert isTrue(String path){
        if (!actual.<Boolean>read(path)){
            failWithMessage("Expected value of %s to be <True> but was <False>", path);
        }
        return this;
    }

    public JsonPathAssert isFalse(String path){
        if (actual.<Boolean>read(path)){
            failWithMessage("Expected value of %s to be <True> but was <False>", path);
        }
        return this;
    }

    public <T>JsonPathAssert contains(String path, T... values){
        List<T> actuals = actual.read(path);
        if (!actuals.containsAll(Arrays.asList(values))){
            failWithMessage("Expected values are not all contained in the value of %s", path);
        }
        return this;
    }

    public JsonPathAssert hasSize(String path, int expectedSize){
        int actualSize = actual.read(path + ".length()");
        if(actualSize != expectedSize){
            failWithMessage("Expected values of %s have <%d> elements but <%d>", path, expectedSize, actualSize);
        }
        return this;
    }

}
