package com.labrador.accountservice;

import org.junit.jupiter.api.Test;

import java.util.Optional;


public class HelloTest {

    @Test
    public void testOptional(){
//        Optional<String> notempty = Optional.ofNullable("hello");
//        Optional<String> empty = Optional.ofNullable(null);
//        Optional<String> name = notempty.map(String::toString);
        Person person = new Person("john", 27, null);
        Optional<Person> personOptional = Optional.of(person);

        personOptional.map(Person::getName)ssi
    }
}
