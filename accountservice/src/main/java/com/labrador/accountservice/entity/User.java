package com.labrador.accountservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.labrador.commons.db.EntityWithUUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.*;


/**
 * Created by shibaoxu on 2017/3/9.
 */
@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User extends EntityWithUUID{

    @Length(min = 3, max = 50)
    @NotBlank()
    @Column(updatable = false, unique = true)
    @EqualsAndHashCode.Include
    private String username;

    @Length(min = 3, max = 50)
    @NotBlank(message = "{email.notempty}")
    private String displayName;

    @Transient
    @JsonIgnore
    private String plainPassword;

    @Column(updatable = false)
    @NotBlank
    @JsonIgnore
    private String password;

    private boolean enabled = true;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Role> roles = new ArrayList<>();

    public User(String username, String displayName, String plainPassword){
        this.username = username;
        this.displayName = displayName;
        this.setPlainPassword(plainPassword);
    }

    public void setPlainPassword(String plainPassword) {
        this.plainPassword = plainPassword;
        this.password = new BCryptPasswordEncoder().encode(plainPassword);
    }
}
