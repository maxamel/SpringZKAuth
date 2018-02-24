package com.github.maxamel.server.domain.model;

import com.github.maxamel.server.domain.model.constraints.UserEntryNameUnique;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * @author Max Amelchenko
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(of = "id", callSuper = true)
@Entity
@Table(uniqueConstraints =
		{@UniqueConstraint(name = UserEntryNameUnique.CONSTRAINT_NAME, columnNames = {UserEntryNameUnique.USER_NAME, UserEntryNameUnique.ENTRY_NAME})})
@SequenceGenerator(name = "diarySeq", sequenceName = "diary_seq")
public class Diary extends AuditableEntity {

    @Id
    @GeneratedValue(generator = "diarySeq", strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotEmpty
    @Column(nullable = false, name = UserEntryNameUnique.USER_NAME)
    private String username;
    
    @NotEmpty
    @Column(nullable = false, name = UserEntryNameUnique.ENTRY_NAME)
    private String entryname;

    @NotEmpty
    @Column(nullable = false, length = 600)
    private String content;

}