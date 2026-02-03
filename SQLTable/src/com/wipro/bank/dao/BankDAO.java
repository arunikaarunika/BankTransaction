package com.wipro.bank.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.wipro.bank.bean.TransferBean;
import com.wipro.bank.util.DBUtil;

public class BankDAO {

	    public int generateSequenceNumber() {
	        try (Connection con = DBUtil.getDBConnection();
	             PreparedStatement ps =
	                 con.prepareStatement("SELECT transactionId_seq.NEXTVAL FROM dual");
	             ResultSet rs = ps.executeQuery()) {

	            rs.next();
	            return rs.getInt(1);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        return 0;
	    }

	    public boolean validateAccount(String accountNumber) {
	        try (Connection con = DBUtil.getDBConnection();
	             PreparedStatement ps =
	                 con.prepareStatement("SELECT 1 FROM ACCOUNT_TBL WHERE Account_Number=?")) {

	            ps.setString(1, accountNumber);
	            ResultSet rs = ps.executeQuery();
	            return rs.next();
	        } catch (Exception e) {
	            return false;
	        }
	    }

	    public float findBalance(String accountNumber) {
	        try (Connection con = DBUtil.getDBConnection();
	             PreparedStatement ps =
	                 con.prepareStatement("SELECT Balance FROM ACCOUNT_TBL WHERE Account_Number=?")) {

	            ps.setString(1, accountNumber);
	            ResultSet rs = ps.executeQuery();
	            if (rs.next())
	                return rs.getFloat(1);
	        } catch (Exception e) {}
	        return -1;
	    }

	    public boolean updateBalance(String accountNumber, float newBalance) {
	        try (Connection con = DBUtil.getDBConnection();
	             PreparedStatement ps =
	                 con.prepareStatement(
	                     "UPDATE ACCOUNT_TBL SET Balance=? WHERE Account_Number=?")) {

	            ps.setFloat(1, newBalance);
	            ps.setString(2, accountNumber);
	            return ps.executeUpdate() > 0;
	        } catch (Exception e) {
	            return false;
	        }
	    }

	    public boolean transferMoney(TransferBean tb) {
	        try (Connection con = DBUtil.getDBConnection()) {

	            con.setAutoCommit(false);

	            float fromBal = findBalance(tb.getFromAccountNumber());
	            float toBal   = findBalance(tb.getToAccountNumber());

	            if (fromBal < tb.getAmount())
	                throw new Exception("Insufficient Balance");

	            updateBalance(tb.getFromAccountNumber(), fromBal - tb.getAmount());
	            updateBalance(tb.getToAccountNumber(), toBal + tb.getAmount());

	            String query =
	            		  "INSERT INTO TRANSFER_TBL " +
	            		  "(Transaction_ID, Account_Number, Beneficiary_Account_Number, Transaction_Date, Transaction_Amount) " +
	            		  "VALUES (?,?,?,?,?)";


	            PreparedStatement ps = con.prepareStatement(query);
	            ps.setInt(1, generateSequenceNumber());
	            ps.setString(2, tb.getFromAccountNumber());
	            ps.setString(3, tb.getToAccountNumber());
	            ps.setDate(4, new java.sql.Date(tb.getDateOfTransaction().getTime()));
	            ps.setFloat(5, tb.getAmount());

	            ps.executeUpdate();
	            con.commit();
	            return true;

	        } catch (Exception e) {
	            e.printStackTrace();
	            return false;
	        }
	    }
	}
