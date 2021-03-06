package com.labrador.accountservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.labrador.commons.entity.EntityWithUUID;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by shibaoxu on 2017/2/27.
 */
@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "roles")
public class Role extends EntityWithUUID{

    @Length(min = 2, max = 20)
    @Column(unique = true)
    @EqualsAndHashCode.Include
    private String name;

    @Length(max = 100)
    private String description;

    @ManyToMany(mappedBy = "roles", cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JsonIgnore
    @ToString.Exclude
    private Set<User> users = new HashSet<>();

    public void removeAllUser(){
        for(User user : this.users){
            user.getRoles().remove(this);
        }
        this.users.clear();
    }
}
