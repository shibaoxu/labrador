package com.labrador.authservice.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.newtouch.labrador.commons.db.EntityWithUUID;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Data;

import java.util.Collection;

import javax.persistence.Entity;

@Data
@Entity(name = "users")
public class User extends EntityWithUUID implements UserDetails {

    private static final long serialVersionUID = 4132353916579304988L;
    
    private String username;
    private String displayName;
    @JsonIgnore
    private String password;
    private boolean enabled = false;
    private Collection<? extends GrantedAuthority> authorities = null;
    private Collection<Role> roles = null;


    @Override
    public boolean isAccountNonExpired() {
        return enabled;
    }

    @Override
    public boolean isAccountNonLocked() {
        return enabled;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return enabled;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
