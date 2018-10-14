package com.labrador.authservice

import com.labrador.authservice.entity.User
import com.labrador.commons.db.EntityWithUUID
import org.junit.jupiter.api.Test


import static org.assertj.core.api.Assertions.assertThat

class EntityEqualsTests {

    @Test
    void testEquals(){
        def user1 = new User()
        def user2 = new User()
        assertThat(user1).isNotEqualTo(user2)

        user1.setId("1")
        user2.setId("1")
        assertThat(user1).isEqualTo(user2)

        user1.setUsername('newuser')
        user1.setDisplayName("张三")
        assertThat(user1).isNotEqualTo(user2)

        user2.setUsername('newuser')
        user2.setDisplayName("李四")
        assertThat(user1).isEqualTo(user2)
    }

    @Test
    void testCollection(){
        Set<User> setUsers = new HashSet<>();
        def user1 = User.builder().username("newuser").displayName("user1").build()
        def user2 = User.builder().username("newuser").displayName("user2").build()
        user1.setId("1")
        user2.setId("1")
        assertThat(user1).isEqualTo(user2)

        setUsers.add(user1)

        assertThat(setUsers).contains(user1).contains(user2).hasSize(1)
        setUsers.add(user2)
        assertThat(setUsers)
                .containsOnly(user1)
                .containsOnly(user2)
                .hasSize(1)
                .extracting("displayName")
                .containsOnly("user1")

        List<User> listUsers = new ArrayList<>()
        listUsers.add(user1)
        listUsers.add(user2)
        assertThat(listUsers)
                .hasSize(2)
                .contains(user1)
                .contains(user2)
                .extracting("displayName")
                .containsExactly("user1", "user2")
    }

    @Test
    void testSuperClass(){
        def user1 = new User()
        def user2 = new User()
        user1.setId("1")
        user1.setUsername("user1")
        user2.setId("2")
        user2.setUsername("user2")

        assertThat(user1).isNotEqualTo(user2)

        EntityWithUUID sUser1 = (EntityWithUUID)user1
        EntityWithUUID sUser2 = (EntityWithUUID)user2

        assertThat(sUser1).isNotEqualTo(sUser2)
    }
}
