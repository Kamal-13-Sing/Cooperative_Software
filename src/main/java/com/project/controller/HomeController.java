package com.project.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.project.entity.AdminRegister;
import com.project.entity.CustomerAccount;
import com.project.entity.CustomerProfile;
import com.project.entity.CustomerRegistration;
import com.project.entity.CustomerStatementSecond;
import com.project.entity.CustomerUserPassword;
import com.project.entity.FineRate;
import com.project.entity.InterestRate;
import com.project.entity.TotalTransaction;
import com.project.service.AdminService;
import com.project.service.CustomerRegisterServiceImp;

import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

@Controller
public class HomeController {

	@Autowired
	private AdminService adminService;

	@Autowired
	private CustomerRegisterServiceImp crsImp;

	@Autowired
	private JavaMailSender javaMailSender;

//---------Home page Mapping-----------------

	@GetMapping("/")
	public String home() {

		return "home";
	}

//-----------Admin Register Mapping-------------

	@GetMapping("/adminRegister")
	public String adminRegister() {

		return "adminRegister";
	}

	@PostMapping("/adminRegister")
	public String adminR(@ModelAttribute AdminRegister ar, HttpSession session) {

		System.out.println("Email: " + ar.getEmail());
		System.out.println("Password: " + ar.getPassword());

		AdminRegister asave = adminService.save(ar);

		if (ar != null) {
			// session.setAttribute("msg", "Register Success");
		}

		return "redirect:/adminRegister";
	}

//-----------------admin Login Mapping---------------

	@GetMapping("/adminLogin")
	public String adminLogin() {

		return "adminLogin";
	}

	@PostMapping("/adminFormLogin")
	public String adminFormLogin(@ModelAttribute AdminRegister ar, HttpSession session, RedirectAttributes ra) {

		String email = ar.getEmail();
		String password = ar.getPassword();

		// ---------fetch data from DB------------

		List<AdminRegister> list = adminService.getAll();

		for (AdminRegister a : list) {
			/*
			 * System.out.println("Id: "+a.getId());
			 * System.out.println("Name: "+a.getEmail());
			 * System.out.println("Password: "+a.getPassword());
			 */
			if (a.getEmail().equals(email) && a.getPassword().equals(password)) {

				session.setAttribute("msg", " admin Login session SucessFull");
				return "redirect:/adminDashboard";
			} else {
				// session.setAttribute("error", "Email or Password may be wrong..!!");
				ra.addFlashAttribute("error", "Email or Password may be wrong..!!");
				return "redirect:/adminLogin";
			}

		}

		return "redirect:/adminLogin";
	}

//--------------admin dashboard mapping-----------------

	@GetMapping("/adminDashboard")
	public String adminDashboard() {

		return "adminDashboard";
	}

//-------------------admin Logout mapping----------------

	@GetMapping("/adminLogout")
	public String logOut(HttpSession session) {

		return "adminLogout";
	}
//---------------customer Login mapping----------------

	@GetMapping("/customerLogout")
	public String customerLogout(HttpSession session) {

		return "customerLogout";
	}

//---------------Customer Registration Mapping---------------------

	@GetMapping("/customerRegister")
	public String customerRegister(Model m, RedirectAttributes ra) {

		Random random = new Random();
		int accountNumber = random.nextInt(100000000);

		int length = String.valueOf(accountNumber).length();

		if (length == 8) {
			m.addAttribute("acNo", accountNumber);

		}

		// ----------transfer date time to the registration page----------------
		LocalDateTime currentDateTime = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
		String formattedDateTime = currentDateTime.format(formatter);

		m.addAttribute("realdatetime", formattedDateTime);

		return "customerRegister";
	}

//-------------------register new Customer----------------------------

	@PostMapping("/registerCustomer")
	public String registerCustomer(@ModelAttribute CustomerRegistration cr, RedirectAttributes ra) {

		System.out.println("Rid: " + cr.getRegistrationNumber());
		System.out.println("Rid: " + cr.getName());
		System.out.println("Rid: " + cr.getDob());
		System.out.println("Rid: " + cr.getGender());

		int rNo = cr.getRegistrationNumber();

		CustomerRegistration crSave = crsImp.saveCustomer(cr);

		if (crSave != null) {

			ra.addFlashAttribute("rNo", rNo);
			return "redirect:/customerAccountAdd";
		}

		return "redirect:/customerAccountAdd";
	}

//---------------Add Customer Account----------

	@GetMapping("/customerAccountAdd")
	public String customerAccountAdd() {

		return "customerAccountAdd";
	}

//------------add Customer Account------------

	@PostMapping("/addCustomer")
	public String addCustomer(@ModelAttribute CustomerAccount ca) {

		System.out.println("Ac No: " + ca.getAccountNo());
		System.out.println("Ac Type: " + ca.getAccountType());
		System.out.println("Ac Balance: " + ca.getBalance());
		System.out.println("Ac Status: " + ca.getStatus());
		System.out.println("SavingAmt: " + ca.getSavingAmount());
		System.out.println("Deposit count: " + ca.getDepositCount());
		System.out.println("FineMonth: " + ca.getFineMonthCount());

		CustomerAccount caSave = crsImp.saveAccount(ca);

		if (caSave != null) {
			return "redirect:/adminDashboard";
		}

		return "redirect:/customerAccountAdd";
	}

//----------Customer Login Mappint--------------------

	@GetMapping("/customerLogin")
	public String customerLogin() {

		return "customerLogin";
	}

//------------------totalCustomer-----------------------

	@GetMapping("/totalCustomer")
	public String totalCustomer(Model m) {

		// List<CustomerRegistration> list = crsImp.getAllCustomer();
		List<CustomerRegistration> list = crsImp.findAllOrderedByRegistrationDate();

		int count = 0;
		m.addAttribute("crObj", list);
		for (CustomerRegistration ca : list) {
			System.out.println("Ac No: " + ca.getRegistrationNumber());
			System.out.println("Citiz No: " + ca.getCitizenshipNo());

			count++;
		}
		m.addAttribute("count", count);
		System.out.println("Total customer : " + count);

		return "totalCustomer";
	}

// ----------------search Customer By Account No-----------------

	@PostMapping("searchByAc")
	public String searchByAc(Model m, @ModelAttribute CustomerRegistration cr, RedirectAttributes rda) {

		// -----------Customer Registration Details Given By Admin-------------------

		int crById = cr.getRegistrationNumber();
		String crByct = cr.getContact();

		// ----------Total Customer details for validation-------------
		List<CustomerRegistration> list = crsImp.getAllCustomer();
		int checkData = 0;

		for (CustomerRegistration ca : list) {
			System.out.println("Ac No: " + ca.getRegistrationNumber());
			System.out.println("Citi No: " + ca.getName());
			// ----------------check if given data is in database or not---------------
			if (crByct.equals(ca.getContact()) || crById == ca.getRegistrationNumber()) {
				checkData = 1;
				break;
			}
		}
		if (checkData == 1) {
			System.out.println("=====Data Found=======");

			System.out.println("ACNo: " + crById);
			System.out.println("contact: " + crByct);

			CustomerRegistration crByIdOrCt = crsImp.findByRegNoOrContact(crById, crByct);

			System.out.println("AccountNo: " + crByIdOrCt.getRegistrationNumber());
			System.out.println("Phone: " + crByIdOrCt.getContact());
			System.out.println("Name: " + crByIdOrCt.getName());

			int crCt = crByIdOrCt.getRegistrationNumber();

			rda.addFlashAttribute("obj", crByIdOrCt);

			// ---------Customer Account Details---------------------------

			CustomerAccount caId = crsImp.getByIdAccount(crCt);
			System.out.println("balance: " + caId.getBalance());
			System.out.println("Ac No: " + caId.getAccountNo());

			System.out.println("FineMonthCount: " + caId.getFineMonthCount());

			int fineMonth = caId.getFineMonthCount();
			rda.addFlashAttribute("caObj", caId);

			System.out.println("--------------A------------------");

			// --------fetch image from database--------------

			CustomerProfile cpProfile = crsImp.findByProfileContact(crByIdOrCt.getContact());

			// System.out.println("ImageName: "+cpProfile.getImageName());

			if (cpProfile == null) {
				// m.addAttribute("check", "failed");
				rda.addFlashAttribute("check", "failed");
				System.out.println("Failed Image");
				// m.addAttribute("cpObj", cpProfile);
				// return "redirect:/customerDetails";

			} else {

				rda.addFlashAttribute("check", "pass");
				rda.addFlashAttribute("cpObj", cpProfile);
				// m.addAttribute("check", "pass");
				// m.addAttribute("cpObj", cpProfile);
				System.out.println("Pass Image: " + cpProfile.getImageName());
			}

			// ---------send fine rate and amount as per monthly saving amount------------

			FineRate fr = adminService.findByAccountType("regularmonthlysaving");
			double savingAmount = caId.getSavingAmount();
			System.out.println("saving amount: " + savingAmount);

			double fineRate = 0;
			double fineAmount = 0;

			switch (fineMonth) {

			case -1:

				System.out.println("fine Month count: " + fineAmount);
				rda.addFlashAttribute("fineRate", "0");

				rda.addFlashAttribute("fineMonth", "0");

				// cfineRate = cfr.getMonth12();

				fineAmount = 0;

				rda.addFlashAttribute("fineAmount", fineAmount);

				break;

			case 0:

				System.out.println("fine Month count: " + fineMonth);
				rda.addFlashAttribute("fineMonth", fineMonth);
				rda.addFlashAttribute("fineRate", "0");

				fineAmount = 0;

				rda.addFlashAttribute("fineAmount", fineAmount);

				break;

			case 1:

				System.out.println("fine Month count: " + fineMonth);
				rda.addFlashAttribute("fineMonth", fineMonth);
				rda.addFlashAttribute("fineRate", fr.getMonth1());
				fineRate = fr.getMonth1();

				fineAmount = (fineRate / 100) * savingAmount;

				rda.addFlashAttribute("fineAmount", fineAmount);

				break;

			case 2:

				System.out.println("fine Month count: " + fineMonth);
				rda.addFlashAttribute("fineMonth", fineMonth);
				rda.addFlashAttribute("fineRate", fr.getMonth2());

				fineRate = fr.getMonth2();

				fineAmount = (fineRate / 100) * savingAmount;

				rda.addFlashAttribute("fineAmount", fineAmount);
				break;

			case 3:

				System.out.println("fine Month count: " + fineMonth);
				rda.addFlashAttribute("fineMonth", fineMonth);
				rda.addFlashAttribute("fineRate", fr.getMonth3());

				System.out.println("fineRate: " + fr.getMonth3());

				fineRate = fr.getMonth3();

				fineAmount = (fineRate / 100) * savingAmount;
				System.out.println("fineAmount: " + fineAmount);

				rda.addFlashAttribute("fineAmount", fineAmount);
				break;

			case 4:

				System.out.println("fine Month count: " + fineMonth);
				rda.addFlashAttribute("fineMonth", fineMonth);
				System.out.println("fine Rate for Month: " + fr.getMonth4());
				rda.addFlashAttribute("fineMonth", fr.getMonth4());

				fineRate = fr.getMonth4();

				fineAmount = (fineRate / 100) * savingAmount;
				System.out.println("fineAmount: " + fineAmount);

				double limitedFineAmount = Math.round(fineAmount * 10.0) / 10.0; // Rounds to one decimal place
				System.out.println("Limited fine amount: " + limitedFineAmount); // Output: Limited fine amount:
																					// 350.0

				rda.addFlashAttribute("fineAmount", limitedFineAmount);
				break;

			case 5:

				System.out.println("fine Month count: " + fineMonth);
				rda.addFlashAttribute("fineMonth", fineMonth);
				rda.addFlashAttribute("fineRate", fr.getMonth5());

				fineRate = fr.getMonth5();

				fineAmount = (fineRate / 100) * savingAmount;

				rda.addFlashAttribute("fineAmount", fineAmount);

				break;

			case 6:

				System.out.println("fine Month count: " + fineMonth);
				rda.addFlashAttribute("fineMonth", fineMonth);
				rda.addFlashAttribute("fineRate", fr.getMonth6());

				fineRate = fr.getMonth6();

				fineAmount = (fineRate / 100) * savingAmount;

				rda.addFlashAttribute("fineAmount", fineAmount);
				break;

			case 7:

				System.out.println("fine Month count: " + fineMonth);
				rda.addFlashAttribute("fineMonth", fineMonth);
				rda.addFlashAttribute("fineRate", fr.getMonth7());

				fineRate = fr.getMonth7();

				fineAmount = (fineRate / 100) * savingAmount;

				rda.addFlashAttribute("fineAmount", fineAmount);
				break;

			case 8:

				System.out.println("fine Month count: " + fineMonth);
				rda.addFlashAttribute("fineMonth", fineMonth);
				rda.addFlashAttribute("fineRate", fr.getMonth8());

				fineRate = fr.getMonth8();

				fineAmount = (fineRate / 100) * savingAmount;

				rda.addFlashAttribute("fineAmount", fineAmount);
				break;

			case 9:

				System.out.println("fine Month count: " + fineMonth);
				rda.addFlashAttribute("fineMonth", fineMonth);
				rda.addFlashAttribute("fineRate", fr.getMonth9());

				fineRate = fr.getMonth9();

				fineAmount = (fineRate / 100) * savingAmount;

				rda.addFlashAttribute("fineAmount", fineAmount);
				break;

			case 10:

				System.out.println("fine Month count: " + fineMonth);
				rda.addFlashAttribute("fineMonth", fineMonth);
				rda.addFlashAttribute("fineRate", fr.getMonth10());

				fineRate = fr.getMonth10();

				fineAmount = (fineRate / 100) * savingAmount;

				rda.addFlashAttribute("fineAmount", fineAmount);
				break;

			case 11:

				System.out.println("fine Month count: " + fineMonth);
				rda.addFlashAttribute("fineMonth", fineMonth);
				rda.addFlashAttribute("fineRate", fr.getMonth11());

				fineRate = fr.getMonth11();

				fineAmount = (fineRate / 100) * savingAmount;

				rda.addFlashAttribute("fineAmount", fineAmount);
				break;

			case 12:

				System.out.println("fine Month count: " + fineMonth);
				rda.addFlashAttribute("fineMonth", fineMonth);
				rda.addFlashAttribute("fineRate", fr.getMonth12());

				fineRate = fr.getMonth12();

				fineAmount = (fineRate / 100) * savingAmount;

				if (fineAmount <= 0) {
					rda.addFlashAttribute("fineAmount", 0);
				} else {
					rda.addFlashAttribute("fineAmount", fineAmount);
				}

			}

			return "redirect:/customerDetails";

			// return "redirect:/customerDetails";

		} else {
			System.out.println("=====Data Not Found=======");
			rda.addFlashAttribute("error", "Information doesn't match...!!!");
			return "redirect:/customerDetails";
		}

	}

//-----------------customerDetails------------------------------

	@GetMapping("/customerDetails")
	public String customerDetails() {

		return "customerDetails";
	}

//--------------Customer account Lock and active--------------

	@PostMapping("/lockactive")
	public String lockactive(@ModelAttribute CustomerAccount ca) {

		String caStatus = ca.getStatus();
		int caNo = ca.getAccountNo();

		System.out.println("AcNo: " + caNo);
		System.out.println("AccountStatus: " + caStatus);

		CustomerAccount caGet = crsImp.getByIdAccount(caNo);
		System.out.println("GEt all By Account Id==========");

		System.out.println("balance: " + caGet.getBalance());
		System.out.println("Ac No: " + caGet.getAccountNo());
		System.out.println("AcType: " + caGet.getAccountType());
		System.out.println("AcStatus: " + caGet.getStatus());
		System.out.println("FineMonth: " + caGet.getFineMonthCount());

		caGet.setStatus(caStatus);

		crsImp.saveAccount(caGet);

		// ca.setAccountType(caType);

		// crsImp.saveAccount(ca);

		return "redirect:/customerDetails";
	}
//--------------------Deposit/Withdarw Amount-----------------------------

	@GetMapping("/depositwithdrawAmount")
	public String depositwithdrawAmount(Model m) {

		LocalDateTime currentDateTime = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss a");
		String formattedDateTime = currentDateTime.format(formatter);

		m.addAttribute("realdatetime", formattedDateTime);

		String sAcNo = String.valueOf(m.getAttribute("acNo"));
		String tType = String.valueOf(m.getAttribute("tType"));

		System.out.println("CustomerAcNo :+ " + sAcNo);

		if (sAcNo == "null") {
			System.out.println("AcNo is Null");

		} else {

			System.out.println("AcNo is Not null");

			int acNo = Integer.parseInt(sAcNo);
			System.out.println("Customer AcID: " + sAcNo);

			System.out.println("Redirect AcNo: " + acNo);

			// ---------------getting redirected fine amount of customer-----------
			String fAmt = String.valueOf(m.getAttribute("fAmt"));
			double cFineAmount = Double.parseDouble(fAmt);

			String fMonth = String.valueOf(m.getAttribute("fMonth"));
			double cFineMonth = Double.parseDouble(fMonth);
			// int cFineMonth = Integer.parseInt(fMonth);

			System.out.println("Fine Month: " + cFineMonth);

			System.out.println("Customer Fine Amount: " + cFineAmount);

			// ---------------getting redirected saving amount of customer-----------
			String sAmt = String.valueOf(m.getAttribute("sAmt"));
			double cSavingAmount = Double.parseDouble(sAmt);

			System.out.println("Customer Saving Amount: " + cSavingAmount);
			// int add = 1;
			// int newCFineMonth = cFineMonth ;

			System.out.println("saving amount with running month: " + cFineMonth);
			double totalDepositAmount = cFineAmount + (cSavingAmount * cFineMonth);
			System.out.println("Customer Total deposit amount:" + totalDepositAmount);

			m.addAttribute("depositacNo", sAcNo);
			m.addAttribute("depositAmount", totalDepositAmount);
			m.addAttribute("fineAmt", cFineAmount);
			m.addAttribute("tType", tType);

			return "depositwithdrawAmount";
		}

		/*
		 * String message = (sAcNo.equals("null")) ? "x is null" : "x is not Null";
		 * System.out.println(message);
		 */

		// System.out.println("##@@@@@######@@@@");

		return "depositwithdrawAmount";
	}

	@PostMapping("/processTransaction")
	public String processTransaction(@ModelAttribute TotalTransaction tt, RedirectAttributes ra,
			@RequestParam("fAmt") String fAmt) {

		LocalDateTime currentDateTime = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss a");
		String formattedDateTime = currentDateTime.format(formatter);

		int givenAcNo = tt.getAccountNo();
		double givenAmount = tt.getAmount();
		String transactionType = tt.getTransactionType();

		double savingAmount = 0;

		System.out.println("FineAmountCollection: " + fAmt);
		if (fAmt == "") {
			System.out.println("FineAC is Empty");
		} else {
			double fineAmt = Double.parseDouble(fAmt);
			savingAmount = givenAmount - fineAmt;
			givenAmount = savingAmount;
			System.out.println("final deposited to customer: " + givenAmount);
			System.out.println("FineAC is Not Empty");
		}
		// --------------save given deposit and and withdraw----------------

		System.out.println("Ac: " + tt.getAccountNo());
		System.out.println("Amount: " + tt.getAmount());
		System.out.println("TransactionType: " + tt.getTransactionType());

		// --------fetch and update the given account information----------
		System.out.println("------------getting all up------");
		List<CustomerAccount> caAll = crsImp.getAllAccount();

		int check = 0;
		for (CustomerAccount caa : caAll) {
			System.out.println("AcNo: " + caa.getAccountNo());
			System.out.println("Balance: " + caa.getBalance());
			if (givenAcNo == caa.getAccountNo()) {
				check = 1;
				break;
			}

		}
		System.out.println("------------getting all up------");

		// ------------check == 1 mean account exist----------

		if (check == 1) {

			CustomerAccount caAc = crsImp.getByIdAccount(givenAcNo);
			System.out.println("Balance in Ac: " + caAc.getBalance());

			double prevBalance = caAc.getBalance();
			String acStatus = caAc.getStatus();

			System.out.println("Account Status: " + acStatus);

			// -----------fetch deposit count -----------------
			CustomerAccount caGet = crsImp.getByIdAccount(givenAcNo);
			int depositCount = caGet.getDepositCount();
			System.out.println("Deposit count: " + depositCount);

			// --------------check account Status----------------------

			if (acStatus.equals("active")) {

				double newBalance = 0;

				if (transactionType.equals("deposit")) {

					if (0 == 0) {

						// ------operations for deposit------------
						// System.out.println("###ADd######");
						newBalance = givenAmount + prevBalance;

						// ---------save in total transactioin------------
						crsImp.saveTransaction(tt);
						caAc.setBalance(newBalance);
						CustomerAccount updateBalance = crsImp.saveAccount(caAc);

						// --------save in customer statement second----------

						CustomerStatementSecond css = new CustomerStatementSecond();
						css.setAccountNo(givenAcNo);
						css.setAmount(givenAmount);
						css.setDate(formattedDateTime);
						css.setSource("cooperative");
						css.setTransactionType(transactionType);

						crsImp.saveCustomerStatementSecond(css);

						if (updateBalance != null) {
							System.out.println("Updated");

						} else {

							System.out.println("Failed to update");
						}

						// -------change deposit count after deposited---

						CustomerAccount caByAcNo = crsImp.getByIdAccount(givenAcNo);
						System.out.println("deposit count: " + caByAcNo.getDepositCount());

						// int depositCount = caByAcNo.getDepositCount();
						caByAcNo.setDepositCount(1);
						caByAcNo.setFineMonthCount(-1);
						crsImp.saveAccount(caGet);

						// ---------emaildeposit notification when deposit start---------

						// System.out.println("Session contact: " + pNo);

						CustomerRegistration csAc = crsImp.getByIdCustomer(givenAcNo);

						System.out.println("Name: " + csAc.getName());
						System.out.println("email: " + csAc.getEmail());

						String cemail = csAc.getEmail();

						// ---------email integration----------------

//						Random random = new Random();
//						int otpNumber = random.nextInt(10000000);
//						String rotp = String.valueOf(otpNumber); 

						try {

							MimeMessage msg = javaMailSender.createMimeMessage();
							MimeMessageHelper helper = new MimeMessageHelper(msg);

							helper.setFrom("nakfarke6@gmail.com");
							helper.setTo(cemail);
							helper.setSubject("DHAROHAR_ALERT");
							helper.setText("Your " + givenAcNo + " has been Credited by NPR " + givenAmount + " on "
									+ formattedDateTime + ", Remarks: Cash Deposit");

							javaMailSender.send(msg);

						} catch (Exception ex) {
							ex.printStackTrace();
						}

						// ---------email integration end---------------

						// --------emial notification end----------------------
						ra.addFlashAttribute("success", " Deposit of " + givenAmount + " was successful");

						return "redirect:/depositwithdrawAmount";

					} else {

						ra.addFlashAttribute("dcountCheck", "Already deposited this month..!!");
						return "redirect:/depositwithdrawAmount";
					}

				} else {

					if (prevBalance >= givenAmount) {

						newBalance = prevBalance - givenAmount;

						// ---------save in total transactioin------------
						crsImp.saveTransaction(tt);
						caAc.setBalance(newBalance);
						CustomerAccount updateBalance = crsImp.saveAccount(caAc);

						// --------save in customer statement second----------

						CustomerStatementSecond css = new CustomerStatementSecond();
						css.setAccountNo(givenAcNo);
						css.setAmount(givenAmount);
						css.setDate(formattedDateTime);
						css.setSource("cooperative");
						css.setTransactionType(transactionType);

						crsImp.saveCustomerStatementSecond(css);

						if (updateBalance != null) {
							System.out.println("Updated");

						} else {

							System.out.println("Failed to update");
						}

						// ---------email withdraw notification when deposit start---------

						// System.out.println("Session contact: " + pNo);

						CustomerRegistration csAc = crsImp.getByIdCustomer(givenAcNo);

						System.out.println("Name: " + csAc.getName());
						System.out.println("email: " + csAc.getEmail());

						String cemail = csAc.getEmail();

						// ---------email integration----------------

//						Random random = new Random();
//						int otpNumber = random.nextInt(10000000);
//						String rotp = String.valueOf(otpNumber); 

						try {

							MimeMessage msg = javaMailSender.createMimeMessage();
							MimeMessageHelper helper = new MimeMessageHelper(msg);

							helper.setFrom("nakfarke6@gmail.com");
							helper.setTo(cemail);
							helper.setSubject("DHAROHAR_ALERT");
							helper.setText("Your " + givenAcNo + " has been Debited by NPR " + givenAmount + " on "
									+ formattedDateTime + ", Remarks: Cash Withdraw");

							javaMailSender.send(msg);

						} catch (Exception ex) {
							ex.printStackTrace();
						}

						// ---------email integration end---------------

						// --------emial notification end----------------------

						ra.addFlashAttribute("success", " Withdarw of " + givenAmount + " was successful");
						return "redirect:/depositwithdrawAmount";

					} else {

						ra.addFlashAttribute("balanceCheck", "Insufficient balance...!!!");
						return "redirect:/depositwithdrawAmount";
					}

				}

			} else {
				ra.addFlashAttribute("acCheck", "Account status is Lock...!!!");
				return "redirect:/depositwithdrawAmount";

			}
		} else {

			ra.addFlashAttribute("acCheck", "Account does not exist...!!!");
			return "redirect:/depositwithdrawAmount";
		}

		// return "redirect:/depositwithdrawAmount";
	}

//---------------totalTransaction--------------------------

	@GetMapping("totalTransaction")
	public String totalTransaction(Model m) {

		// List<TotalTransaction> tt = crsImp.getAllTransaction();
		List<TotalTransaction> tt = crsImp.findAllOrderedByTransactionDate();

		for (TotalTransaction ttr : tt) {
			System.out.println("Amount: " + ttr.getAmount());
		}

		m.addAttribute("tt", tt);

		return "totalTransaction";
	}

//--------------------Customer Side Work------------------------------------

	@GetMapping("/newUser")
	public String newUser() {

		return "newUser";
	}

//--------mapping customer password setup----------------

	@PostMapping("/newUserVerify")
	public String customerPasswordSetup(Model m, @ModelAttribute CustomerRegistration cr, RedirectAttributes ra,
			HttpSession session) {

		// ---------user given value----------------------
		int rNo = cr.getRegistrationNumber();
		String pNo = cr.getContact();

		System.out.println("RNo: " + rNo);
		System.out.println("PNo: " + pNo);

		// ------------fetch value from database-----------------------

		int checkAccount = 0;
		List<CustomerRegistration> crAll = crsImp.getAllCustomer();

		for (CustomerRegistration c : crAll) {

			if (c.getRegistrationNumber() == rNo && c.getContact().equals(pNo)) {
				checkAccount = 1;
				break;
			}
		}

		// -------check account already activate or not for new customer-------------

		int checkActivateOrNot = 0;

		List<CustomerUserPassword> cupAll = crsImp.getAllCustomerUserPassword();

		for (CustomerUserPassword cAll : cupAll) {

			System.out.println("Customer password: " + cAll.getPassword());
			System.out.println("Customer pin: " + cAll.getTransactionPin());
			System.out.println("Customer PhoneNo: " + cAll.getPhoneNo());
			if (pNo.equals(cAll.getPhoneNo())) {
				checkActivateOrNot = 1;
				break;
			}

		}

		if (checkActivateOrNot == 1) {
			System.out.println("Account already Activated");
		} else {

			System.out.println("Account is not Activated");
		}

		String pno = pNo;
		if (checkAccount == 1) {

			// ------condition for activate or not customer Account-------

			if (checkActivateOrNot == 1) {

				ra.addFlashAttribute("error", "Account already activated..!!");
				return "redirect:/newUser";
			} else {

				System.out.println("---Data Found---");
				session.setAttribute("customersession", "customersessionset: " + pno);
				// session.setAttribute("rno", rno);

				ra.addFlashAttribute("pNo", pno);
				ra.addAttribute("phone", pno);

				// return "redirect:/customerPasswordSetup";
				return "redirect:/customerActivationOTP";
			}

		} else {
			System.out.println("---Data Not Found---");
			ra.addFlashAttribute("error", "Information doesnot match...!!!");
			return "redirect:/newUser";
		}

	}

	// --------customer account activation otp

	@GetMapping("/customerActivationOTP")
	public String activationOTP(HttpSession session, @RequestParam("phone") String pNo, Model m,
			RedirectAttributes ra) {

		// -----------otp generation-----------

		// String sessionContact = (String) session.getAttribute("customersession");

		System.out.println("Session contact: " + pNo);

		CustomerRegistration csAc = crsImp.findByContact(pNo);

		System.out.println("Name: " + csAc.getName());
		System.out.println("email: " + csAc.getEmail());

		String cemail = csAc.getEmail();

		// ---------email integration----------------

		Random random = new Random();
		int otpNumber = random.nextInt(10000000);
		String rotp = String.valueOf(otpNumber);

		try {

			MimeMessage msg = javaMailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(msg);

			helper.setFrom("nakfarke6@gmail.com");
			helper.setTo(cemail);
			helper.setSubject("OTP For Account Activation");
			helper.setText(rotp);

			javaMailSender.send(msg);

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		// ---------email integration end---------------

		// int otp = 876936;

		m.addAttribute("otp", otpNumber);
		m.addAttribute("phoneNo", pNo);

		return "customerActivationOTP";
	}

	@PostMapping("/otpsetup")
	public String otpsetup(RedirectAttributes ra, @RequestParam(name = "otp") String otp,
			@RequestParam(name = "confirmotp") String confirmotp, @RequestParam(name = "phone") String phone) {

		String cpno = phone;
		String cotp = otp;
		String cconfirmotp = confirmotp;

		System.out.println("Phone: " + cpno);
		System.out.println("otp: " + cotp);

		ra.addAttribute("phone", phone);
		// ra.addAttribute("password", pass);

		return "redirect:/customerPasswordSetup";
	}

	// ---------customer password setup-----------------

	@GetMapping("/customerPasswordSetup")
	public String customerPasswordSetup(@RequestParam("phone") String pNo, Model m, RedirectAttributes ra) {

		// m.addAttribute("rgno","rNo");
		System.out.println("pNo: " + pNo);

		m.addAttribute("phoneNo", pNo);

		// ra.addAttribute(Phone, ra)

		return "customerPasswordSetup";
	}

	@PostMapping("/passwordsetup")
	public String passwordsetup(@ModelAttribute CustomerUserPassword cup, RedirectAttributes ra) {

		String pno = cup.getPhoneNo();
		String pass = cup.getPassword();

		System.out.println("Phone: " + pno);
		System.out.println("pass: " + pass);

		ra.addAttribute("phone", pno);
		ra.addAttribute("password", pass);
		return "redirect:/customerTransactionPin";
	}

//---------customer transaction pin setup-------------

	@GetMapping("/customerTransactionPin")
	public String customerTransactionPin(@RequestParam("phone") String pno, Model m,
			@RequestParam("password") String pass) {

		BCryptPasswordEncoder bc = new BCryptPasswordEncoder();
		String givenPassword = pass;
		String encryptedPassword = bc.encode(givenPassword);

		System.out.println("Normal: " + pass);
		System.out.println("Encrypted: " + encryptedPassword);

		// ------let's match given password-------

		/*
		 * String givenPass = "kamal098";
		 * 
		 * boolean isMatch = bc.matches(givenPass, enPass);
		 * 
		 * if(isMatch) { System.out.println("Match"); }else {
		 * System.out.println("Not Macth"); }
		 */

		m.addAttribute("phoneNo", pno);
		m.addAttribute("password", encryptedPassword);
		return "customerTransactionPin";
	}

	@PostMapping("/transactionpinsetup")
	public String transactionpinsetup(@ModelAttribute CustomerUserPassword cup, RedirectAttributes ra) {

		String pno = cup.getPhoneNo();
		String pin = cup.getTransactionPin();

		System.out.println("Phone: " + pno);
		System.out.println("pin: " + pin);

		CustomerUserPassword cupSave = crsImp.saveCustomerUserPass(cup);

		// ra.addAttribute("phone", pno);
		return "redirect:/customerLogout";
	}

//-----------Customer Login mapping------------------

	@PostMapping("/verifiedCustomerLogin")
	public String verifiedCustomerLogin(@ModelAttribute CustomerUserPassword cup, HttpSession session,
			RedirectAttributes ra) {

		String givenPno = cup.getPhoneNo();
		String givenPass = cup.getPassword();

		System.out.println("Phone: " + givenPno);
		System.out.println("Password: " + givenPass);

		int checkUserPass = 0;
		List<CustomerUserPassword> cupAll = crsImp.getAllCustomerUserPassword();

		for (CustomerUserPassword cp : cupAll) {

			System.out.println("------------------");

			System.out.println("Phone: " + cp.getPhoneNo());
			System.out.println("Password: " + cp.getPassword());

			// -----------check user password---------------
			// String givenPass = "kamal098";
			BCryptPasswordEncoder bc2 = new BCryptPasswordEncoder();
			boolean isMatch = bc2.matches(givenPass, cp.getPassword());
			int test = 0;

			if (isMatch) {
				System.out.println("Match");
				test = 1;
			} else {
				System.out.println("Not Macth");
				test = 0;
			}

			if (givenPno.equals(cp.getPhoneNo()) && test == 1) {

				checkUserPass = 1;

				break;
			}
		}

		if (checkUserPass == 1) {

			System.out.println("Valid user");

			// ----------fetch customer account number using phoneNo -----------------

			CustomerRegistration crNo = crsImp.findByContact(givenPno);
			int customerAcNo = crNo.getRegistrationNumber();
			System.out.println("LoginAcNo: " + customerAcNo);

			// --------fetch Customer Account status----------------

			CustomerAccount caAcNo = crsImp.getByIdAccount(customerAcNo);
			String cStatus = caAcNo.getStatus();
			System.out.println("Account Status: " + cStatus);

			// -------check Login Account status--------------------

			if (cStatus.equals("active")) {

				session.setAttribute("customersession", givenPno);

				// ra.addAttribute("phone", pno);
				return "redirect:/customerProfile";

			} else {

				ra.addFlashAttribute("error", "Your Account is Locked..!!");
				return "redirect:/customerLogin";

			}

		} else {

			System.out.println("Invalid User");

			ra.addFlashAttribute("error", "Incorrect number or password");
			return "redirect:/customerLogin";
		}

	}

//-------mapping customer profile-------------

	// ---------customer profile page--------------
	@GetMapping("/customerProfile")
	public String customerProfile(HttpSession session, Model m) {

		// m.addAttribute("rgno","rNo"); System.out.println("RegNo: "+pNo);

		String pno = (String) session.getAttribute("customersession");
		System.out.println(pno);
		LocalDateTime currentDateTime = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss a");
		String formattedDateTime = currentDateTime.format(formatter);
		System.out.println("Date Time: " + formattedDateTime);

		return "customerProfile";
	}

	// ------------csutomer Account Information-----------

	@GetMapping("/customerAccountDetail")
	public String customerAccountDetail(Model m, HttpSession session, RedirectAttributes ra) {
		// m.addAttribute("phoneNo", pNo);

		String sessionContact = (String) session.getAttribute("customersession");
		System.out.println("Session: " + sessionContact);

		if (sessionContact != null) {

			// ----------fetch data from Customer Registration-------------

			CustomerRegistration crByContact = crsImp.findByContact(sessionContact);
			System.out.println("Account Holder Name: " + crByContact.getName());
			System.out.println("Account No: " + crByContact.getRegistrationNumber());
			int crAcc = crByContact.getRegistrationNumber();

			// --------fetch data from Customer Account---------------------

			CustomerAccount caByAcc = crsImp.getByIdAccount(crAcc);

			System.out.println("balance: " + caByAcc.getBalance());

			System.out.println("C Fine: " + caByAcc.getFineMonthCount());
			int fineMonth = caByAcc.getFineMonthCount();

			double interestAmount = caByAcc.getAccruedInterest();

			// --------fetch image from database--------------

			CustomerProfile cpProfile = crsImp.findByProfileContact(sessionContact);

			// -----------fetch interest rate and interest amount------------

			InterestRate ir = adminService.findByAcType("regularmonthlysaving");

			System.out.println("Interest Rate: " + ir.getRate());

			// System.out.println("ImageName: "+cpProfile.getImageName());

			if (crByContact != null && caByAcc != null) {

				// --interest

				m.addAttribute("crObj", crByContact);
				m.addAttribute("caObj", caByAcc);
				m.addAttribute("interestRate", ir.getRate());
				m.addAttribute("interestAmount", interestAmount);
				double value = caByAcc.getBalance();

				// Create a DecimalFormat object with the desired pattern
				DecimalFormat df = new DecimalFormat("#.##");

				// Format the value using the DecimalFormat object
				String formattedValue = df.format(value);

				System.out.println("@Acount balance: " + formattedValue);
				m.addAttribute("balance", formattedValue);
				// m.addAttribute("cfine", caByAcc.getFineMonthCount());

				if (cpProfile == null) {
					m.addAttribute("check", "failed");
					System.out.println("Failed Image");
					// m.addAttribute("cpObj", cpProfile);
				} else {
					m.addAttribute("check", "pass");
					m.addAttribute("cpObj", cpProfile);
					System.out.println("Pass Image: " + cpProfile.getImageName());
				}

				// ---------send fine rate and amount as per monthly saving amount------------

				FineRate fr = adminService.findByAccountType("regularmonthlysaving");
				double savingAmount = caByAcc.getSavingAmount();
				System.out.println("saving amount: " + savingAmount);

				double fineRate = 0;
				double fineAmount = 0;
				switch (fineMonth) {

				case -1:

					System.out.println("fine Month count: " + fineAmount);
					m.addAttribute("fineRate", "0");
					m.addAttribute("cfine", caByAcc.getFineMonthCount() + 1);

					// cfineRate = cfr.getMonth12();

					fineAmount = 0;

					m.addAttribute("fineAmount", fineAmount);

					break;
				case 0:
					System.out.println("fine Month count: " + fineMonth);
					m.addAttribute("fineRate", "0");
					m.addAttribute("cfine", caByAcc.getFineMonthCount());

					// cfineRate = cfr.getMonth12();

					fineAmount = 0;

					m.addAttribute("fineAmount", fineAmount);

					break;

				case 1:

					System.out.println("fine Month count: " + fineMonth);
					m.addAttribute("fineRate", fr.getMonth1());
					m.addAttribute("cfine", caByAcc.getFineMonthCount());
					fineRate = fr.getMonth1();

					fineAmount = (fineRate / 100) * savingAmount;

					m.addAttribute("fineAmount", fineAmount);

					break;

				case 2:

					System.out.println("fine Month count: " + fineMonth);
					m.addAttribute("fineRate", fr.getMonth2());
					m.addAttribute("cfine", caByAcc.getFineMonthCount());

					fineRate = fr.getMonth2();

					fineAmount = (fineRate / 100) * savingAmount;

					m.addAttribute("fineAmount", fineAmount);
					break;

				case 3:

					System.out.println("fine Month count: " + fineMonth);
					m.addAttribute("fineRate", fr.getMonth3());
					m.addAttribute("cfine", caByAcc.getFineMonthCount());

					fineRate = fr.getMonth3();

					fineAmount = (fineRate / 100) * savingAmount;

					m.addAttribute("fineAmount", fineAmount);
					break;

				case 4:

					System.out.println("fine Month count: " + fineMonth);
					System.out.println("fine Rate for Month: " + fr.getMonth4());
					m.addAttribute("cfine", caByAcc.getFineMonthCount());
					m.addAttribute("fineRate", fr.getMonth4());

					fineRate = fr.getMonth4();

					fineAmount = (fineRate / 100) * savingAmount;

					m.addAttribute("fineAmount", fineAmount);
					break;

				case 5:

					System.out.println("fine Month count: " + fineMonth);
					m.addAttribute("fineRate", fr.getMonth5());
					m.addAttribute("cfine", caByAcc.getFineMonthCount());

					fineRate = fr.getMonth5();

					fineAmount = (fineRate / 100) * savingAmount;

					m.addAttribute("fineAmount", fineAmount);

					break;

				case 6:

					System.out.println("fine Month count: " + fineMonth);
					m.addAttribute("fineRate", fr.getMonth6());
					m.addAttribute("cfine", caByAcc.getFineMonthCount());

					fineRate = fr.getMonth6();

					fineAmount = (fineRate / 100) * savingAmount;

					m.addAttribute("fineAmount", fineAmount);
					break;

				case 7:

					System.out.println("fine Month count: " + fineMonth);
					m.addAttribute("fineRate", fr.getMonth7());
					m.addAttribute("cfine", caByAcc.getFineMonthCount());

					fineRate = fr.getMonth7();

					fineAmount = (fineRate / 100) * savingAmount;

					m.addAttribute("fineAmount", fineAmount);
					break;

				case 8:

					System.out.println("fine Month count: " + fineMonth);
					m.addAttribute("fineRate", fr.getMonth8());
					m.addAttribute("cfine", caByAcc.getFineMonthCount());

					fineRate = fr.getMonth8();

					fineAmount = (fineRate / 100) * savingAmount;

					m.addAttribute("fineAmount", fineAmount);
					break;

				case 9:

					System.out.println("fine Month count: " + fineMonth);
					m.addAttribute("fineRate", fr.getMonth9());
					m.addAttribute("cfine", caByAcc.getFineMonthCount());

					fineRate = fr.getMonth9();

					fineAmount = (fineRate / 100) * savingAmount;

					m.addAttribute("fineAmount", fineAmount);
					break;

				case 10:

					System.out.println("fine Month count: " + fineMonth);
					m.addAttribute("fineRate", fr.getMonth10());
					m.addAttribute("cfine", caByAcc.getFineMonthCount());

					fineRate = fr.getMonth10();

					fineAmount = (fineRate / 100) * savingAmount;

					m.addAttribute("fineAmount", fineAmount);
					break;

				case 11:

					System.out.println("fine Month count: " + fineMonth);
					m.addAttribute("fineRate", fr.getMonth11());
					m.addAttribute("cfine", caByAcc.getFineMonthCount());

					fineRate = fr.getMonth11();

					fineAmount = (fineRate / 100) * savingAmount;

					m.addAttribute("fineAmount", fineAmount);
					break;

				case 12:

					System.out.println("fine Month count: " + fineMonth);
					m.addAttribute("fineRate", fr.getMonth12());
					m.addAttribute("cfine", caByAcc.getFineMonthCount());

					fineRate = fr.getMonth12();

					fineAmount = (fineRate / 100) * savingAmount;

					if (fineAmount <= 0) {
						m.addAttribute("fineAmount", 0);
					} else {
						m.addAttribute("fineAmount", fineAmount);
					}

				}

				return "customerAccountDetail";

			} else {
				return "redirect:/customerLogout";
			}

		} else {

			// -----if session is null the redirect to logout page-------

			return "redirect:/customerLogout";
		}

	}

//------------customer profile and personal information Edit----------------

	@GetMapping("/editProfile")
	public String editProfile(HttpSession session, Model m) {
		String sessionContact = (String) session.getAttribute("customersession");
		System.out.println("SessionForEditProfile: " + sessionContact);
		m.addAttribute("sContact", sessionContact);

		List<CustomerProfile> list = crsImp.getAllCustomerProfile();

		m.addAttribute("list", list);

		return "editProfile";
	}

	@PostMapping("/imageupload")
	public String imageupload(@RequestParam MultipartFile img, HttpSession session, Model m) {

		String sessionContact = (String) session.getAttribute("customersession");
		System.out.println("SessionForEditProfile: " + sessionContact);
		System.out.println("ImageName: " + img.getOriginalFilename());

		// -----------fetch info of prfile page to know account exist or not----

		List<CustomerProfile> cpAll = crsImp.getAllCustomerProfile();
		int checkProfile = 0;

		for (CustomerProfile cp : cpAll) {
			System.out.println("ImageId: " + cp.getId());
			if (cp.getContact().equals(sessionContact)) {
				checkProfile = 1;
				break;
			}

		}

		// ------fetch info of given contact-----------

		if (checkProfile == 0) {

			CustomerProfile cpNew = new CustomerProfile();
			cpNew.setContact(sessionContact);
			cpNew.setImageName(img.getOriginalFilename());

			CustomerProfile imgSave = crsImp.saveCustomerProfile(cpNew);
			if (imgSave != null) {
				try {

					File saveFile = new ClassPathResource("static/dimg").getFile();

					Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + img.getOriginalFilename());

					System.out.println("Path: " + path);

					Files.copy(img.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		} else {

			CustomerProfile cpFind = crsImp.findByProfileContact(sessionContact);

			System.out.println("Contact: " + cpFind.getContact());
			System.out.println("ImageName: " + cpFind.getImageName());

			// cpFind.setContact(sessionContact);
			cpFind.setImageName(img.getOriginalFilename());

			CustomerProfile imgSave = crsImp.saveCustomerProfile(cpFind);

			if (imgSave != null) {
				try {

					File saveFile = new ClassPathResource("static/dimg").getFile();

					Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + img.getOriginalFilename());

					System.out.println("Path: " + path);

					Files.copy(img.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		return "redirect:/customerAccountDetail";
	}

//---------------Fund Transfer Get Mapping---------------------

	@GetMapping("/fundTransfer")
	public String fundTransfer(Model m, HttpSession session) {

		LocalDateTime currentDateTime = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss a");
		String formattedDateTime = currentDateTime.format(formatter);
		System.out.println("Date--: " + formattedDateTime);

		m.addAttribute("realdatetime", formattedDateTime);

		String sessionContact = (String) session.getAttribute("customersession");
		System.out.println("Session: " + sessionContact);

		if (sessionContact != null) {

			// ----------fetch data from Customer Registration-------------

			CustomerRegistration crByContact = crsImp.findByContact(sessionContact);
			System.out.println("Account Holder Name: " + crByContact.getName());
			System.out.println("Account No: " + crByContact.getRegistrationNumber());
			int crAcc = crByContact.getRegistrationNumber();

			// --------fetch data from Customer Account---------------------

			CustomerAccount caByAcc = crsImp.getByIdAccount(crAcc);

			System.out.println("balance: " + caByAcc.getBalance());

			if (crByContact != null && caByAcc != null) {
				m.addAttribute("crObj", crByContact);
				m.addAttribute("caObj", caByAcc);

				return "fundTransfer";

			} else {
				return "redirect:/customerLogout";
			}

		} else {

			// -----if session is null the redirect to logout page-------

			return "redirect:/customerLogout";
		}

	}

//--------transferFund Post Mapping-----------------

	@PostMapping("/transferFund")
	public String transferFund(@RequestParam String pin, @ModelAttribute CustomerAccount ca, HttpSession session,
			RedirectAttributes ra, @RequestParam String remarks, String transactionDate, Model m) {

		// -----------get reat date time---------------
		LocalDateTime currentDateTime = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss a");
		String formattedDateTime = currentDateTime.format(formatter);
		System.out.println("Date--: " + formattedDateTime);
		m.addAttribute("realdatetime", formattedDateTime);

		// -------receiver's Account and amount to transfer----------
		int givenAcNo = ca.getAccountNo();
		double givenAmnt = ca.getBalance();
		String givenPin = pin;

		// -------given transaction information-----------
		System.out.println("###########" + remarks);
		System.out.println("###########" + transactionDate);

		System.out.println("Receiver's AcNo: " + givenAcNo);
		System.out.println("Transfer Amnt: " + givenAmnt);
		System.out.println(" entered Transaction pin: " + pin);

		String sessionContact = (String) session.getAttribute("customersession");
		System.out.println("Sender Number: " + sessionContact);

		// ----------fetch data from sender -------------

		CustomerRegistration crByContact = crsImp.findByContact(sessionContact);
		System.out.println("Sender Name: " + crByContact.getName());
		System.out.println("sender Account No: " + crByContact.getRegistrationNumber());
		int senderAcNo = crByContact.getRegistrationNumber();

		// --------fetch sender transaction pin-----------------

		CustomerUserPassword cupPin = crsImp.findByPhoneNo(sessionContact);
		String senderPin = cupPin.getTransactionPin();
		System.out.println("Sender Transaction Pin: " + cupPin.getTransactionPin());

		// ------------fetch sender balance----------------------

		CustomerAccount senderByAc = crsImp.getByIdAccount(senderAcNo);
		System.out.println("Sender Current Balance: " + senderByAc.getBalance());
		double senderCurrentBalance = senderByAc.getBalance();

		// --------fetch the receiver's account information----------

		System.out.println("------------getting all up------");
		List<CustomerAccount> caAll = crsImp.getAllAccount();
		int checkAcExist = 0;

		for (CustomerAccount cae : caAll) {

			System.out.println("####print all account#####");
			System.out.println("AcNo: " + cae.getAccountNo());
			System.out.println("Balance: " + cae.getBalance());

			// --------check before fetch receiver's Account Info----------

			if (givenAcNo == cae.getAccountNo()) {

				checkAcExist = 1;
				break;
			}

		}

		// ----------- Execute process if given account is valid----------------

		if (givenAcNo != senderAcNo) {

			if (checkAcExist == 1) {
				System.out.println("Receiver's account is exist");

				// ------------fetch receiver's balance----------------------

				CustomerAccount receiverByAc = crsImp.getByIdAccount(givenAcNo);
				System.out.println("Receiver's Current Balance: " + receiverByAc.getBalance());
				double receiverCurrentBalance = receiverByAc.getBalance();

				CustomerRegistration crGet = crsImp.getByIdCustomer(givenAcNo);

				// ----------fetch receiver's Account status----------
				String receiverAcstatus = receiverByAc.getStatus();
				System.out.println("Receiver's Account Status: " + receiverAcstatus);

				// -------- condition for receiver's Account status-----------
				if (receiverAcstatus.equals("active")) {

					// ------check sender balance ---------------
					if (givenAmnt <= senderCurrentBalance) {

						// ---------validate transactiion pin -----------------

						if (givenPin.equals(senderPin)) {

							// ----------decrease the sender balance------
							double senderNewBalance = senderCurrentBalance - givenAmnt;
							System.out.println("Sender New Balance: " + senderNewBalance);

							// ----------increase the receiver's balance------
							double receiverNewBalance = receiverCurrentBalance + givenAmnt;
							System.out.println("receiver New Balance: " + receiverNewBalance);

							// --------update sender and receiver's Balance----------

							senderByAc.setBalance(senderNewBalance);

							CustomerAccount caSender = crsImp.saveAccount(senderByAc);

							receiverByAc.setBalance(receiverNewBalance);
							CustomerAccount caReceiver = crsImp.saveAccount(receiverByAc);

							if (caSender != null && caReceiver != null) {
								System.out.println("Sender and receiver Balance Update SucessFully");

								// ----------save the transaction history------------------

								// cs.setAmount(givenAmnt);
								// cs.setDate(transactionDate);
								// cs.setSenderName(crByContact.getName());
								// cs.setSenderAccountNo(crByContact.getRegistrationNumber());
								// cs.setReceiverName(crGet.getName());
								// cs.setReceiverAccountNo(givenAcNo);
								// cs.setRemarks(remarks);

								// crsImp.saveCustomerStatement(cs);

								ra.addFlashAttribute("success", "Transfer Successfull");
								return "redirect:/fundTransfer";

							} else {
								System.out.println("Sender and receiver Balance Update SucessFully");
								ra.addFlashAttribute("error", "Transfer Failed");
								return "redirect:/fundTransfer";
							}

						} else {
							ra.addFlashAttribute("error", "Incorrect Transaction Pin..!!");
							return "redirect:/fundTransfer";
						}

					} else {

						ra.addFlashAttribute("error", "Insufficient balance...!!!");
						return "redirect:/fundTransfer";
					}

				} else {

					ra.addFlashAttribute("error", "Receiver's Account is Locked..!!");
					return "redirect:/fundTransfer";

				}

			} else {
				ra.addFlashAttribute("error", "Account doesnot match..!!");
				return "redirect:/fundTransfer";
			}

		} else {
			ra.addFlashAttribute("error", "Failed to Transfer Fund..!!");
			return "redirect:/fundTransfer";
		}

	}

//-------------------customerStatement-------------------------------
	/*
	 * @GetMapping("/customerStatement") public String customerStatement(Model m,
	 * HttpSession session) {
	 * 
	 * String sessionContact = (String) session.getAttribute("customersession");
	 * System.out.println("Session: " + sessionContact);
	 * 
	 * // ----------fetch data from Customer Phone Number------------- String
	 * ownerName; CustomerRegistration crByContact =
	 * crsImp.findByContact(sessionContact); if (crByContact != null) {
	 * System.out.println("Account Holder Name: " + crByContact.getName());
	 * System.out.println("Account No: " + crByContact.getRegistrationNumber()); int
	 * csAcc = crByContact.getRegistrationNumber(); ownerName =
	 * crByContact.getName(); } else { return "redirect:/customerLogout"; }
	 * 
	 * // CustomerStatement csAll = //
	 * crsImp.findBySenderAccountNoOrReceiverAccountNo(0,csAcc); //
	 * CustomerStatement csAll = //
	 * crsImp.findBySenderNameOrReceiverName(crByContact.getName(), "");
	 * 
	 * // List<CustomerStatement> csAll = crsImp.getAllCustomerStatement();
	 * List<CustomerStatement> csAll = crsImp.findAllOrderedByDate();
	 * System.out.println("------------------------"); for (CustomerStatement cs :
	 * csAll) {
	 * 
	 * if (crByContact.getName().equals(cs.getReceiverName()) ||
	 * crByContact.getName().equals(cs.getSenderName())) {
	 * System.out.println("Receivers name: " + cs.getReceiverName()); //
	 * System.out.println("Sender name: " + cs.getSenderName()); }
	 * 
	 * }
	 * 
	 * // System.out.println("Remarks: " + csAll.getRemarks());
	 * 
	 * m.addAttribute("csObj", csAll); m.addAttribute("ownerName", ownerName);
	 * 
	 * return "customerStatement"; }
	 */
//------------Statement details--------------

	// -------------------customerStatementSecond-------------------------------

	@GetMapping("/customerStatementSecond")
	public String customerStatementSecond(Model m, HttpSession session) {

		String sessionContact = (String) session.getAttribute("customersession");
		System.out.println("Session: " + sessionContact);

		// ----------fetch data from Customer Phone Number-------------
		String ownerName;
		int csAcc;
		CustomerRegistration crByContact = crsImp.findByContact(sessionContact);
		if (crByContact != null) {
			System.out.println("Account Holder Name: " + crByContact.getName());
			System.out.println("Account No: " + crByContact.getRegistrationNumber());
			csAcc = crByContact.getRegistrationNumber();
			ownerName = crByContact.getName();
		} else {
			return "redirect:/customerLogout";
		}

		// CustomerStatement csAll =
		// crsImp.findBySenderAccountNoOrReceiverAccountNo(0,csAcc);
		// CustomerStatement csAll =
		// crsImp.findBySenderNameOrReceiverName(crByContact.getName(), "");

		// List<CustomerStatement> csAll = crsImp.getAllCustomerStatement();
		List<CustomerStatementSecond> csAllSec = crsImp.findAllOrderedByDateSecond();
		System.out.println("------------------------");
		for (CustomerStatementSecond cs : csAllSec) {

			/*
			 * if (crByContact.getName().equals(cs.getReceiverName()) ||
			 * crByContact.getName().equals(cs.getSenderName())) {
			 * System.out.println("Receivers name: " + cs.getReceiverName()); //
			 * System.out.println("Sender name: " + cs.getSenderName()); }
			 */

		}

		// System.out.println("Remarks: " + csAll.getRemarks());

		m.addAttribute("csObj", csAllSec);
		m.addAttribute("acNo", csAcc);

		return "customerStatementSecond";
	}

	// ------------Statement details--------------

	@GetMapping("/statementDetailSecond/{id}")
	public String statementDetailSecond(Model m, @PathVariable int id) {

		m.addAttribute("id", id);

		CustomerStatementSecond csById = crsImp.getByIdCustomerStatementSecond(id);

		m.addAttribute("idObj", csById);

		return "statementDetailSecond";
	}

	// ------------Statement details--------------

	@GetMapping("/statementDetailThird/{id}")
	public String statementDetailThird(Model m, @PathVariable int id) {

		m.addAttribute("id", id);

		TotalTransaction tt = crsImp.getByAccountTransaction(id);

		System.out.println("Amount: " + tt.getAccountNo());

		m.addAttribute("idObj", tt);

		return "statementDetailThird";
	}

//----------------customer edit button--------------

	@PostMapping("/editButton")
	public String editButton(RedirectAttributes ra, @RequestParam(name = "acNo") String acNo) {

		ra.addFlashAttribute("acNo", acNo);
		return "redirect:/customerDetailEdit";
	}

//--------------cutsomer delete button-----------
	@PostMapping("/deleteButton")
	public String deleteButton(RedirectAttributes ra, @RequestParam(name = "acNo") String acNo) {

		// ra.addFlashAttribute("acNo", acNo);
		int ac = Integer.parseInt(acNo);

		crsImp.deleteCustomer(ac);
		crsImp.deleteAccount(ac);
		crsImp.deleteCustomerProfile(ac);
		crsImp.deleteCustomerUserPassword(ac);

		ra.addFlashAttribute("smsg", "Successfully Deleted");

		return "redirect:/customerDetails";
	}

//---------------deposit/withdraw button-----------------
	@PostMapping("/depositwithdrawButton")
	public String depositwithdrawButton(RedirectAttributes ra, @RequestParam(name = "acNo") String acNo,
			@RequestParam(name = "fAmt") String fAmt, @RequestParam(name = "sAmt") String sAmt,
			@RequestParam(name = "fMonth") String fMonth,
			@RequestParam(name = "transactionType") String transactionType) {

		System.out.print("#$%TransactionTypr: " + transactionType);

		int fineAmount = Integer.parseInt(fMonth);
		int newFineMonth = fineAmount + 1;
		ra.addFlashAttribute("acNo", acNo);
		ra.addFlashAttribute("fAmt", fAmt);
		ra.addFlashAttribute("fMonth", newFineMonth);
		ra.addFlashAttribute("sAmt", sAmt);
		ra.addFlashAttribute("tType", transactionType);

		// depositwithdrawAmountMapping();

		return "redirect:/depositwithdrawAmount";
	}

//-------------customer details edit-----------------------

	@GetMapping("/customerDetailEdit")
	public String editCustomerDetail(Model m) {
		/*
		 * String sessionContact = (String) session.getAttribute("customersession");
		 * System.out.println("Session: " + sessionContact);
		 * 
		 * m.addAttribute("session", sessionContact);
		 * 
		 */

		String sAcNo = String.valueOf(m.getAttribute("acNo"));
		int acNo = Integer.parseInt(sAcNo);
		System.out.println("Customer AcID: " + acNo);

		System.out.println("AcNo: " + acNo);

		CustomerRegistration crGet = crsImp.getByIdCustomer(acNo);

		CustomerAccount caByAc = crsImp.getByIdAccount(acNo);
		System.out.println("#@#@# saving amt: " + caByAc.getSavingAmount());

		double savingAmount = caByAc.getSavingAmount();
		m.addAttribute("savingAmt", savingAmount);

		System.out.println("Name: " + crGet.getName());

		m.addAttribute("crGet", crGet);

		String gender = crGet.getGender();
		m.addAttribute("gender", gender);

		String maritalStatus = crGet.getMaritalStatus();
		m.addAttribute("maritalStatus", maritalStatus);

		return "customerDetailEdit";
	}

//-------------------edit Customer---------------

	@PostMapping("/editCustomerDetail")
	public String editCustomer(RedirectAttributes ra, @RequestParam(name = "name") String name,
			@RequestParam(name = "contact") String contact, @RequestParam(name = "email") String email,
			@RequestParam(name = "address") String address, @RequestParam(name = "gender") String gender,
			@RequestParam(name = "profession") String profession, @RequestParam(name = "dob") String dob,
			@RequestParam(name = "citizenshipNo") String citizenshipNo,
			@RequestParam(name = "maritalStatus") String maritalStatus,
			@RequestParam(name = "registrationNumber") String registrationNumber,
			@RequestParam(name = "nomineeName") String nomineeName, @RequestParam(name = "relation") String relation,
			@RequestParam(name = "nomineeContact") String nomineeContact,
			@RequestParam(name = "nomineeAddress") String nomineeAddress,
			@RequestParam(name = "savingAmount") String savingAmount) {
		System.out.println("Edited Namae: " + name);
		System.out.println("Edited contact: " + contact);
		System.out.println("Edited Email: " + email);
		System.out.println("Edited Address: " + address);
		System.out.println("Edited Gender: " + gender);
		System.out.println("Edited profession: " + profession);
		System.out.println("Edited DoB: " + dob);
		System.out.println("Edited CitizenNo: " + citizenshipNo);
		System.out.println("Edited RegNo: " + registrationNumber);
		System.out.println("Edited MaritalStatus: " + maritalStatus);

		System.out.println("##SAving aMount: " + savingAmount);

		// -----------fetch data of given acNo-------------------

		int regNo = Integer.parseInt(registrationNumber);
		CustomerRegistration crGet = crsImp.getByIdCustomer(regNo);

		System.out.println("Fetch Name from acNo: " + crGet.getName());

		crGet.setName(name);
		crGet.setContact(contact);
		crGet.setEmail(email);
		crGet.setAddress(address);
		crGet.setGender(gender);
		crGet.setProfession(profession);
		crGet.setDob(dob);
		crGet.setCitizenshipNo(citizenshipNo);
		crGet.setRegistrationNumber(regNo);
		crGet.setMaritalStatus(maritalStatus);
		crGet.setNomineeName(nomineeName);
		crGet.setNomineeContact(nomineeContact);
		crGet.setNomineeAddress(nomineeAddress);
		crGet.setRelation(relation);

		CustomerRegistration crSave = crsImp.saveCustomer(crGet);

		// ------set saving amount

		CustomerAccount ca = crsImp.getByIdAccount(regNo);

		double newSavingAmount = Double.parseDouble(savingAmount);
		ca.setSavingAmount(newSavingAmount);
		CustomerAccount caSave = crsImp.saveAccount(ca);

		if (crSave != null) {

			ra.addFlashAttribute("smsg", "Successfully Updated");
			return "redirect:/customerDetails";
		} else {
			ra.addFlashAttribute("smsg", "Failed to Update");
			return "redirect:/customerDetails";
		}

	}

//------------Customer password change----------------------

	@GetMapping("/customerPasswordChange")
	public String customerPasswordChange() {

		return "customerPasswordChange";
	}

	@PostMapping("/passwordChange")
	public String PasswordChange(RedirectAttributes ra, HttpSession session, @RequestParam String currentPassword,
			@RequestParam String newPassword, @RequestParam String confirmNewPassword) {

		String sessionContact = (String) session.getAttribute("customersession");
		System.out.println("Session: " + sessionContact);

		System.out.println("CurrentPassword: " + currentPassword);
		System.out.println("newPassword: " + newPassword);
		System.out.println("ConfirmNewPassword: " + confirmNewPassword);

		// ----------fetch data by phone number----------
		CustomerUserPassword cup = crsImp.findByPhoneNo(sessionContact);

		System.out.println("DBPassword: " + cup.getPassword());

		String dbPassword = cup.getPassword();

		// -------encrypt and check given password with db password

		BCryptPasswordEncoder bc = new BCryptPasswordEncoder();
		String givenRawPassword = confirmNewPassword;

		String encryptedPassword = bc.encode(confirmNewPassword);

		System.out.println("Normal: " + confirmNewPassword);
		System.out.println("Encrypted: " + encryptedPassword);

		// ------let's match given password-------

		/*
		 * String givenPass = "kamal098";
		 * 
		 * boolean isMatch = bc.matches(givenPass, enPass);
		 * 
		 * if(isMatch) { System.out.println("Match"); }else {
		 * System.out.println("Not Macth"); }
		 */

		int check = 0;
		boolean isMatch = bc.matches(currentPassword, cup.getPassword());

		if (isMatch) {

			check = 1;
			System.out.println("Password is Match");

		} else {

			System.out.println("Password doesnot match");
			check = 0;
		}

		/* if(cup.getPassword().equals(currentPassword)) { */
		if (check == 1) {
			cup.setPassword(encryptedPassword);
			crsImp.saveCustomerUserPass(cup);
			ra.addFlashAttribute("success", "Password Successfully Updated");
			System.out.print("password SuccessFully Updated");
			return "redirect:/customerLogout";

		} else {

			ra.addFlashAttribute("error", "Current Password doesnot Match...!!!");
			System.out.print("Failed password Updated");
			return "redirect:/customerPasswordChange";
		}

	}

	// ------------Customer Transaction pin change----------------------

	@GetMapping("/customerPinChange")
	public String customerPinChange() {

		return "customerPinChange";
	}

	@PostMapping("/pinChange")
	public String pinChange(RedirectAttributes ra, HttpSession session, @RequestParam String currentPin,
			@RequestParam String newPin, @RequestParam String confirmNewPin) {

		String sessionContact = (String) session.getAttribute("customersession");
		System.out.println("Session: " + sessionContact);

		System.out.println("CurrentPin: " + currentPin);
		System.out.println("newPin: " + newPin);
		System.out.println("ConfirmNewPin: " + confirmNewPin);

		// ----------fetch data by phone number----------
		CustomerUserPassword cup = crsImp.findByPhoneNo(sessionContact);

		System.out.println("DBPin: " + cup.getTransactionPin());

		if (cup.getTransactionPin().equals(currentPin)) {

			cup.setTransactionPin(confirmNewPin);
			crsImp.saveCustomerUserPass(cup);
			ra.addFlashAttribute("success", "Pin Successfully Updated");
			System.out.print("pin SuccessFully Updated");
			return "redirect:/customerPinChange";

		} else {

			ra.addFlashAttribute("error", "Current Pin doesnot Match...!!!");
			System.out.print("Failed to Updated Pin");
			return "redirect:/customerPinChange";
		}

	}

//-----------interest page mapping---------

	@GetMapping("/interestRate")
	public String interest(Model m) {
		// ----------fetch data of given type-------------

		InterestRate irGet = adminService.findByAcType("regularmonthlysaving");

		System.out.println("SavingsAccountType: " + irGet.getAccountType());
		System.out.println("PRate: " + irGet.getRate());
		m.addAttribute("cRate", irGet.getRate());

		return "interestRate";
	}

	@PostMapping("/setInterest")
	public String interestRate(@RequestParam double interestRate, String accountType, Model m) {

		System.out.println("Interest Rate: " + interestRate + "%");
		System.out.println("Account Type: " + accountType);

		String savingType = accountType;

		// ----------fetch data of given type-------------

		InterestRate irGet = adminService.findByAcType(accountType);

		System.out.println("SavingsAccountType: " + irGet.getAccountType());
		System.out.println("PRate: " + irGet.getRate());
		m.addAttribute("cRate", irGet.getRate());

		// double rate = Double.parseDouble(interestRate);

		irGet.setRate(interestRate);

		// System.out.println("AcType: "+irGet.getAccountType());
		// System.out.println("Rate: "+irGet.getRate()+"%");

		InterestRate irSave = adminService.saveInterest(irGet);
		if (irSave != null) {

			return "redirect:/interestRate";

		} else {
			return "redirect:/interestRate";
		}

	}

//-------------update accured amount with interest-----------------
	int c = 0;

	// @Scheduled(fixedRate = 90000) // 10000 mean 10 sec
	// @Scheduled(cron = "0 0 0 1 * *") // Execute at midnight on the 1st day of
	// every month
//	 @Scheduled(cron = "59 * * * * *") // schedules a task to run at the 59th
	// second of every minute, every hour, every
	// day, every month, and every day of the week.
//	@Scheduled(fixedRate = 10000)
//	 @Scheduled(fixedRate = 10000)

	// ---test propose
	// @Scheduled(fixedRate = 2000) //---every 2 second
//	@Scheduled(cron = "59 * * * * *") //every minute of 59 second
//	@Scheduled(cron = "0 */6 * * * *") // Execute every six minutes

	// ----real word scenario
//  @Scheduled(cron = "0 0 0 * * *") //--it will execute the task daily at midnight (12:00 AM).

	public void updateBalanceWithInterest() {

		List<CustomerAccount> caAll = crsImp.getAllAccount();

		for (CustomerAccount ca : caAll) {

			System.out.println("AcNo: " + ca.getAccountNo());
			System.out.println("CurrentBalance: " + ca.getBalance());
			System.out.println("CurrentInterestAmount: " + ca.getAccruedInterest());

			double currentAccruedInterest = ca.getAccruedInterest();
			double currentBalance = ca.getBalance();
			// double newBalance = currentBalance + 5;

			// ---------fetch current interest rate------------

			InterestRate irGet = adminService.findByAcType("regularmonthlysaving");

			System.out.println("Current InterestRate: " + irGet.getRate());

			double Rate = irGet.getRate();
			/*
			 * double interestRateMonthly = Rate / 12; // -- monthly count
			 * System.out.println("Monthly rate: " + interestRateMonthly);
			 */
			
			//----test scenario----------------
			double interestRateMonthly = Rate;
			double interestAmount = (interestRateMonthly / 100) * currentBalance;
			
			

			//-----real world scenario
//			double interestRateDaily = Rate / 30;
//			double interestAmount = (interestRateDaily / 100) * currentBalance;
//			
//			
			System.out.println("Interest amount of baalnce: " + interestAmount);
			double newAccruedInterest = currentAccruedInterest + interestAmount;

			double value = newAccruedInterest;

			// Create a DecimalFormat object with the desired pattern
			DecimalFormat df = new DecimalFormat("#.#####");

			// Format the value using the DecimalFormat object
			String formattedValue = df.format(value);
			double formatedAccruedInterest = Double.parseDouble(formattedValue);
			System.out.println("Formated Balacne: " + formatedAccruedInterest);

			// -------current balance interest rate------------

			ca.setAccruedInterest(formatedAccruedInterest);

			crsImp.saveAccount(ca);
			System.out.println("newBalance: " + newAccruedInterest);

			System.out.println("-------------------");

		}
		c++;
		System.out.println("C: " + c);
		System.out.println("++++++++++++++++++++++++++++");
	}

// --------------update current balance with accured interest---

//	 @Scheduled(fixedRate = 2000) //---every 2 second
//	@Scheduled(cron = "00 * * * * *") //every minute of 59 second	

//--------test scenario-------------

	// @Scheduled(cron = "59 * * * * *")
	
	// ---real world senario ------
//	@Scheduled(cron = "0 0 0 1 1,7 *")	//--will execute the task at midnight on the 1st of January and July each year
	public void updateBalanceWithAccuredInterest() {

		List<CustomerAccount> caAll = crsImp.getAllAccount();

		for (CustomerAccount ca : caAll) {

			System.out.println("AcNo: " + ca.getAccountNo());
			System.out.println("CurrentBalance: " + ca.getBalance());
			System.out.println("CurrentAccuredInterest: " + ca.getAccruedInterest());

			double currentAccruedInterest = ca.getAccruedInterest();
			double currentBalance = ca.getBalance();

			double newCurrentBalance = currentBalance + currentAccruedInterest;

			double value = newCurrentBalance;

			// Create a DecimalFormat object with the desired pattern
			DecimalFormat df = new DecimalFormat("#.#####");

			// Format the value using the DecimalFormat object
			String formattedValue = df.format(value);
			double formatedBalance = Double.parseDouble(formattedValue);

			ca.setBalance(formatedBalance);
			ca.setAccruedInterest(0);

			crsImp.saveAccount(ca);

			// ---------fetch current interest rate------------

			// InterestRate irGet = adminService.findByAcType("regularmonthlysaving");

			// System.out.println("Current InterestRate: " + irGet.getRate());

			// double Rate = irGet.getRate();

			/*
			 * double interestRateMonthly = Rate / 12; // -- monthly count
			 * System.out.println("Monthly rate: " + interestRateMonthly);
			 */

			// double interestRateDaily = Rate / 30;

			// double interestAmount = (interestRateDaily / 100) * currentBalance;
			// System.out.println("Interest amount of baalnce: " + interestAmount);
			// double newAccruedInterest = currentAccruedInterest + interestAmount;

			// double value = newAccruedInterest;

			// Create a DecimalFormat object with the desired pattern
			// DecimalFormat df = new DecimalFormat("#.#####");

			// Format the value using the DecimalFormat object
			// String formattedValue = df.format(value);
			// double formatedAccruedInterest = Double.parseDouble(formattedValue);
			/// System.out.println("Formated Balacne: " + formatedAccruedInterest);

			// -------current balance interest rate------------

			// ca.setAccruedInterest(formatedAccruedInterest);

			// crsImp.saveAccount(ca);
			// System.out.println("newBalance: " + newAccruedInterest);

			System.out.println("-------------------");

		}
		c++;
		System.out.println("C: " + c);
		System.out.println("++++++++++++++++++++++++++++");
	}

	// ------------customer deposit money via esewa---------------------

	@GetMapping("/customerDepositMoney")
	public String depositMoney(Model m, HttpSession session, RedirectAttributes ra) {

		System.out.println("####Payment option is clicked");
		// ----------fetch regular saving amount of customer---------

		String sessionContact = (String) session.getAttribute("customersession");
		System.out.println("Session: " + sessionContact);

		// ----------check condition for deposit amount from esewa------------

		CustomerRegistration crGet = crsImp.findByContact(sessionContact);

		System.out.println("Deposit count Session AcNo: " + crGet.getRegistrationNumber());
		int cAcNo = crGet.getRegistrationNumber();
		System.out.println("Account Number for deposit count: " + cAcNo);

		// ----------fetch deposit count value of given acNo------------------

		CustomerAccount caByAcNo = crsImp.getByIdAccount(cAcNo);
		System.out.println("deposit count: " + caByAcNo.getDepositCount());
		int depositCount = caByAcNo.getDepositCount();

		// -------------check if deposit is done or not----------------

		if (depositCount == 0) {

			// Your business logic to generate the success URL
			String successUrl = "http://localhost:8080/success"; // Adjust as needed

			String failedUrl = "http://localhost:8080/failed";
			// Add the successUrl to the model to make it accessible in the Thymeleaf
			// template

			m.addAttribute("successUrl", successUrl);
			m.addAttribute("failedUrl", failedUrl);

			// ------------fetch account number from contact----------

			CustomerRegistration crAll = crsImp.findByContact(sessionContact);

			System.out.println("Session AcNo: " + crAll.getRegistrationNumber());
			int customerAcNo = crAll.getRegistrationNumber();

			CustomerAccount caAc = crsImp.getByIdAccount(customerAcNo);

			System.out.println("Customer Saving Amount: " + caAc.getSavingAmount());

			int totalSavingMonth = caAc.getFineMonthCount() + 1;
			System.out.println("#@SAving count month: " + totalSavingMonth);

			double savingAmount = caAc.getSavingAmount();
			System.out.println("SAving Amount: " + savingAmount);

			// double totalSavingAmount = caAc.getSavingAmount() * totalSavingMonth;

			double totalSavingAmount = caAc.getSavingAmount();
			System.out.println("Total saving Amount of " + totalSavingMonth + "month");
			System.out.println("amount" + totalSavingAmount);

			System.out.println("Fine month: " + caAc.getFineMonthCount());
			int customerFineMonth = caAc.getFineMonthCount();

			// ---------fine amount of customer-----------

			// ---------send fine rate and amount as per monthly saving amount------------

			double cfineRate = 0;
			double cfineAmount = 0;
			double totalAmount = 0;
			FineRate cfr = adminService.findByAccountType("regularmonthlysaving");

			switch (customerFineMonth) {

			case -1:

				System.out.println("fine Month count: " + customerFineMonth);
				m.addAttribute("fineRate", "0");
				// cfineRate = cfr.getMonth1();

				// cfineAmount = (cfineRate / 100) * savingAmount;
				cfineAmount = 0;

				m.addAttribute("fineAmount", cfineAmount);
				totalAmount = totalSavingAmount;
				// m.addAttribute("totalsavingAmount", totalSavingAmount);
				System.out.println("Total amount: " + totalAmount);
				m.addAttribute("totalsavingAmount", totalAmount);

				break;

			case 0:

				System.out.println("fine Month count: " + customerFineMonth);
				m.addAttribute("fineRate", "0");
				// cfineRate = cfr.getMonth1();

				// cfineAmount = (cfineRate / 100) * savingAmount;
				cfineAmount = 0;

				m.addAttribute("fineAmount", cfineAmount);
				totalAmount = totalSavingAmount;
				// m.addAttribute("totalsavingAmount", totalSavingAmount);
				System.out.println("Total amount: " + totalAmount);
				m.addAttribute("totalsavingAmount", totalAmount);
				break;

			case 1:

				System.out.println("fine Month count: " + customerFineMonth);
				m.addAttribute("fineRate", cfr.getMonth1());
				cfineRate = cfr.getMonth1();

				cfineAmount = (cfineRate / 100) * savingAmount;

				m.addAttribute("fineAmount", cfineAmount);
				totalAmount = totalSavingAmount * 2;
				// m.addAttribute("totalsavingAmount", totalSavingAmount);
				System.out.println("Total amount: " + totalAmount);
				m.addAttribute("totalsavingAmount", totalAmount);

				break;

			case 2:

				System.out.println("fine Month count: " + customerFineMonth);
				m.addAttribute("fineRate", cfr.getMonth2());

				cfineRate = cfr.getMonth2();

				cfineAmount = (cfineRate / 100) * savingAmount;

				m.addAttribute("fineAmount", cfineAmount);
				totalAmount = totalSavingAmount * 3;
				// m.addAttribute("totalsavingAmount", totalSavingAmount);
				System.out.println("Total amount: " + totalAmount);
				m.addAttribute("totalsavingAmount", totalAmount);
				break;

			case 3:

				System.out.println("fine Month count: " + customerFineMonth);
				m.addAttribute("fineRate", cfr.getMonth3());

				cfineRate = cfr.getMonth3();

				cfineAmount = (cfineRate / 100) * savingAmount;

				m.addAttribute("fineAmount", cfineAmount);
				totalAmount = totalSavingAmount * 4;
				// m.addAttribute("totalsavingAmount", totalSavingAmount);
				System.out.println("Total amount: " + totalAmount);
				m.addAttribute("totalsavingAmount", totalAmount);
				break;

			case 4:

				System.out.println("fine Month count: " + customerFineMonth);
				System.out.println("fine Rate for Month: " + cfr.getMonth4());
				m.addAttribute("fineRate", cfr.getMonth4());

				cfineRate = cfr.getMonth4();

				cfineAmount = (cfineRate / 100) * savingAmount;

				m.addAttribute("fineAmount", cfineAmount);
				totalAmount = totalSavingAmount * 5;
				// m.addAttribute("totalsavingAmount", totalSavingAmount);
				System.out.println("Total amount: " + totalAmount);
				m.addAttribute("totalsavingAmount", totalAmount);
				break;

			case 5:

				System.out.println("fine Month count: " + customerFineMonth);
				m.addAttribute("fineRate", cfr.getMonth5());

				cfineRate = cfr.getMonth5();

				cfineAmount = (cfineRate / 100) * savingAmount;

				m.addAttribute("fineAmount", cfineAmount);
				totalAmount = totalSavingAmount * 6;
				// m.addAttribute("totalsavingAmount", totalSavingAmount);
				System.out.println("Total amount: " + totalAmount);
				m.addAttribute("totalsavingAmount", totalAmount);

				break;

			case 6:

				System.out.println("fine Month count: " + customerFineMonth);
				m.addAttribute("fineRate", cfr.getMonth6());

				cfineRate = cfr.getMonth6();

				cfineAmount = (cfineRate / 100) * savingAmount;

				m.addAttribute("fineAmount", cfineAmount);
				totalAmount = totalSavingAmount * 7;
				// m.addAttribute("totalsavingAmount", totalSavingAmount);
				System.out.println("Total amount: " + totalAmount);
				m.addAttribute("totalsavingAmount", totalAmount);
				break;

			case 7:

				System.out.println("fine Month count: " + customerFineMonth);
				m.addAttribute("fineRate", cfr.getMonth7());

				cfineRate = cfr.getMonth7();

				cfineAmount = (cfineRate / 100) * savingAmount;

				m.addAttribute("fineAmount", cfineAmount);
				totalAmount = totalSavingAmount * 8;
				// m.addAttribute("totalsavingAmount", totalSavingAmount);
				System.out.println("Total amount: " + totalAmount);
				m.addAttribute("totalsavingAmount", totalAmount);
				break;

			case 8:

				System.out.println("fine Month count: " + customerFineMonth);
				m.addAttribute("fineRate", cfr.getMonth8());

				cfineRate = cfr.getMonth8();

				cfineAmount = (cfineRate / 100) * savingAmount;

				m.addAttribute("fineAmount", cfineAmount);
				totalAmount = totalSavingAmount * 9;
				// m.addAttribute("totalsavingAmount", totalSavingAmount);
				System.out.println("Total amount: " + totalAmount);
				m.addAttribute("totalsavingAmount", totalAmount);
				break;

			case 9:

				System.out.println("fine Month count: " + customerFineMonth);
				m.addAttribute("fineRate", cfr.getMonth9());

				cfineRate = cfr.getMonth9();

				cfineAmount = (cfineRate / 100) * savingAmount;

				m.addAttribute("fineAmount", cfineAmount);
				totalAmount = totalSavingAmount * 10;
				// m.addAttribute("totalsavingAmount", totalSavingAmount);
				System.out.println("Total amount: " + totalAmount);
				m.addAttribute("totalsavingAmount", totalAmount);
				break;

			case 10:

				System.out.println("fine Month count: " + customerFineMonth);
				m.addAttribute("fineRate", cfr.getMonth10());

				cfineRate = cfr.getMonth10();

				cfineAmount = (cfineRate / 100) * savingAmount;

				m.addAttribute("fineAmount", cfineAmount);
				totalAmount = totalSavingAmount * 11;
				// m.addAttribute("totalsavingAmount", totalSavingAmount);
				System.out.println("Total amount: " + totalAmount);
				m.addAttribute("totalsavingAmount", totalAmount);
				break;

			case 11:

				System.out.println("fine Month count: " + customerFineMonth);
				m.addAttribute("fineRate", cfr.getMonth11());

				cfineRate = cfr.getMonth11();

				cfineAmount = (cfineRate / 100) * savingAmount;

				m.addAttribute("fineAmount", cfineAmount);
				totalAmount = totalSavingAmount * 12;
				// m.addAttribute("totalsavingAmount", totalSavingAmount);
				System.out.println("Total amount: " + totalAmount);
				m.addAttribute("totalsavingAmount", totalAmount);
				break;

			case 12:

				System.out.println("fine Month count: " + customerFineMonth);
				m.addAttribute("fineRate", cfr.getMonth12());

				cfineRate = cfr.getMonth12();

				cfineAmount = (cfineRate / 100) * savingAmount;

				if (cfineAmount <= 0) {
					m.addAttribute("fineAmount", 0);
				} else {
					m.addAttribute("fineAmount", cfineAmount);
					totalAmount = totalSavingAmount * 2;
					// m.addAttribute("totalsavingAmount", totalSavingAmount);
					System.out.println("Total amount: " + totalAmount);
					m.addAttribute("totalsavingAmount", totalAmount);
				}

			}

			double amountForDeposit = totalAmount + cfineAmount;

			System.out.println("Customer fine amount :" + cfineAmount);
			System.out.println("Customer fine + saving amount :" + amountForDeposit);

			// double amount = 110;

			// m.addAttribute("amount", savingAmount);
			m.addAttribute("amount", amountForDeposit);

			// -------productid and merchant code-------------

			char initialLetter = 'P';
			char lastLetter = 'K';

			// Generate a random number (e.g., between 1000 and 9999)
			int randomNumber = new Random().nextInt(99000) + 10000;

			// Combine the initial letter and random number
			String pid = String.valueOf(initialLetter) + randomNumber + String.valueOf(lastLetter);

			System.out.println("Generated pid Value: " + pid);

			m.addAttribute("merchantCode", "EPAYTEST");
			m.addAttribute("pid", pid);

			// --------fetch and update amount--------------

			/*
			 * List<UserAccount> list = uasImp.getAll();
			 * 
			 * for (UserAccount ua : list) { System.out.println("Acount: " +
			 * ua.getAccountNo()); System.out.println("Name: " + ua.getName());
			 * System.out.println("Balance: " + ua.getBalance());
			 * 
			 * }
			 */

			int id = 1;

			return "customerDepositMoney";

		} else {

			ra.addFlashAttribute("dcount", "*----------------Already deposited this month..!!-----------------*");
			return "redirect:/customerProfile";

		}

	}

//---------------success and failed deposit-------------------------
	@Transactional
	@GetMapping("/success")
	public String success(RedirectAttributes ra, @RequestParam(name = "oid") String oid,
			@RequestParam(name = "amt") Double amount, @RequestParam(name = "refId") String refId, Model model,
			HttpSession session) {

		model.addAttribute("oid", oid);
		model.addAttribute("amount", amount);
		model.addAttribute("refId", refId);

		System.out.println("OID: " + oid);
		System.out.println("Amount: " + amount);
		System.out.println("RefId: " + refId);
		double esewaAmount = amount;

		// ---------fetch customer account details and update------------
		String sessionContact = (String) session.getAttribute("customersession");
		System.out.println("Session: " + sessionContact);

		// ------------fetch account number from contact----------

		CustomerRegistration crAll = crsImp.findByContact(sessionContact);

		System.out.println("Session AcNo: " + crAll.getRegistrationNumber());
		int customerAcNo = crAll.getRegistrationNumber();

		// ------------separate saving amount and fine amount----------------

		System.out.println("@@@@@@@@@22#########3");

		CustomerAccount cAAc = crsImp.getByIdAccount(customerAcNo);

		System.out.println("Customer Saving Amount: " + cAAc.getSavingAmount());
		double savingAmount = cAAc.getSavingAmount();

		System.out.println("Fine month: " + cAAc.getFineMonthCount());
		int customerFineMonth = cAAc.getFineMonthCount();

		// ---------fine amount of customer-----------

		// ---------send fine rate and amount as per monthly saving amount------------

		double cfineRate = 0;
		double cfineAmount = 0;
		FineRate cfr = adminService.findByAccountType("regularmonthlysaving");

		switch (customerFineMonth) {

		case -1:

			System.out.println("fine Month count: " + customerFineMonth);
			// m.addAttribute("fineRate", "0");

			// cfineRate = cfr.getMonth12();

			cfineAmount = 0;

			// m.addAttribute("fineAmount", cfineAmount);

			break;

		case 0:

			System.out.println("fine Month count: " + customerFineMonth);
			// m.addAttribute("fineRate", "0");

			// cfineRate = cfr.getMonth12();

			cfineAmount = 0;

			// m.addAttribute("fineAmount", cfineAmount);

			break;

		case 1:

			System.out.println("fine Month count: " + customerFineMonth);
			// m.addAttribute("fineRate", cfr.getMonth1());
			cfineRate = cfr.getMonth1();

			cfineAmount = (cfineRate / 100) * savingAmount;

			// m.addAttribute("fineAmount", cfineAmount);

			break;

		case 2:

			System.out.println("fine Month count: " + customerFineMonth);
			// m.addAttribute("fineRate", cfr.getMonth2());

			cfineRate = cfr.getMonth2();

			cfineAmount = (cfineRate / 100) * savingAmount;

			// m.addAttribute("fineAmount", cfineAmount);
			break;

		case 3:

			System.out.println("fine Month count: " + customerFineMonth);
			// m.addAttribute("fineRate", cfr.getMonth3());

			cfineRate = cfr.getMonth3();

			cfineAmount = (cfineRate / 100) * savingAmount;

			// m.addAttribute("fineAmount", cfineAmount);
			break;

		case 4:

			System.out.println("fine Month count: " + customerFineMonth);
			System.out.println("fine Rate for Month: " + cfr.getMonth4());
			// m.addAttribute("fineRate", cfr.getMonth4());

			cfineRate = cfr.getMonth4();

			cfineAmount = (cfineRate / 100) * savingAmount;

			// m.addAttribute("fineAmount", cfineAmount);
			break;

		case 5:

			System.out.println("fine Month count: " + customerFineMonth);
			// m.addAttribute("fineRate", cfr.getMonth5());

			cfineRate = cfr.getMonth5();

			cfineAmount = (cfineRate / 100) * savingAmount;

			// m.addAttribute("fineAmount", cfineAmount);

			break;

		case 6:

			System.out.println("fine Month count: " + customerFineMonth);
			// m.addAttribute("fineRate", cfr.getMonth6());

			cfineRate = cfr.getMonth6();

			cfineAmount = (cfineRate / 100) * savingAmount;

			// m.addAttribute("fineAmount", cfineAmount);
			break;

		case 7:

			System.out.println("fine Month count: " + customerFineMonth);
			// m.addAttribute("fineRate", cfr.getMonth7());

			cfineRate = cfr.getMonth7();

			cfineAmount = (cfineRate / 100) * savingAmount;

			// m.addAttribute("fineAmount", cfineAmount);
			break;

		case 8:

			System.out.println("fine Month count: " + customerFineMonth);
			// m.addAttribute("fineRate", cfr.getMonth8());

			cfineRate = cfr.getMonth8();

			cfineAmount = (cfineRate / 100) * savingAmount;

			// m.addAttribute("fineAmount", cfineAmount);
			break;

		case 9:

			System.out.println("fine Month count: " + customerFineMonth);
			// m.addAttribute("fineRate", cfr.getMonth9());

			cfineRate = cfr.getMonth9();

			cfineAmount = (cfineRate / 100) * savingAmount;

			// m.addAttribute("fineAmount", cfineAmount);
			break;

		case 10:

			System.out.println("fine Month count: " + customerFineMonth);
			// m.addAttribute("fineRate", cfr.getMonth10());

			cfineRate = cfr.getMonth10();

			cfineAmount = (cfineRate / 100) * savingAmount;

			// m.addAttribute("fineAmount", cfineAmount);
			break;

		case 11:

			System.out.println("fine Month count: " + customerFineMonth);
			// m.addAttribute("fineRate", cfr.getMonth11());

			cfineRate = cfr.getMonth11();

			cfineAmount = (cfineRate / 100) * savingAmount;

			// m.addAttribute("fineAmount", cfineAmount);
			break;

		case 12:

			System.out.println("fine Month count: " + customerFineMonth);
			// m.addAttribute("fineRate", cfr.getMonth12());

			cfineRate = cfr.getMonth12();

			cfineAmount = (cfineRate / 100) * savingAmount;

//			if (cfineAmount <= 0) {
//				//m.addAttribute("fineAmount", 0);
//			} else {
//				//m.addAttribute("fineAmount", cfineAmount);
//			}

		}

		// ------------after separation------------------------------

		System.out.println("##FineAmount : " + cfineAmount);
		double esewaDepositAmount = esewaAmount - cfineAmount;
		System.out.println("##Total Amount for deposit: " + esewaDepositAmount);
		CustomerAccount caAc = crsImp.getByIdAccount(customerAcNo);

		System.out.println("Customer Saving Amount : " + caAc.getSavingAmount());
		double currentBalance = caAc.getBalance();
		System.out.println("Current Balance: " + currentBalance);

		double newBalance = esewaDepositAmount + currentBalance;

		caAc.setBalance(newBalance);

		// -------save amount-----------
		crsImp.saveAccount(caAc);
		System.out.println("saving balance: " + newBalance);

		LocalDateTime currentDateTime = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss a");
		String formattedDateTime = currentDateTime.format(formatter);
		System.out.println("Date Time: " + formattedDateTime);

		// ---------update statement -----------------------
		int acNo = caAc.getAccountNo();
		System.out.println("AcNo: " + acNo);

		CustomerStatementSecond css = new CustomerStatementSecond();
		css.setAccountNo(acNo);
		css.setAmount(esewaDepositAmount);
		css.setDate(formattedDateTime);
		css.setTransactionType("deposit");
		css.setSource("esewa");

		crsImp.saveCustomerStatementSecond(css);

		// --------fakePayment check-----------
		int amt = 100;
		model.addAttribute("actualAmount", amt);
		model.addAttribute("merchantCode", "EPAYTEST");

		// --------fetch details for update deposit count-------------------

		// ----------fetch regular saving amount of customer---------

		// String sessionContact = (String) session.getAttribute("customersession");
		System.out.println("Session: " + sessionContact);

		// ----------check condition for deposit amount from esewa------------

		CustomerRegistration crGet = crsImp.findByContact(sessionContact);

		System.out.println("Deposit count Session AcNo: " + crGet.getRegistrationNumber());
		int cAcNo = crGet.getRegistrationNumber();
		System.out.println("Account Number for deposit count: " + cAcNo);

		// ----------fetch deposit count value of given acNo------------------

		CustomerAccount caByAcNo = crsImp.getByIdAccount(cAcNo);
		System.out.println("deposit count: " + caByAcNo.getDepositCount());

		// int depositCount = caByAcNo.getDepositCount();
		caByAcNo.setDepositCount(1);
		caByAcNo.setFineMonthCount(-1);
		crsImp.saveAccount(caByAcNo);
		
		// ---------emaildeposit notification when deposit start---------

		// System.out.println("Session contact: " + pNo);

		CustomerRegistration csAc = crsImp.getByIdCustomer(acNo);

		System.out.println("Name: " + csAc.getName());
		System.out.println("email: " + csAc.getEmail());

		String cemail = csAc.getEmail();

		// ---------email integration----------------

		try {

			MimeMessage msg = javaMailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(msg);

			helper.setFrom("nakfarke6@gmail.com");
			helper.setTo(cemail);
			helper.setSubject("DHAROHAR_ALERT");
			helper.setText("Your " + cAcNo + " has been Credited by NPR " + esewaDepositAmount + " on "
					+ formattedDateTime + ", Remarks: Deposit via eSewa");

			javaMailSender.send(msg);

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		// ---------email integration end---------------

		// --------emial notification end----------------------

		ra.addFlashAttribute("depositSuccess", "*----------Amount deposited SuccessFully----------*");
		return "redirect:/customerProfile";
	}

	@GetMapping("/failed")
	public String failed() {

		return "failed";
	}

//----------------deposit count and auto setting value----------------
//	 @Scheduled(fixedRate = 20000)
	
	//------test scenario----------------

	 //@Scheduled(cron = "59 * * * * *")
	
//-----------real world scanraio----------------
	
//	@Scheduled(cron = "0 0 0 1 * *") // Execute at midnight on the 1st day of every month
	public void depositCountChange() {

		// --------fetch details for update deposit count-------------------

		// ----------fetch regular saving amount of customer---------

		// String sessionContact = (String) session.getAttribute("customersession");
		// System.out.println("Session: " + sessionContact);

		// ----------check condition for deposit amount from esewa------------

		List<CustomerAccount> caGet = crsImp.getAllAccount();

		for (CustomerAccount ca : caGet) {

			System.out.println("Account Number for deposit count: " + ca.getAccountNo());
			System.out.println(" deposit count: " + ca.getDepositCount());

			ca.setDepositCount(0);
			crsImp.saveAccount(ca);

			System.out.println("----------------------------");

		}

		// System.out.println("Account Number for deposit count: " + cAcNo);

		// ----------fetch deposit count value of given acNo------------------

		// CustomerAccount caByAcNo = crsImp.getByIdAccount(cAcNo);
		// System.out.println("deposit count: " + caByAcNo.getDepositCount());

		// int depositCount = caByAcNo.getDepositCount();
		// caByAcNo.setDepositCount(1);
		// crsImp.saveAccount(caByAcNo);

	}

//-------------fine rate define by admin-----------------------------

	@GetMapping("/fineRate")
	public String fineRate(Model m) {

		FineRate ByAcType = adminService.findByAccountType("regularmonthlysaving");

		m.addAttribute("cRate", ByAcType);
		return "fineRate";
	}

	@PostMapping("/setFineRate")
	public String fineRateSet(RedirectAttributes ra, @RequestParam(name = "month1") String m1,
			@RequestParam(name = "month2") String m2, @RequestParam(name = "month3") String m3,
			@RequestParam(name = "month4") String m4, @RequestParam(name = "month5") String m5,
			@RequestParam(name = "month6") String m6, @RequestParam(name = "month7") String m7,
			@RequestParam(name = "month8") String m8, @RequestParam(name = "month9") String m9,
			@RequestParam(name = "month10") String m10, @RequestParam(name = "month11") String m11,
			@RequestParam(name = "month12") String m12, @RequestParam(name = "accountType") String acType) {

		System.out.println("month 1: " + m1);

		if (m1 == "" || m2 == "" || m3 == "" || m4 == "" || m5 == "" || m6 == "" || m7 == "" || m8 == "" || m9 == ""
				|| m10 == "" || m11 == "" || m12 == "") {
			ra.addFlashAttribute("failed", "All fields are required..!!");
			return "redirect:/fineRate";

		} else {

			double mth1 = Double.parseDouble(m1);
			double mth2 = Double.parseDouble(m2);
			double mth3 = Double.parseDouble(m3);
			double mth4 = Double.parseDouble(m4);
			double mth5 = Double.parseDouble(m5);
			double mth6 = Double.parseDouble(m6);
			double mth7 = Double.parseDouble(m7);
			double mth8 = Double.parseDouble(m8);
			double mth9 = Double.parseDouble(m9);
			double mth10 = Double.parseDouble(m10);
			double mth11 = Double.parseDouble(m11);
			double mth12 = Double.parseDouble(m12);

			FineRate frByAcType = adminService.findByAccountType(acType);

			System.out.println("Month12: " + frByAcType.getMonth12());

			// FineRate fr = new FineRate();

			// fr.setAccountType(acType);
			frByAcType.setMonth1(mth1);
			frByAcType.setMonth2(mth2);
			frByAcType.setMonth3(mth3);
			frByAcType.setMonth4(mth4);
			frByAcType.setMonth5(mth5);
			frByAcType.setMonth6(mth6);
			frByAcType.setMonth7(mth7);
			frByAcType.setMonth8(mth8);
			frByAcType.setMonth9(mth9);
			frByAcType.setMonth10(mth10);
			frByAcType.setMonth11(mth11);
			frByAcType.setMonth12(mth12);

			FineRate frSave = adminService.saveFine(frByAcType);

			if (frSave != null) {
				ra.addFlashAttribute("success", "Fine rate successfully updated.");
				return "redirect:/fineRate";

			} else {
				ra.addFlashAttribute("failed", "Failed to update fine rate..!!");
				return "redirect:/fineRate";
			}

		}

	}

	// -------------fine and interest related operations-----------------------

	// @Scheduled(fixedRate = 90000) // 10000 mean 10 sec
	// @Scheduled(cron = "0 0 0 1 * *") // Execute at midnight on the 1st day of
	// every month
	// @Scheduled(cron = "59 * * * * *") // schedules a task to run at the 59th
	// second of every minute, every hour, every
	// day, every month, and every day of the week.
	// @Scheduled(fixedRate = 10000)

	// ---------test propose------------
	// @Scheduled(cron = "59 * * * * *")

	// -----real world senario--------------

	// @Scheduled(cron = "0 0 0 1 * *") // Execute at midnight on the 1st day of
	// every month

	public void fineForCustomers() {

		System.out.println("fine for customer Running--------------");

		// ------increment value---------------------
		List<CustomerAccount> caAll = crsImp.getAllAccount();
		int newFineCount = 0;
		for (CustomerAccount ca : caAll) {

			System.out.println("AcNo: " + ca.getAccountNo());
			System.out.println("fine of month: " + ca.getFineMonthCount());

			if (ca.getFineMonthCount() < 12) {
				newFineCount = ca.getFineMonthCount() + 1;
				System.out.println("New fine of month: " + newFineCount);
				System.out.println("-------------------------");
			} else {
				newFineCount = ca.getFineMonthCount();

				System.out.println("New fine of month: " + newFineCount);
				System.out.println("-------------------------");
			}

			// ca.setDepositCount(newDepositCount);
			ca.setFineMonthCount(newFineCount);
			ca.setDepositCount(0);
			crsImp.saveAccount(ca);

		}

	}

}
