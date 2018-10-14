package com.labrador.commons.db;

import com.labrador.commons.security.SecurityUtils;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.time.Instant;

/**
 * Created by shibaoxu on 2017/3/10.
 */
@MappedSuperclass
@Data
public class EntityWithUUID {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator", parameters = {
            @Parameter(name = "uuid_gen_strategy_class", value = "org.hibernate.id.uuid.CustomVersionOneStrategy") })
    private String id;

    @Column(updatable = false)
    private Instant createdDate;

    @LastModifiedDate
    private Instant lastModifiedDate;
    @Column(updatable = false)
    private String createdBy;
    private String lastModifiedBy;

    @PrePersist
    public void setInsertInfo() {
        this.createdDate = Instant.now();
        this.lastModifiedDate = createdDate;
        this.createdBy = SecurityUtils.getUsername();
        this.lastModifiedBy = SecurityUtils.getUsername();
    }

    @PreUpdate
    public void setUpdateInfo() {
        this.lastModifiedDate = Instant.now();
        this.lastModifiedBy = SecurityUtils.getUsername();
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
}
