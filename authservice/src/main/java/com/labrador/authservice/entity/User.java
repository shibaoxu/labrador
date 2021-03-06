package com.labrador.authservice.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.labrador.commons.entity.EntityWithUUID;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import java.util.*;
import java.util.stream.Collectors;

@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "users")
public class User extends EntityWithUUID implements UserDetails {

    private static final long serialVersionUID = 4132353916579304988L;

    @EqualsAndHashCode.Include
    private String username;
    private String displayName;
    @JsonIgnore
    private String password;
    @Builder.Default
    private boolean enabled = false;

    @ManyToMany
    @Builder.Default
    private Set<Role> roles = new HashSet<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toSet());
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.isEnabled();
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.isEnabled();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.enabled;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
