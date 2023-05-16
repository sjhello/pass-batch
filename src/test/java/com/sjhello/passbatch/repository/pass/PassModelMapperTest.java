package com.sjhello.passbatch.repository.pass;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class PassModelMapperTest {

	@Test
	void test_toPassEntity() {
		// given
		LocalDateTime now = LocalDateTime.now();
		String userId = "A10000000";

		BulkPassEntity bulkPassEntity = new BulkPassEntity();
		bulkPassEntity.setPackageSeq(1);
		bulkPassEntity.setUserGroupId("GROUP");
		bulkPassEntity.setStatus(BulkPassStatus.COMPLETED);
		bulkPassEntity.setCount(10);
		bulkPassEntity.setStartedAt(now.minusDays(10));
		bulkPassEntity.setEndedAt(now);

		// when
		PassEntity passEntity = PassModelMapper.INSTANCE.toPassEntity(bulkPassEntity, userId);

		// then
		assertEquals(1, passEntity.getPackageSeq());
		assertEquals(userId, passEntity.getUserId());
		assertEquals(passEntity.getStatus(), PassStatus.READY);
	}
}
