package com.sjhello.passbatch.job.pass;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

import javax.persistence.EntityManagerFactory;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sjhello.passbatch.repository.pass.PassEntity;
import com.sjhello.passbatch.repository.pass.PassStatus;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class ExpirePassesJobConfig {

	private final int CHUNK_SIZE = 5;

	// @EnableBatchProcessing에서 제공
	private final JobBuilderFactory jobBuilderFactory;

	private final StepBuilderFactory stepBuilderFactory;

	private final EntityManagerFactory managerFactory;

	@Bean
	public Job expirePassesJob() {
		return jobBuilderFactory.get("expirePassesJob")
			.start(expirePassesStep())
			.build();
	}

	@Bean
	public Step expirePassesStep() {
		return stepBuilderFactory.get("expirePassesStep")
			.<PassEntity, PassEntity>chunk(CHUNK_SIZE)
			.reader(expirePassesItemReader())
			.processor(expirePassesItemProcessor())
			.writer(expirePassesItemWriter())
			.build();
	}

	/*
	* JpaPagingItemReader만 지원하다가 Spring 4.3 에서 추가됨
	* 데이터를 읽는다
	* */
	@Bean
	@StepScope
	public JpaCursorItemReader<PassEntity> expirePassesItemReader() {
		return new JpaCursorItemReaderBuilder<PassEntity>()
			.name("expirePassesItemReader")
			.entityManagerFactory(managerFactory)
			.queryString("select p from PassEntity p where p.status = :status and p.endedAt <= :endedAt")
			.parameterValues(Map.of("status", PassStatus.PROGRESSED,
									"endedAt", LocalDateTime.now()))
			.build();
	}

	/*
	* 읽어온 데이터를 처리한다
	* */
	@Bean
	public ItemProcessor<PassEntity, PassEntity> expirePassesItemProcessor() {
		return passEntity -> {
			// 이용권의 상태변경, 이용권 만료 기간 변경
			passEntity.setStatus(PassStatus.EXPIRED);
			passEntity.setExpiredAt(LocalDateTime.now());
			return passEntity;
		};
	}

	/*
	* 처리한 데이터를 영속화 한다
	* */
	@Bean
	public JpaItemWriter<PassEntity> expirePassesItemWriter() {
		return new JpaItemWriterBuilder<PassEntity>()
			.entityManagerFactory(managerFactory)
			.build();
	}
}
