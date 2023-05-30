package org.jsp.banking_system.controller;

import java.util.List;

import org.jsp.banking_system.dto.BankAccount;
import org.jsp.banking_system.dto.Management;
import org.jsp.banking_system.exception.MyException;
import org.jsp.banking_system.helper.ResponseStructure;

import org.jsp.banking_system.service.ManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("management")
public class Managementcontroller {
	
	@Autowired
	ManagementService service;
	
	@PostMapping("add")
	public ResponseStructure<Management> savemanagement(@RequestBody Management management) 
	{
		return service.savemanagement(management);
	}
	
	@PostMapping("login")
	public ResponseStructure<Management> login(@RequestBody Management management) throws MyException
	{
		return service.login(management);
	}
	@GetMapping("accounts")
	public ResponseStructure<List<BankAccount>> fecthAllAccounts() throws MyException{
		return service.fecthAllAccounts();
	}
	
	@PutMapping("accountchange/{acno}")
	public ResponseStructure<BankAccount> changestatus(@PathVariable long acno)
	{
		return service.changestatus(acno); 
		
	}
	

}
