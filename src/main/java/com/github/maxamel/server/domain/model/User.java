package com.github.maxamel.server.domain.model;

import com.github.maxamel.server.domain.model.constraints.UserNameUnique;

import java.math.BigInteger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * @author Max Amelchenko
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString(callSuper = true)
@EqualsAndHashCode(of = "id", callSuper = true)
@Entity
@Table(uniqueConstraints =
        {@UniqueConstraint(name = UserNameUnique.CONSTRAINT_NAME, columnNames = UserNameUnique.FIELD_NAME)})
@SequenceGenerator(name = "userSeq", sequenceName = "user_seq")
public class User extends AuditableEntity {

    @Id
    @GeneratedValue(generator = "userSeq", strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotEmpty
    @Column(nullable = false, name = UserNameUnique.FIELD_NAME)
    private String name;

    @NotNull
    @Column(nullable = false)
    private BigInteger token;

    @NotNull
    private boolean session;

}