package com.sjhello.passbatch.job.pass;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;

import com.sjhello.passbatch.repository.pass.BulkPassEntity;
import com.sjhello.passbatch.repository.pass.BulkPassRepository;
import com.sjhello.passbatch.repository.pass.BulkPassStatus;
import com.sjhello.passbatch.repository.pass.PassEntity;
import com.sjhello.passbatch.repository.pass.PassRepository;
import com.sjhello.passbatch.repository.pass.PassStatus;
import com.sjhello.passbatch.repository.user.UserGroupMappingEntity;
import com.sjhello.passbatch.repository.user.UserGroupMappingRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ExtendWith(MockitoExtension.class)
class AddPassesTaskletTest {

	@Mock
	private StepContribution stepContribution;

	@Mock
	private ChunkContext chunkContext;

	@Mock
	private PassRepository passRepository;

	@Mock
	private BulkPassRepository bulkPassRepository;

	@Mock
	private UserGroupMappingRepository userGroupMappingRepository;

	// @Mock으로 생성된 객체를 주입한다
	@InjectMocks
	private AddPassesTasklet addPassesTasklet;

	@Test
	void test_execute() {
		// given
		final String userGroupId = "GROUP";
		final String userId = "A1000000";
		final Integer packageSeq = 1;
		final Integer count = 10;

		final LocalDateTime now = LocalDateTime.now();

		final BulkPassEntity bulkPassEntity = new BulkPassEntity();
		bulkPassEntity.setBulkPassSeq(1);
		bulkPassEntity.setPackageSeq(packageSeq);
		bulkPassEntity.setUserGroupId(userGroupId);
		bulkPassEntity.setStatus(BulkPassStatus.READY);
		bulkPassEntity.setCount(count);
		bulkPassEntity.setStartedAt(now);
		bulkPassEntity.setEndedAt(now.plusDays(60));

		final UserGroupMappingEntity userGroupMappingEntity = new UserGroupMappingEntity();
		userGroupMappingEntity.setUserGroupId(userGroupId);
		userGroupMappingEntity.setUserId(userId);

		// when
		when(bulkPassRepository.findByStatusAndStartedAtGreaterThan(eq(BulkPassStatus.READY), any()))
			.thenReturn(List.of(bulkPassEntity));
		when(userGroupMappingRepository.findByUserGroupId(eq(userGroupId)))
			.thenReturn(List.of(userGroupMappingEntity));
		RepeatStatus repeatStatus = addPassesTasklet.execute(stepContribution, chunkContext);

		// then 추가된 PassEntity 값을 확인한다.
		ArgumentCaptor<List> passEntitiesCaptor = ArgumentCaptor.forClass(List.class);
		verify(passRepository, times(1))
			.saveAll(passEntitiesCaptor.capture());

		List<PassEntity> passEntities = passEntitiesCaptor.getValue();
		PassEntity passEntity = passEntities.get(0);

		// then
		assertEquals(RepeatStatus.FINISHED, repeatStatus);
		assertEquals(passEntities.size(), 1);
		assertEquals(packageSeq, passEntity.getPackageSeq());
		assertEquals(userGroupId, passEntity.getUserId());
		assertEquals(PassStatus.READY, passEntity.getStatus());
		assertEquals(count, passEntity.getRemainingCount());
		assertEquals(now, passEntity.getStartedAt());
	}

}
