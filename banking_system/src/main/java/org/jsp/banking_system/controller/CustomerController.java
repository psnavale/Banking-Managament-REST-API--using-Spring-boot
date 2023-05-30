package org.jsp.banking_system.controller;

import java.util.List;

import org.jsp.banking_system.dto.BankAccount;
import org.jsp.banking_system.dto.BankTrasaction;
import org.jsp.banking_system.dto.Customer;
import org.jsp.banking_system.dto.Login;
import org.jsp.banking_system.exception.MyException;
import org.jsp.banking_system.helper.ResponseStructure;
import org.jsp.banking_system.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("customer")
public class CustomerController {
	@Autowired
	CustomerService service;
	
	@PostMapping("add")
	public ResponseStructure<Customer> savecustomer(@RequestBody Customer customer) throws MyException {
		return service.savecustomer(customer);
	}
	@PutMapping("otp/{custid}/{otp}")
	public ResponseStructure<Customer> otpVerify(@PathVariable int custid,@PathVariable int otp) throws MyException{
		return service.verify(custid,otp);
	}
	@PostMapping("login")
	public ResponseStructure<Customer> login(@RequestBody Login login) throws MyException{
		return service.login(login);
	}
	
	@PostMapping("account/{cust_id}/{type}")
	public ResponseStructure<Customer> createAccount(@PathVariable int cust_id,@PathVariable String type) throws MyException
	{
		return service.createAccount(cust_id,type);
	}
	
	@GetMapping("accounts/{custid}")
	public ResponseStructure<List<BankAccount>> fetchAllTrue(@PathVariable int custid) throws MyException
	{
		return service.fetchAllTrue(custid);
	}
	
	@GetMapping("account/check/{acno}")
	public ResponseStructure<Double> CheckBalance(@PathVariable  long acno){
		return service.CheckBalanece(acno);
	}
	@PutMapping("account/deposit/{acno}/{amount}")
	public ResponseStructure<BankAccount> deposite(@PathVariable Long acno,@PathVariable double amount){
		return service.deposite(acno,amount);
	}
	@PutMapping("account/withdraw/{acno}/{amount}")
	public ResponseStructure<BankAccount> withdraw(@PathVariable long acno,@PathVariable double amount) throws MyException{
		return service.withdraw(acno,amount);
	}
	@GetMapping("account/viewtrasaction/{acno}")
	public ResponseStructure<List<BankTrasaction>> viewtrasaction(@PathVariable long acno) throws MyException{
		return service.viewtrasaction(acno);
	}
}
