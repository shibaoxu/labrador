package com.labrador.authservice.entity;

import javax.persistence.Entity;

import com.newtouch.labrador.commons.db.EntityWithUUID;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity(name = "roles")
public class Role extends EntityWithUUID implements Serializable {
    private String name;

    public Role() {
    }

    public Role(String id, String name) {
        this.setId(id);
        this.setName(name);
    }
}
