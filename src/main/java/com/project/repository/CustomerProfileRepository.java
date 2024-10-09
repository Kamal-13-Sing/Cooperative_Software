package com.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.entity.CustomerProfile;
import com.project.entity.CustomerRegistration;

public interface CustomerProfileRepository extends JpaRepository<CustomerProfile,Integer> {

	CustomerProfile findByContact(String contact);
}
