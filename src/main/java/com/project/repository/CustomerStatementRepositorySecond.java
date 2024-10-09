package com.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.project.entity.CustomerStatementSecond;

public interface CustomerStatementRepositorySecond extends JpaRepository<CustomerStatementSecond, Integer> {

	//CustomerStatement findBySenderNameOrReceiverName(String senderName, String receiverName);
	

	
	@Query("SELECT e FROM CustomerStatementSecond e ORDER BY e.id DESC") // ASC or DESC for descending order
	List<CustomerStatementSecond> findAllOrderedByDate();
}
