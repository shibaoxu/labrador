package com.labrador.authservice.entity;

import javax.persistence.Entity;

import com.newtouch.labrador.commons.db.EntityWithUUID;
import lombok.Data;

@Data
@Entity(name="users")
public class Role extends EntityWithUUID {
    private String name;
}
