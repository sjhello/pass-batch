package com.sjhello.passbatch.job.pass;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import com.sjhello.passbatch.config.TestBatchConfig;
import com.sjhello.passbatch.repository.pass.PassEntity;
import com.sjhello.passbatch.repository.pass.PassRepository;
import com.sjhello.passbatch.repository.pass.PassStatus;

@SpringBootTest
@SpringBatchTest
@ActiveProfiles("test")
@ContextConfiguration(classes = {ExpirePassesJobConfig.class, TestBatchConfig.class})
class ExpirePassesJobConfigTest {

	// e2e 테스트 지원하는 객체
	@Autowired
	private JobLauncherTestUtils jobLauncherTestUtils;

	@Autowired
	private PassRepository passRepository;

	@Test
	void test_expirePassesStep() throws Exception {
		// given
		addPassEntities(10);

		// when
		JobExecution jobExecution = jobLauncherTestUtils.launchJob();
		JobInstance jobInstance = jobExecution.getJobInstance();

		// then
		assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());
		assertEquals("expirePassesJob", jobInstance.getJobName());
	}

	private void addPassEntities(int size) {
		final LocalDateTime now = LocalDateTime.now();
		final Random random = new Random();

		List<PassEntity> passEntities = new ArrayList<>();
		for (int i = 0; i < size; ++i) {
			PassEntity passEntity = new PassEntity();
			passEntity.setPackageSeq(1);
			passEntity.setUserId("A" + 1000000 + i);
			passEntity.setStatus(PassStatus.PROGRESSED);
			passEntity.setRemainingCount(random.nextInt(11));
			passEntity.setStartedAt(now.minusDays(60));
			passEntity.setEndedAt(now.minusDays(1));
			passEntities.add(passEntity);

		}
		passRepository.saveAll(passEntities);
	}
}
