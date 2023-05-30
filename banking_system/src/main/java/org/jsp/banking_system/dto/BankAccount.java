package org.jsp.banking_system.dto;

import java.util.List;

import org.springframework.stereotype.Component;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import lombok.Data;

@Data
@Entity
@Component
public class BankAccount {
	@Id
	@SequenceGenerator(name="acno",sequenceName = "acno" ,initialValue = 1002121111,allocationSize = 1)
	@GeneratedValue(generator = "acno")
	long number;
	String type;
	double Banklimit;
	double amount;
	boolean status;
	
	
	@OneToMany(cascade = CascadeType.ALL)
	List<BankTrasaction> bankTrasactions;

}
