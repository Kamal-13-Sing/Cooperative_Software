package com.project.service;

import java.util.List;

import com.project.entity.CustomerAccount;
import com.project.entity.CustomerProfile;
import com.project.entity.CustomerRegistration;
import com.project.entity.CustomerStatementSecond;
import com.project.entity.CustomerUserPassword;
import com.project.entity.TotalTransaction;

public interface CustomerRegisterService {

//-----------method for Register Costumer---------------
	public CustomerRegistration saveCustomer(CustomerRegistration cr);

	public List<CustomerRegistration> getAllCustomer();

	public CustomerRegistration getByIdCustomer(int id);

	public CustomerRegistration findByRegNoOrContact(int registrationNumber, String contact);

	public CustomerRegistration findByContact(String contact);

	public boolean deleteCustomer(int id);
	
	public List<CustomerRegistration> findAllOrderedByRegistrationDate();

//----------method for Adding Customer Account------------

	public CustomerAccount saveAccount(CustomerAccount ca);

	public List<CustomerAccount> getAllAccount();

	public CustomerAccount getByIdAccount(int id);

	public boolean deleteAccount(int id);

//----------methods for Total Transaction---------------------

	public TotalTransaction saveTransaction(TotalTransaction tt);

	public List<TotalTransaction> getAllTransaction();

	public TotalTransaction getByAccountTransaction(int id);

	public boolean deleteTransaction(int id);

	public List<TotalTransaction> findAllOrderedByTransactionDate();

//-----------methods for Customer phone and password-------------

	public CustomerUserPassword saveCustomerUserPass(CustomerUserPassword cup);

	public List<CustomerUserPassword> getAllCustomerUserPassword();

	public CustomerUserPassword getByIdCustomerUserPassword(int id);

	public boolean deleteCustomerUserPassword(int id);

	public CustomerUserPassword findByPhoneNo(String phoneNo);



// ----------methods for Customer StatementsSecond ------------------

	public CustomerStatementSecond saveCustomerStatementSecond(CustomerStatementSecond cs);

	public CustomerStatementSecond getByIdCustomerStatementSecond(int id);

	public List<CustomerStatementSecond> getAllCustomerStatementSecond();

	//public CustomerStatement findBySenderNameOrReceiverName(String senderName, String receiverName);

	public List<CustomerStatementSecond> findAllOrderedByDateSecond();

//---------methods for Customer Profile ----------------

	public CustomerProfile saveCustomerProfile(CustomerProfile cp);

	public CustomerProfile findByProfileContact(String contact);

	public List<CustomerProfile> getAllCustomerProfile();
	public boolean deleteCustomerProfile(int id);

}
