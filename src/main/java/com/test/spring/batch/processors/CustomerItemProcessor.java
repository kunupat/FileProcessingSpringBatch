package com.test.spring.batch.processors;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import com.test.spring.batch.models.Customer;

public class CustomerItemProcessor implements ItemProcessor<Customer, Customer> {

	private static final Logger log = LoggerFactory.getLogger(CustomerItemProcessor.class);
	
	@Override
	public Customer process(final Customer customer) throws Exception {
		
		final String name = customer.getName().toUpperCase();
	   
	    final Customer transformedPerson = new Customer(customer.getId(), name, customer.getAge());

	    log.info("Converting (" + customer + ") into (" + transformedPerson + ")");

	    return transformedPerson;
	}

}
