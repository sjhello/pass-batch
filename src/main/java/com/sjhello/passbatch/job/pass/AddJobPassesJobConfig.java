package com.sjhello.passbatch.job.pass;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class AddJobPassesJobConfig {

	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;
	private final AddPassesTasklet addPassesTasklet;

	@Bean
	public Job addPassesJob() {
		return jobBuilderFactory.get("addJobPasses")
			.start(addPassesStep())
			.build();
	}

	@Bean
	public Step addPassesStep() {
		return stepBuilderFactory.get("addJobStep")
			.tasklet(addPassesTasklet)
			.build();
	}
}
