package com.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.project.entity.TotalTransaction;

public interface TotalTransactionRepository extends JpaRepository<TotalTransaction,Integer> {

	@Query("SELECT e FROM TotalTransaction e ORDER BY e.id DESC") // ASC or DESC for descending order
	List<TotalTransaction> findAllOrderedByTransactionDate();
	
}
