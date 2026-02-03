package com.wipro.bank.service;

import com.wipro.bank.bean.TransferBean;
import com.wipro.bank.dao.BankDAO;
import com.wipro.bank.util.InsufficientFundsException;

public class BankService {
	   BankDAO bankdao=new BankDAO();
	   public String checkBalance(String accountNumber) {
		   if(bankdao.validateAccount(accountNumber))
		   {
			   float balance=bankdao.findBalance(accountNumber);
			   return "BALANCE IS :"+balance;
		   }
		   else
		   {
			   return "ACCOUNT NUMBER IS INVALID";
		   }
	   }
	   public String transfer(TransferBean transferBean) {
		   
		   if(transferBean!=null) {
			   if(bankdao.validateAccount(transferBean.getFromAccountNumber())&&bankdao.validateAccount(transferBean.getToAccountNumber())) {
				   float fromAccountBalance=bankdao.findBalance(transferBean.getFromAccountNumber());
				   float toAccountBalance=bankdao.findBalance(transferBean.getToAccountNumber());
				   try {
				   if(fromAccountBalance>=transferBean.getAmount()) 
				   {
					   fromAccountBalance-=transferBean.getAmount();
					   toAccountBalance+=transferBean.getAmount();
					   bankdao.updateBalance(transferBean.getFromAccountNumber(), fromAccountBalance);
					   bankdao.updateBalance(transferBean.getToAccountNumber(), toAccountBalance);
					   bankdao.transferMoney(transferBean);
					   return "SUCCESS";
				   }
				   else {
					   throw new InsufficientFundsException();
				   }
				   }catch(InsufficientFundsException e) {
					   return e.toString();
				   }
			   }
			   else {
				   return "INVALID ACCOUNT";
			   }
		   }
		   return "INVALID";
	   }
	}