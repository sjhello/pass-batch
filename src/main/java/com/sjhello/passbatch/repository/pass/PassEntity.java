package com.sjhello.passbatch.repository.pass;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.sjhello.passbatch.repository.BaseEntity;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "pass")
public class PassEntity extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer passSeq;

	private Integer packageSeq;
	private String userId;

	@Enumerated(EnumType.STRING)
	private PassStatus status;
	private Integer remainingCount;

	private LocalDateTime startedAt;
	private LocalDateTime endedAt;
	private LocalDateTime expiredAt;
}
