package com.sjhello.passbatch.repository.packaze;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
class PackageRepositoryTest {

	@Autowired
	private PackageRepository packageRepository;

	@Test
	void testSave() {
		// given
		PackageEntity packageEntity = new PackageEntity();
		packageEntity.setPeriod(84);
		packageEntity.setPackageName("바디 챌린지 PT 84일");

		// when
		packageRepository.save(packageEntity);

		// then
		assertNotNull(packageEntity.getPackageName());
	}

	@Test
	void test_findByCreatedAtAfter() {
		// given
		LocalDateTime localDateTime = LocalDateTime.now().minusMinutes(1);

		PackageEntity packageEntity0 = new PackageEntity();
		packageEntity0.setPeriod(84);
		packageEntity0.setPackageName("학생 할인 3개월");
		packageRepository.save(packageEntity0);

		PackageEntity packageEntity1 = new PackageEntity();
		packageEntity1.setPeriod(84);
		packageEntity1.setPackageName("학생 할인 6개월");
		packageRepository.save(packageEntity1);

		// when
		List<PackageEntity> packageEntities = packageRepository.findByCreatedAtAfter(localDateTime, PageRequest.of(0, 1,
			Sort.by("packageSeq").descending()));

		// then
		assertEquals(1, packageEntities.size());
		assertEquals(packageEntities.get(0).getPackageName(), packageEntity1.getPackageName());
	}

	@Test
	void test_updateCountAndPeriod() {
		// given
		PackageEntity packageEntity = new PackageEntity();
		packageEntity.setPeriod(90);
		packageEntity.setPackageName("바디 챌린지 PT 84일");
		packageRepository.save(packageEntity);

		// when
		// 구체적인 update를 위해 jpql 사용하여 update
		Integer updateCount = 30;
		Integer updatePeriod = 120;
		int result = packageRepository.updateCountAndPeriod(packageEntity.getPackageSeq(), 30, 120);
		PackageEntity packageEntity1 = packageRepository.findById(packageEntity.getPackageSeq()).get();

		// then
		assertAll(() -> {
			assertEquals(1, result);
			assertEquals(30, packageEntity1.getCount());
			assertEquals(120, packageEntity1.getPeriod());
		});
	}

	@Test
	void test_delete() {
		// given

		// when
		packageRepository.deleteById(17);

		// then
		assertTrue(packageRepository.findById(17).isEmpty());
	}
}
