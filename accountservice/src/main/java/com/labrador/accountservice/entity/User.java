package com.labrador.accountservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.labrador.accountservice.entity.validation.PasswordStrength;
import com.labrador.commons.entity.EntityWithUUID;
import com.labrador.commons.entity.validation.NewEntityValidationGroup;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;

import javax.persistence.*;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.Set;


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
    @NotBlank
    @Column(updatable = false, unique = true)
    @EqualsAndHashCode.Include
    private String username;

    @Length(max = 50)
    @NotBlank
    private String displayName;

    @Transient
    @JsonIgnore
    @PasswordStrength(groups = NewEntityValidationGroup.class)
    @ToString.Exclude
    private String plainPassword;

    @Column(updatable = false)
    @NotBlank(groups = NewEntityValidationGroup.class)
    @JsonIgnore
    @ToString.Exclude
    private String password;

    @AssertTrue(groups = NewEntityValidationGroup.class)
    private boolean enabled = true;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @ToString.Exclude
    private Set<Role> roles = new HashSet<>();

    public User(String username, String displayName, String plainPassword){
        this.username = username;
        this.displayName = displayName;
        this.setPlainPassword(plainPassword);
    }

    public void setPlainPassword(String plainPassword) {
        this.plainPassword = plainPassword;
        this.password = PasswordEncoderFactories.createDelegatingPasswordEncoder().encode(plainPassword);
    }
}
