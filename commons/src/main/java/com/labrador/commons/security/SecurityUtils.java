package com.labrador.commons.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;

/**
 * Created by shibaoxu on 2017/3/13.
 */
public final class SecurityUtils {
    public static String getUsername(){
        return SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
//        return user.getUsername();
    }

    public static String[] getRoles(){
        Collection<? extends GrantedAuthority> users = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        return users.stream().map(GrantedAuthority::getAuthority).toArray(String[]::new);
    }
}
