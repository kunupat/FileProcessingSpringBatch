package com.test.spring.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;
import org.springframework.batch.item.json.JsonFileItemWriter;
import org.springframework.batch.item.json.builder.JsonFileItemWriterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import com.test.spring.batch.listeners.JobCompletionNotificationListener;
import com.test.spring.batch.models.Customer;
import com.test.spring.batch.processors.CustomerItemProcessor;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;

	@Autowired
	private ApplicationArguments applicationArguments;

	@Bean
	public FlatFileItemReader<Customer> reader() {

		String path = System.getProperty("user.home") + "/data/out/";
		String[] sourceArgs = applicationArguments.getSourceArgs();
		String fileName = null;
		for(String arg : sourceArgs)
			fileName = arg;

		return new FlatFileItemReaderBuilder<Customer>().name("personItemReader")
				.resource(new FileSystemResource(path + fileName)).delimited()
				.names(new String[] { "id", "name", "age" }).fieldSetMapper(new BeanWrapperFieldSetMapper<Customer>() {
					{
						setTargetType(Customer.class);
					}
				}).build();
	}

	@Bean
	public CustomerItemProcessor processor() {
		return new CustomerItemProcessor();
	}

	/*
	 * @Bean public JdbcBatchItemWriter<Customer> writer(DataSource dataSource) {
	 * return new JdbcBatchItemWriterBuilder<Customer>()
	 * .itemSqlParameterSourceProvider(new
	 * BeanPropertyItemSqlParameterSourceProvider<>())
	 * .sql("INSERT INTO customers (cust_id, cust_name, cust_age) VALUES (:id, :name, :age)"
	 * ) .dataSource(dataSource) .build(); }
	 */

	@Bean
	public JsonFileItemWriter<Customer> jsonFileItemWriter() {

		String outputPath = System.getProperty("user.home") + "/data/out/jsons/";
		
		String[] sourceArgs = applicationArguments.getSourceArgs();
		String fileName = "";
		for(String arg : sourceArgs)
			fileName = arg;
		
		String fileNameJson = fileName.replace(".csv", ".json");

		return new JsonFileItemWriterBuilder<Customer>().jsonObjectMarshaller(new JacksonJsonObjectMarshaller<>())
				.resource(new FileSystemResource(outputPath + fileNameJson)).name("CustomerJsonFileItemWriter").build();
	}

	@Bean
	public Job importUserJob(JobCompletionNotificationListener listener, Step exportToJsonStep) {
		return jobBuilderFactory.get("FileProcessorJob").incrementer(new RunIdIncrementer()).listener(listener)
				.flow(exportToJsonStep).end().build();
	}

	/*
	 * @Bean public Step step1(JdbcBatchItemWriter<Customer> writer) { return
	 * stepBuilderFactory.get("step1") .<Customer, Customer> chunk(10)
	 * .reader(reader()) .processor(processor()) .writer(writer) .build(); }
	 */

	@Bean
	public Step exportToJsonStep() {
		return stepBuilderFactory.get("exportToJsonStep").<Customer, Customer>chunk(10).reader(reader())
				.writer(jsonFileItemWriter()).build();
	}
}