package com.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.entity.CustomerUserPassword;

public interface CustomerUserPasswordRepository extends JpaRepository<CustomerUserPassword, Integer> {

	
	CustomerUserPassword findByPhoneNo(String phoneNo);
}
