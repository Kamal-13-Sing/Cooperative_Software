package com.project.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.project.entity.CustomerAccount;
import com.project.entity.CustomerProfile;
import com.project.entity.CustomerRegistration;
import com.project.entity.CustomerStatementSecond;
import com.project.entity.CustomerUserPassword;
import com.project.entity.TotalTransaction;
import com.project.repository.CustomerAccountRepository;
import com.project.repository.CustomerProfileRepository;
import com.project.repository.CustomerRegisterRepository;
import com.project.repository.CustomerStatementRepositorySecond;
import com.project.repository.CustomerUserPasswordRepository;
import com.project.repository.TotalTransactionRepository;

import jakarta.servlet.http.HttpSession;

@Service
public class CustomerRegisterServiceImp implements CustomerRegisterService {

	@Autowired
	private CustomerRegisterRepository crRepo;

	@Autowired
	private CustomerAccountRepository caRepo;

	@Autowired
	private TotalTransactionRepository ttRepo;

	@Autowired
	private CustomerUserPasswordRepository cupRepo;

	@Autowired
	private CustomerStatementRepositorySecond csRepoSec;

	@Autowired
	private CustomerProfileRepository cpRepo;

//-----------------Customer Registration ---------------------	
	@Override
	public CustomerRegistration saveCustomer(CustomerRegistration cr) {

		CustomerRegistration cSave = crRepo.save(cr);

		return cSave;
	}

	@Override
	public List<CustomerRegistration> getAllCustomer() {

		//List<CustomerRegistration> list = crRepo.findAll();
		List<CustomerRegistration> list = crRepo.findAllOrderedByRegistrationDate();
		

		return list;
	}

	@Override
	public CustomerRegistration getByIdCustomer(int id) {

		CustomerRegistration crById = crRepo.findById(id).get();

		return crById;
	}

	/*
	 * @Override public CustomerRegistration findByCustomerContact(String contact) {
	 * CustomerRegistration crByCt = crRepo.findByContact(contact);
	 * 
	 * return crByCt; }
	 */

	@Override
	public CustomerRegistration findByContact(String contact) {

		CustomerRegistration crByContact = crRepo.findByContact(contact);

		return crByContact;
	}

	@Override
	public boolean deleteCustomer(int id) {
		
		crRepo.deleteById(id);

		return true;
	}
	
	@Override
	public List<CustomerRegistration> findAllOrderedByRegistrationDate() {
		List<CustomerRegistration> crgetAll = crRepo.findAllOrderedByRegistrationDate();

		return crgetAll;
	}

//-------------Customer Account Adding----------------------

	@Override
	public CustomerAccount saveAccount(CustomerAccount ca) {

		CustomerAccount caSave = caRepo.save(ca);

		return caSave;
	}

	@Override
	public List<CustomerAccount> getAllAccount() {
		List<CustomerAccount> getAllAccount = caRepo.findAll();

		return getAllAccount;
	}

	@Override
	public CustomerAccount getByIdAccount(int id) {
		CustomerAccount caById = caRepo.findById(id).get();

		return caById;
	}

	@Override
	public boolean deleteAccount(int id) {
		caRepo.deleteById(id);

		return true;
	}

//------------------find By both----------------
	@Override
	public CustomerRegistration findByRegNoOrContact(int registrationNumber, String contact) {
		CustomerRegistration caByIdOrCt = crRepo.findByRegistrationNumberOrContact(registrationNumber, contact);

		return caByIdOrCt;
	}

//------------------Transaction history-----------------

	@Override
	public TotalTransaction saveTransaction(TotalTransaction tt) {

		TotalTransaction ttsave = ttRepo.save(tt);

		return ttsave;
	}

	@Override
	public List<TotalTransaction> getAllTransaction() {
		List<TotalTransaction> tt = ttRepo.findAll();

		return tt;
	}

	@Override
	public TotalTransaction getByAccountTransaction(int id) {
		
		TotalTransaction tt = ttRepo.findById(id).get();
		return tt;
	}

	@Override
	public boolean deleteTransaction(int id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<TotalTransaction> findAllOrderedByTransactionDate() {
		List<TotalTransaction> ttgetAll = ttRepo.findAllOrderedByTransactionDate();

		return ttgetAll;
	}

	public void removeSessionMessage() {
		HttpSession session = ((ServletRequestAttributes) (RequestContextHolder.getRequestAttributes())).getRequest()
				.getSession();

		session.removeAttribute("customersession");
	}

// -----------methods for Customer phone and password-------------

	@Override
	public CustomerUserPassword saveCustomerUserPass(CustomerUserPassword cup) {

		CustomerUserPassword cupSave = cupRepo.save(cup);

		return cupSave;
	}

	@Override
	public List<CustomerUserPassword> getAllCustomerUserPassword() {

		List<CustomerUserPassword> cupGetAll = cupRepo.findAll();

		return cupGetAll;
	}

	@Override
	public CustomerUserPassword getByIdCustomerUserPassword(int id) {

		return null;
	}

	@Override
	public boolean deleteCustomerUserPassword(int id) {

		cupRepo.deleteById(id);
		
		return true;
	}

	@Override
	public CustomerUserPassword findByPhoneNo(String phoneNo) {
		CustomerUserPassword findByContact = cupRepo.findByPhoneNo(phoneNo);

		return findByContact;
	}



// --------------Customer Statement Second---------------------
	@Override
	public CustomerStatementSecond saveCustomerStatementSecond(CustomerStatementSecond cs) {

		CustomerStatementSecond csSave = csRepoSec.save(cs);

		return csSave;
	}

	@Override
	public List<CustomerStatementSecond> getAllCustomerStatementSecond() {

		List<CustomerStatementSecond> csAll = csRepoSec.findAll();

		return csAll;
	}

	@Override
	public List<CustomerStatementSecond> findAllOrderedByDateSecond() {
		List<CustomerStatementSecond> csAllByDate = csRepoSec.findAllOrderedByDate();

		return csAllByDate;
	}

	/*
	 * @Override public CustomerStatementSecond
	 * findBySenderNameOrReceiverNameSecond(String senderName, String receiverName)
	 * { // TODO Auto-generated method stub return null; }
	 */

	@Override
	public CustomerStatementSecond getByIdCustomerStatementSecond(int id) {
		CustomerStatementSecond csGetById = csRepoSec.findById(id).get();

		return csGetById;
	}

	// ---------methods for Customer Profile ----------------
	@Override
	public CustomerProfile saveCustomerProfile(CustomerProfile cp) {

		CustomerProfile cpSave = cpRepo.save(cp);

		return cpSave;
	}

	@Override
	public CustomerProfile findByProfileContact(String contact) {
		CustomerProfile cpFind = cpRepo.findByContact(contact);

		return cpFind;
	}

	@Override
	public List<CustomerProfile> getAllCustomerProfile() {
		List<CustomerProfile> cpAll = cpRepo.findAll();

		return cpAll;
	}

	@Override
	public boolean deleteCustomerProfile(int id) {
		cpRepo.deleteById(id);
		
		return true;
	}
	
	//-----------email integration-------------
	

	
	//----------email integeration end---------------

}
