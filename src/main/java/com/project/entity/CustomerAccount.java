package com.project.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class CustomerAccount {

	@Id
	private int accountNo;
	private double balance;
	private String accountType;
	private String Status;
	private double savingAmount;
	private int depositCount;
	private int fineMonthCount;
	private double accruedInterest;
	
	

	public double getAccruedInterest() {
		return accruedInterest;
	}

	public void setAccruedInterest(double accruedInterest) {
		this.accruedInterest = accruedInterest;
	}

	public int getFineMonthCount() {
		return fineMonthCount;
	}

	public void setFineMonthCount(int fineMonthCount) {
		this.fineMonthCount = fineMonthCount;
	}

	public int getDepositCount() {
		return depositCount;
	}

	public void setDepositCount(int depositCount) {
		this.depositCount = depositCount;
	}

	public double getSavingAmount() {
		return savingAmount;
	}

	public void setSavingAmount(double savingAmount) {
		this.savingAmount = savingAmount;
	}

	public int getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(int accountNo) {
		this.accountNo = accountNo;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	public String getStatus() {
		return Status;
	}

	public void setStatus(String status) {
		Status = status;
	}

}
