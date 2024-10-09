package com.project.service;

import java.util.List;

import com.project.entity.AdminRegister;
import com.project.entity.FineRate;
import com.project.entity.InterestRate;

public interface AdminService {

	public AdminRegister save(AdminRegister ar);

	public List<AdminRegister> getAll();

	public AdminRegister getById(int id);

	public boolean delete(int id);

//------------Interest Rate---------------

	public InterestRate saveInterest(InterestRate ir);

	public InterestRate findByAcType(String acType);

// ------------Fine Rate---------------

	public FineRate saveFine(FineRate fr);
	
	public FineRate getByFineId(int id);
	
	public List<FineRate> getAllFineRate();
	
	public FineRate findByAccountType(String acNType);


}
