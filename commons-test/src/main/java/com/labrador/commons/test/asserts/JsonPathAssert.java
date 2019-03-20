package com.labrador.commons.test.asserts;

import com.jayway.jsonpath.PathNotFoundException;
import com.jayway.jsonpath.ReadContext;
import org.assertj.core.api.AbstractAssert;
import org.springframework.boot.autoconfigure.jsonb.JsonbAutoConfiguration;
import org.springframework.util.StringUtils;
import org.springframework.validation.ObjectError;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class JsonPathAssert extends AbstractAssert<JsonPathAssert, ReadContext> {

    public JsonPathAssert(ReadContext actual) {
        super(actual, JsonPathAssert.class);
    }

    public static JsonPathAssert assertThat(ReadContext actual){
        return new JsonPathAssert(actual);
    }

    public JsonPathAssert hasPath(String path){
        try {
            actual.read(path);
        } catch (PathNotFoundException e){
            failWithMessage("Expeted path <%s> exist, but it is not.", path);
        }
        return this;
    }

    public JsonPathAssert hasPaths(String... paths){
        for(String path: paths){
            try{
                actual.read(path);
            } catch (PathNotFoundException e){
                failWithMessage("Expeted path <%s> exist, but it is not.", path);
                break;
            }
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

    public JsonPathAssert isNull(String path){
        Object value = actual.read(path);
        if (value != null){
            failWithMessage("Expected value of %s to be <null> but it is not.", path);
        }
        return this;
    }

    public JsonPathAssert isEmpty(String path){
        String value = actual.read(path);
        if (!StringUtils.isEmpty(value)){
            failWithMessage("Expected value of %s to be <Empty> but it is <%s>.", path, value);
        }
        return this;
    }

    public JsonPathAssert isBlank(String path){
        String value = actual.read(path);
        if (StringUtils.hasText(value)){
            failWithMessage("Expected value of %s to be <Blank> but it is <%s>.", path, value);
        }
        return this;
    }
    @SuppressWarnings("unchecked")
    public <T> JsonPathAssert contains(String path, T... values){
        List<T> actualValues = actual.read(path);

        Set<T> notFound = new HashSet<>();
        for(T value: values){
            if (!actualValues.contains(value)){
                notFound.add(value);
            }
        }

        if (!notFound.isEmpty()){
            String actualMessage = String.join(",", actualValues.stream().map(Object::toString).collect(Collectors.toList()));
            String expectedMessage = String.join(",", Arrays.stream(values).map(Object::toString).collect(Collectors.toList()));
            String notFoundMessage = String.join(",", notFound.stream().map(Object::toString).collect(Collectors.toList()));
            failWithMessage("Expected <%s> contain values <%s>, but <%s> not found.", actualMessage, expectedMessage, notFoundMessage);
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
