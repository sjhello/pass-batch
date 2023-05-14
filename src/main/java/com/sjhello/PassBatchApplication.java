package com.sjhello;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@RequiredArgsConstructor
@Slf4j
public class PassBatchApplication {

	private final JobBuilderFactory jobBuilderFactory;

	private final StepBuilderFactory stepBuilderFactory;

	@Bean
	public Step passStep() {
		return this.stepBuilderFactory.get("passStep")
			.tasklet((contribution, chunkContext) -> {
				log.info("passStep");
				return RepeatStatus.FINISHED;
			}).build();
	}

	@Bean
	public Job passJob() {
		return this.jobBuilderFactory.get("passJob")
			.start(passStep())
			.build();
	}

	public static void main(String[] args) {
		SpringApplication.run(PassBatchApplication.class, args);
	}

}
