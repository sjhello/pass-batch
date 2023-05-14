package com.sjhello.passbatch.repository.packaze;

import java.time.LocalDateTime;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PackageRepository extends JpaRepository<PackageEntity, Integer> {

	List<PackageEntity> findByCreatedAtAfter(LocalDateTime localDateTime, Pageable pageable);

	@Transactional
	@Modifying
	@Query(
		value = "UPDATE PackageEntity p " +
			"       SET p.count = :count," +
			"			p.period = :period" +
			"	  WHERE p.packageSeq = :packageSeq"
	)
	int updateCountAndPeriod(@Param("packageSeq") Integer packageSeq, @Param("count") Integer count, @Param("period") Integer period);
}
