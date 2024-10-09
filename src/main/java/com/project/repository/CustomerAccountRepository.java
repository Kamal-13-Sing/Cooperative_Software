package com.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.entity.CustomerAccount;
import com.project.entity.CustomerProfile;

public interface CustomerAccountRepository extends JpaRepository<CustomerAccount, Integer>{

	
}
