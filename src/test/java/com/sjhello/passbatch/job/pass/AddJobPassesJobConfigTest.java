package com.sjhello.passbatch.job.pass;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import net.bytebuddy.utility.RandomString;

import com.sjhello.passbatch.config.TestBatchConfig;
import com.sjhello.passbatch.repository.pass.BulkPassRepository;
import com.sjhello.passbatch.repository.user.UserGroupMappingRepository;

@SpringBootTest
@SpringBatchTest
@ActiveProfiles("test")
@ContextConfiguration(classes = {AddJobPassesJobConfig.class, AddPassesTasklet.class, TestBatchConfig.class})
class AddJobPassesJobConfigTest {

	@Autowired
	private JobLauncherTestUtils jobLauncherTestUtils;

	@Autowired
	private BulkPassRepository bulkPassRepository;

	@Autowired
	private UserGroupMappingRepository userGroupMappingRepository;

	@Test
	void 이용권_지급_Step_테스트() {

	}

	private void addBulkPassEntity() {
		LocalDateTime localDateTime = LocalDateTime.now();
		String userGroupId = RandomStringUtils.randomAlphabetic(6);
		String userIdPrefix = "A000";
		String userId = userIdPrefix + RandomStringUtils.randomNumeric(4);


	}
}
