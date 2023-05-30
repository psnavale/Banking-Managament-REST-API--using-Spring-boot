package org.jsp.banking_system.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.jsp.banking_system.dto.BankAccount;
import org.jsp.banking_system.dto.BankTrasaction;
import org.jsp.banking_system.dto.Customer;
import org.jsp.banking_system.dto.Login;
import org.jsp.banking_system.exception.MyException;
import org.jsp.banking_system.helper.MailVerifiaction;
import org.jsp.banking_system.helper.ResponseStructure;
import org.jsp.banking_system.repository.BankAccounts;
import org.jsp.banking_system.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {
	
	@Autowired
	CustomerRepository repository;
	
	@Autowired
	BankAccounts repository2;
	
	@Autowired
	MailVerifiaction mailVerifiaction ;
	
	@Autowired
	BankAccount bankAccount;
	
	@Autowired
	BankTrasaction trasaction;

	public ResponseStructure<Customer> savecustomer(Customer customer) throws MyException {
		ResponseStructure<Customer> structure=new ResponseStructure<>();
	 int age=Period.between(customer.getDob().toLocalDate(),LocalDate.now()).getYears();
	 customer.setAge(age);
	 if(age<18)
	 {
		throw new MyException("Check Id and Try Again");
	 }else {
		 
		 Random random=new Random();
		 int otp=random.nextInt(100000,999999);
		 customer.setOtp(otp);
		 
		 mailVerifiaction.sendMail(customer);
		 
		 structure.setMsg("Verification Mail Sent");
		 structure.setCode(HttpStatus.PROCESSING.value());
		 structure.setData(repository.save(customer));
	 }
		return structure;
	}

	public ResponseStructure<Customer> verify(int custid, int otp) throws MyException {
		ResponseStructure<Customer> structure=new ResponseStructure<>();
		Optional<Customer> optional=repository.findById(custid);
		if(optional.isEmpty()) {
			throw new MyException("Check Id and try Again");
		}else {
			Customer customer=optional.get();
			if(customer.getOtp()==otp)
			{
				structure.setCode(HttpStatus.CREATED.value());
				structure.setMsg("Account created successfully");
				customer.setStatus(true);
				structure.setData(repository.save(customer));
			}
			else {
				throw new MyException("OTP MISMATCH");
			}
		}
		return structure;
	}

	public ResponseStructure<Customer> login(Login login) throws MyException {
		ResponseStructure<Customer> structure=new ResponseStructure<>();
		  Optional<Customer> optional=repository.findById(login.getId());
		  if(optional.isEmpty()) {
				throw new MyException("Invalid Customer Id");
			}else {
				Customer customer=optional.get();
				if(customer.getPassword().equals(login.getPassword()))
				{
					if(customer.isStatus()) {
						structure.setCode(HttpStatus.ACCEPTED.value());
						structure.setMsg("Login success");
						structure.setData(customer);
						
					}else {
						throw new MyException("Verify your email first");
					}
					
				}else {
					throw new MyException("Inavalid password");
				}
			}
		
		return structure;
	}

	public ResponseStructure<Customer> createAccount(int cust_id, String type) throws MyException 
	{
		ResponseStructure<Customer> structure=new ResponseStructure<>();
		Optional<Customer> optional=repository.findById(cust_id);
		if(optional.isEmpty()) {
			throw new MyException("Invalid customer Id");
		}else {
			Customer customer=optional.get();
			List<BankAccount> list=customer.getBankAccounts();
			boolean flag=true;
			for(BankAccount bankAccount:list)
			{
				if(bankAccount.getType().equals(type)) 
				{
					flag=false;
					break;
				}
			}
			if(!flag)
			{
				throw new MyException(type+"Account Already Exists");
			}else {
				bankAccount.setType(type);
				if(type.equals("savings")) {
					bankAccount.setBanklimit(5000);
				}else
				{			
					bankAccount.setBanklimit(10000);
				}
				list.add(bankAccount);
				customer.setBankAccounts(list);
			}
			structure.setCode(HttpStatus.ACCEPTED.value());
			structure.setMsg("Account created wait for management to approve");
			structure.setData(repository.save(customer));		
		}	
		return structure;
	}

	public ResponseStructure<List<BankAccount>> fetchAllTrue(int custid) throws MyException {
		ResponseStructure<List<BankAccount>> structure=new ResponseStructure<>();
		Optional<Customer> optional=repository.findById(custid);
		Customer customer=optional.get();
		List<BankAccount> list=customer.getBankAccounts();
		
		List<BankAccount> res=new ArrayList<BankAccount>();
		for(BankAccount account:list)
		{
			if(account.isStatus()) {
				res.add(account);
			}
		}
		if(res.isEmpty()) {
			throw new MyException("No active Accounts Found"); 
		}else {
			structure.setCode(HttpStatus.FOUND.value());
			structure.setMsg("Accounts Found");
			structure.setData(res);
		}
		

		return structure;
	}

	public ResponseStructure<Double> CheckBalanece(long acno) {
		ResponseStructure<Double> structure=new ResponseStructure<>();
		Optional<BankAccount> optional=repository2.findById(acno);
		BankAccount account=optional.get();
		
		structure.setCode(HttpStatus.FOUND.value());
		structure.setMsg("Data Found");
		structure.setData(account.getAmount());
		
		
		return structure;
	}

	public ResponseStructure<BankAccount> deposite(Long acno,double amount) {
		ResponseStructure<BankAccount> structure=new ResponseStructure<>();
		BankAccount account=repository2.findById(acno).get();
		account.setAmount(account.getAmount()+amount);
		
		trasaction.setDateTime(LocalDateTime.now());
		trasaction.setDeposit(amount);
		trasaction.setBalance(bankAccount.getAmount()+amount);
		
		List<BankTrasaction> trasactions=account.getBankTrasactions();
		trasactions.add(trasaction);
		
		account.setBankTrasactions(trasactions);
		
		structure.setCode(HttpStatus.ACCEPTED.value());
		structure.setMsg("Amount added successfully");
		structure.setData(repository2.save(account));
		
		
		return structure;
	}

	public ResponseStructure<BankAccount> withdraw(long acno, double amount) throws MyException {
		ResponseStructure<BankAccount> structure=new ResponseStructure<>();
		BankAccount account=repository2.findById(acno).get();
		
		if(amount>account.getBanklimit()) {
			throw new MyException("Out of Limit");
		}else {
			if(amount>account.getAmount()) {
				throw new MyException("Insufficient Fund");
			}else {
				account.setAmount(account.getAmount()-amount);
				
				
				
				trasaction.setDateTime(LocalDateTime.now());
				trasaction.setWithdraw(amount);
				trasaction.setBalance(bankAccount.getAmount());
				
				List<BankTrasaction> trasactions=account.getBankTrasactions();
				trasactions.add(trasaction);
				
				account.setBankTrasactions(trasactions);
				
				structure.setCode(HttpStatus.ACCEPTED.value());
				structure.setMsg("Amount withdrawn successfully");
				structure.setData(repository2.save(account));
			
			}
		}
				
		return structure;
	}

	public ResponseStructure<List<BankTrasaction>> viewtrasaction(long acno) throws MyException {
		ResponseStructure<List<BankTrasaction>> structure=new ResponseStructure<>();
		BankAccount account=repository2.findById(acno).get();
		List<BankTrasaction> list=account.getBankTrasactions();
		if(list.isEmpty()) {
			throw new MyException("No Trasaction");
		}else {
			structure.setCode(HttpStatus.FOUND.value());
			structure.setMsg("Data found");
			structure.setData(list);
		}
		return structure;
	}
	
}
