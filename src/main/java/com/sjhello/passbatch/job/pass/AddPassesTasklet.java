package com.sjhello.passbatch.job.pass;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import com.sjhello.passbatch.repository.pass.BulkPassEntity;
import com.sjhello.passbatch.repository.pass.BulkPassRepository;
import com.sjhello.passbatch.repository.pass.BulkPassStatus;
import com.sjhello.passbatch.repository.pass.PassEntity;
import com.sjhello.passbatch.repository.pass.PassModelMapper;
import com.sjhello.passbatch.repository.pass.PassRepository;
import com.sjhello.passbatch.repository.user.UserGroupMappingEntity;
import com.sjhello.passbatch.repository.user.UserGroupMappingRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class AddPassesTasklet implements Tasklet {

	private final PassRepository passRepository;
	private final BulkPassRepository bulkPassRepository;
	private final UserGroupMappingRepository userGroupMappingRepository;

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
		// 이용권 시작 일시 1일 전 user group 내 각 사용자에게 이용권을 추가해줍니다.
		final LocalDateTime startedAt = LocalDateTime.now().minusDays(1);
		List<BulkPassEntity> bulkPassEntities = bulkPassRepository.findByStatusAndStartedAtGreaterThan(
			BulkPassStatus.READY, startedAt);

		int count = 0;
		for (BulkPassEntity bulkPassEntity : bulkPassEntities) {
			// user group에 속한 userId들을 조회합니다.
			List<String> userIds = userGroupMappingRepository.findByUserGroupId(bulkPassEntity.getUserGroupId())
				.stream()
				.map(UserGroupMappingEntity::getUserGroupId)
				.collect(Collectors.toList());

			// 각 userId로 이용권을 추가합니다.
			count += addPasses(bulkPassEntity, userIds);

			// pass 추가 이후 상태를 COMPLETED로 업데이트합니다.
			bulkPassEntity.setStatus(BulkPassStatus.COMPLETED);
		}
		log.info("AddPassesTasklet - execute: 이용권 {}건 추가 완료, startedAt={}", count, startedAt);
		return RepeatStatus.FINISHED;
	}

	private int addPasses(BulkPassEntity bulkPassEntity, List<String> userIds) {
		List<PassEntity> passEntities = new ArrayList<>();
		for (String userId : userIds) {
			PassEntity passEntity = PassModelMapper.INSTANCE.toPassEntity(bulkPassEntity, userId);
			passEntities.add(passEntity);
		}
		return passRepository.saveAll(passEntities).size();
	}

}
