package com.project.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class FineRate {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	private String accountType;
	
	private double month1;
	private double month2;
	private double month3;
	private double month4;
	private double month5;
	private double month6;
	private double month7;
	private double month8;
	private double month9;
	private double month10;
	private double month11;
	private double month12;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getAccountType() {
		return accountType;
	}
	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}
	public double getMonth1() {
		return month1;
	}
	public void setMonth1(double month1) {
		this.month1 = month1;
	}
	public double getMonth2() {
		return month2;
	}
	public void setMonth2(double month2) {
		this.month2 = month2;
	}
	public double getMonth3() {
		return month3;
	}
	public void setMonth3(double month3) {
		this.month3 = month3;
	}
	public double getMonth4() {
		return month4;
	}
	public void setMonth4(double month4) {
		this.month4 = month4;
	}
	public double getMonth5() {
		return month5;
	}
	public void setMonth5(double month5) {
		this.month5 = month5;
	}
	public double getMonth6() {
		return month6;
	}
	public void setMonth6(double month6) {
		this.month6 = month6;
	}
	public double getMonth7() {
		return month7;
	}
	public void setMonth7(double month7) {
		this.month7 = month7;
	}
	public double getMonth8() {
		return month8;
	}
	public void setMonth8(double month8) {
		this.month8 = month8;
	}
	public double getMonth9() {
		return month9;
	}
	public void setMonth9(double month9) {
		this.month9 = month9;
	}
	public double getMonth10() {
		return month10;
	}
	public void setMonth10(double month10) {
		this.month10 = month10;
	}
	public double getMonth11() {
		return month11;
	}
	public void setMonth11(double month11) {
		this.month11 = month11;
	}
	public double getMonth12() {
		return month12;
	}
	public void setMonth12(double month12) {
		this.month12 = month12;
	}
	
	


	
	
}
