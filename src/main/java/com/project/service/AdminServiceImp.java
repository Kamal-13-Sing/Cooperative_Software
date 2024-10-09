package com.project.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.project.entity.AdminRegister;
import com.project.entity.FineRate;
import com.project.entity.InterestRate;
import com.project.repository.CrudRepository;
import com.project.repository.FineRateRepository;
import com.project.repository.InterestRateRepository;

import jakarta.servlet.http.HttpSession;

@Service
public class AdminServiceImp implements AdminService {

	@Autowired
	private CrudRepository crudRepo;

	@Autowired
	private InterestRateRepository irRepo;
	
	@Autowired
	private FineRateRepository frRepo;

	@Override
	public AdminRegister save(AdminRegister ar) {

		AdminRegister arsave = crudRepo.save(ar);

		return arsave;
	}

	@Override
	public List<AdminRegister> getAll() {

		List<AdminRegister> list = crudRepo.findAll();

		return list;
	}

	@Override
	public AdminRegister getById(int id) {

		return null;
	}

	@Override
	public boolean delete(int id) {

		return false;
	}

	public void removeSessionMessage() {
		HttpSession session = ((ServletRequestAttributes) (RequestContextHolder.getRequestAttributes())).getRequest()
				.getSession();

		session.removeAttribute("msg");
	}

	public String call() {
		return "text call 2";
	}

	// ------------interest rate----------------
	@Override
	public InterestRate saveInterest(InterestRate ir) {

		InterestRate irSave = irRepo.save(ir);

		return irSave;
	}

	@Override
	public InterestRate findByAcType(String acType) {
		InterestRate irbyAc = irRepo.findBySavingsAccounts(acType);

		return irbyAc;
	}

	// ------------Fine rate----------------
	@Override
	public FineRate saveFine(FineRate fr) {
		FineRate frSave = frRepo.save(fr);
		
		return frSave;
	}

	@Override
	public FineRate getByFineId(int id) {
		FineRate frGet = frRepo.findById(id).get();
		
		return frGet;
	}

	@Override
	public List<FineRate> getAllFineRate() {
		List<FineRate> frAll = frRepo.findAll();
		
		return frAll;
	}

	@Override
	public FineRate findByAccountType(String acNType) {
		FineRate frByType = frRepo.findByAccountType(acNType);
		
		return frByType;
	}

}
