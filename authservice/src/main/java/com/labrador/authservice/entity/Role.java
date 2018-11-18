package com.labrador.authservice.entity;

import com.labrador.commons.entity.EntityWithUUID;
import lombok.*;

import javax.persistence.Entity;
import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "roles")
public class Role extends EntityWithUUID implements Serializable {
    private static final long serialVersionUID = 1L;
	@EqualsAndHashCode.Include
    private String name;
}
