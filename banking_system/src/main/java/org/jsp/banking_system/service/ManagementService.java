package org.jsp.banking_system.service;

import java.util.List;
import java.util.Optional;

import org.jsp.banking_system.dto.BankAccount;
import org.jsp.banking_system.dto.Customer;
import org.jsp.banking_system.dto.Management;
import org.jsp.banking_system.exception.MyException;
import org.jsp.banking_system.helper.ResponseStructure;
import org.jsp.banking_system.repository.BankAccounts;
import org.jsp.banking_system.repository.ManagementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class ManagementService {

	@Autowired
	ManagementRepository repository;
	
	@Autowired
	BankAccounts repository2;

	public ResponseStructure<Management> savemanagement(Management management) {
		ResponseStructure<Management> structure=new ResponseStructure<>();
		structure.setCode(HttpStatus.CREATED.value());
		structure.setMsg("Account created succesfully");
		structure.setData(repository.save(management));

		return structure;
	}

	public ResponseStructure<Management> login(Management management) throws MyException {
		ResponseStructure<Management> structure=new ResponseStructure<>();
		  Management management1=repository.findByEmail(management.getEmail());
		  if(management1==null) {
				throw new MyException("Invalid Management Email");
			}else {
				if(management1.getPassword().equals(management.getPassword()))
				{

						structure.setCode(HttpStatus.ACCEPTED.value());
						structure.setMsg("Login success");
						structure.setData(management1);
				}
				else {
					throw new MyException("Inavalid password");
				}
			}
		
		return structure;
		
	}

	public ResponseStructure<List<BankAccount>> fecthAllAccounts() throws MyException {
		ResponseStructure<List<BankAccount>> structure=new ResponseStructure<List<BankAccount>>();
		List<BankAccount> list=repository2.findAll();
		if(list.isEmpty()) {
			throw new MyException("No accounts present");
		}else {
			structure.setCode(HttpStatus.FOUND.value());
			structure.setMsg("Data Found");
			structure.setData(list);
		}
		return structure;
	}

	public ResponseStructure<BankAccount> changestatus(long acno) {
		ResponseStructure<BankAccount> structure=new ResponseStructure<>();
		Optional<BankAccount> optional=repository2.findById(acno);
		BankAccount account=optional.get();
		if(account.isStatus())
		{
			account.setStatus(false);
		}else {
			account.setStatus(true);
		}
		structure.setCode(HttpStatus.OK.value());
		structure.setMsg("Changed status Success");
		structure.setData(repository2.save(account));
		
		return structure;
	}
	

}
