package com.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.project.entity.CustomerRegistration;

public interface CustomerRegisterRepository extends JpaRepository<CustomerRegistration, Integer> {

	//CustomerRegistration findByContact(String contact);
	
	CustomerRegistration findByRegistrationNumberOrContact(int registrationNumber, String contact);
	
	CustomerRegistration findByContact(String contact);
	
	@Query("SELECT e FROM CustomerRegistration e ORDER BY e.registrationDate DESC") // ASC or DESC for descending order
	List<CustomerRegistration> findAllOrderedByRegistrationDate();
}
