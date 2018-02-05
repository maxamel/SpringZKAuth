package com.github.maxamel.server.domain.model;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * @author Idan Rozenfeld
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class AuditableEntity {
    @CreatedBy
    private String createdBy;

    /*@CreatedDate
    private Date createdDate;
    */

    @LastModifiedBy
    private String lastModifiedBy;

    /*@LastModifiedDate
    private Date lastModifiedDate;
    */

}