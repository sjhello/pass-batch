package com.sjhello.passbatch.repository.pass;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

// 일치하지 않는 필드를 무시한다.
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PassModelMapper {
	PassModelMapper INSTANCE = Mappers.getMapper(PassModelMapper.class);

	// 필드명이 같지 않거나 custom하게 매핑해주기 위해서는 @Mapping을 추가해주면 됩니다.
	// BulkPassEntity -> PassEntity
	@Mapping(target = "status", qualifiedByName = "defaultStatus")
	@Mapping(target = "remainingCount", source = "bulkPassEntity.count")
	PassEntity toPassEntity(BulkPassEntity bulkPassEntity, String userId);

	// BulkPassStatus와 관계 없이 PassStatus값을 설정합니다.
	@Named("defaultStatus")
	default PassStatus status(BulkPassStatus bulkPassStatus) {
		return PassStatus.READY;
	}
}
