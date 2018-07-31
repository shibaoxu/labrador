package com.labrador.authservice.service;

import com.labrador.authservice.entity.Role;
import com.labrador.authservice.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.stream.Collectors;

@Component
@Primary
@Transactional(readOnly = true)
public class CustomUserDetailsService implements UserDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = getUserByUsername(username);
        Collection<Role> rols = getRoles(username);
        user.setAuthorities(rols.stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toSet()));
        return user;
    }

    private User getUserByUsername(String username) {
        String sql = "select id, username, display_name, password, enabled from users where username = :username";
        SqlParameterSource namedParameters = new MapSqlParameterSource("username", username);
        try {
            return namedParameterJdbcTemplate.queryForObject(sql, namedParameters, (rs, rowNum) -> {
                User user = new User();
                user.setId(rs.getString("id"));
                user.setUsername(rs.getString("username"));
                user.setDisplayName(rs.getString("display_name"));
                user.setEnabled(rs.getBoolean("enabled"));
                user.setPassword(rs.getString("password"));
                return user;
            });
        }catch (IncorrectResultSizeDataAccessException exception){
            logger.warn("没有注册的用户正在尝试登录-->{}", username);
            throw new UsernameNotFoundException(username);
        }catch (Exception ex){
            logger.error("[{}]登录时发生未知错误", ex.getMessage(), ex);
            throw new UsernameNotFoundException(username);
        }
    }

    private Collection<Role> getRoles(String username){
        String sql = "SELECT r.id, r.name FROM roles r " +
                "INNER JOIN USERS_ROLES ur ON ur.ROLES_ID = r.ID " +
                "INNER JOIN USERS u ON u.id = ur.USERS_ID " +
                "WHERE u.USERNAME = :username";
        SqlParameterSource namedParameters = new MapSqlParameterSource("username", username);
        return namedParameterJdbcTemplate.query(sql, namedParameters, (rs, rowNum) -> new Role(rs.getString("id"), rs.getString("name")));
    }
}
