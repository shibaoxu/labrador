package com.labrador.authservice.entity;

import javax.persistence.Entity;

import com.newtouch.labrador.commons.db.EntityWithUUID;
import lombok.*;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "roles")
public class Role extends EntityWithUUID implements Serializable {
    @EqualsAndHashCode.Include
    private String name;
}
