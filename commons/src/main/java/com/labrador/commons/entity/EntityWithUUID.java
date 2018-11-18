package com.labrador.commons.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.labrador.commons.entity.validation.Blank;
import com.labrador.commons.entity.validation.NewEntityValidationGroup;
import com.labrador.commons.entity.validation.UpdateEntityValidationGroup;
import com.labrador.commons.security.SecurityUtils;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.Instant;

/**
 * Created by shibaoxu on 2017/3/10.
 */
@MappedSuperclass
@Data
@EntityListeners(AuditingEntityListener.class)
public class EntityWithUUID {
    @Id
    @NotBlank(groups = UpdateEntityValidationGroup.class)
    @Blank(groups = NewEntityValidationGroup.class)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator", parameters = {
            @Parameter(name = "uuid_gen_strategy_class", value = "org.hibernate.id.uuid.CustomVersionOneStrategy") })
    private String id;

    @Column(updatable = false)
    @CreatedDate
    private Instant createdDate;

    @LastModifiedDate
    private Instant lastModifiedDate;

    @Column(updatable = false)
    @CreatedBy
    private String createdBy;

    @LastModifiedBy
    private String lastModifiedBy;

    @JsonIgnore
    public boolean isNew(){
        return !StringUtils.hasText(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        EntityWithUUID that = (EntityWithUUID) o;

        if (!StringUtils.hasText(this.getId()) || !StringUtils.hasText(that.getId())) {
            return false;
        }
        return id.equals(that.getId());

    }

    @Override
    public int hashCode() {
        return id == null ? 0 : id.hashCode();
    }

    @Override
    public String toString(){
        return this.getClass().getName() + "@" + getId();
    }

}
