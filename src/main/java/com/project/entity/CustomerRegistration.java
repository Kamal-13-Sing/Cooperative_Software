package com.project.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class CustomerRegistration {

	@Id
	private int registrationNumber;
	private String name;

	@Column(unique = true)
	private String email;

	@Column(unique = true)
	private String contact;

	private String address;
	private String gender;
	private String dob;

	@Column(unique = true)
	private String citizenshipNo;

	private String profession;
	private String maritalStatus;
	private String registrationDate;
	
	//------Nominee details----
	
	private String nomineeName;
	private String relation;
	private String nomineeAddress;
	
	@Column(unique = true)
	private String nomineeContact;
	
	//---Nominee details end----
	
	

	public int getRegistrationNumber() {
		return registrationNumber;
	}

	public String getNomineeName() {
		return nomineeName;
	}

	public String getProfession() {
		return profession;
	}

	public void setProfession(String profession) {
		this.profession = profession;
	}

	public void setNomineeName(String nomineeName) {
		this.nomineeName = nomineeName;
	}

	public String getRelation() {
		return relation;
	}

	public void setRelation(String relation) {
		this.relation = relation;
	}

	public String getNomineeAddress() {
		return nomineeAddress;
	}

	public void setNomineeAddress(String nomineeAddress) {
		this.nomineeAddress = nomineeAddress;
	}

	public String getNomineeContact() {
		return nomineeContact;
	}

	public void setNomineeContact(String nomineeContact) {
		this.nomineeContact = nomineeContact;
	}

	public void setRegistrationNumber(int registrationNumber) {
		this.registrationNumber = registrationNumber;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}

	public String getCitizenshipNo() {
		return citizenshipNo;
	}

	public void setCitizenshipNo(String citizenshipNo) {
		this.citizenshipNo = citizenshipNo;
	}

	

	public String getMaritalStatus() {
		return maritalStatus;
	}

	public void setMaritalStatus(String maritalStatus) {
		this.maritalStatus = maritalStatus;
	}

	public String getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(String registrationDate) {
		this.registrationDate = registrationDate;
	}

}
