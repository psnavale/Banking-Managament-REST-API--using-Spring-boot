package org.jsp.banking_system.repository;

import org.jsp.banking_system.dto.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankAccounts extends JpaRepository<BankAccount, Long> {

}
