package com.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.entity.FineRate;

public interface FineRateRepository extends JpaRepository<FineRate,Integer> {

	FineRate findByAccountType(String accountType);
}
