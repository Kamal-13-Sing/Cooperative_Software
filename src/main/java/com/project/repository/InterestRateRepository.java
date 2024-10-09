package com.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.entity.InterestRate;

public interface InterestRateRepository extends JpaRepository<InterestRate,Integer> {

	InterestRate findBySavingsAccounts(String savingsAccounts);
}
