package SamplePackage;

import java.sql.*;
import java.util.Scanner;

public class Main {
	private static final String url = "jdbc:mysql://localhost:3306/ATMInterface";
	private static final String username = "root";
	private static final String password = "root";
	
	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);
		try {
			boolean flag = false;
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection conn = DriverManager.getConnection(url, username, password);
			
			System.out.print("Enter Your ACCOUNT Number : ");
			long accountNumber = scan.nextLong();
			System.out.print("Enter your 4 digit PIN : ");
			int pin = scan.nextInt();
			
			boolean logIn = logIn(conn, accountNumber, pin);
			while(logIn==true) {
				System.out.println("0.Exit\t 1..withDraw\t 2.deposit\t 3.trasfer\n");
				System.out.print("Enter Your Choice : ");
				int choice = scan.nextInt();
				switch(choice) {
					case 0 : logIn = false;break;
					case 1 : withDraw(scan,conn,accountNumber);break;
					case 2 : deposit(scan, conn, accountNumber);break;
					case 3 : transfer(scan, conn, accountNumber);break;
					default : System.out.println("Invalid Choice - try Again");
				}
				if(logIn == false) {
					System.out.println("Sucessfully Exited");
				}
			}
		}
		catch(Exception e) {
			System.out.println("Error in DataBase Connection");
		}

	}
	public static void transactionHistory(Connection conn, long accountNumber) {
		
	}
	public static boolean logIn(Connection conn, long accountNumber, int pin) {
		
		try {
			PreparedStatement state = conn.prepareStatement("select count(*) as count from userDetails where accountNumber = ? and pinNumber = ?;");
			state.setLong(1, accountNumber);
			state.setInt(2, pin);
			ResultSet set = state.executeQuery();
			set.next();
			if(set.getInt("count") > 0) {
				System.out.println("SUCESSFULLY ENTERED INTO YOUR aCCOUNT");
				return true;
			}
			else {
				System.out.println("Invalid Credentials - try Again");
				return false;
			}
		}
		catch(Exception e) {
			System.out.println("Error in connecting to data base : "+e);
			return false;
		}
	}
	public static void withDraw(Scanner scan, Connection conn, long accountNumber) {
		System.out.print("Enter Amount to WithDraw : ");
		long amount = scan.nextLong();
		try {
			PreparedStatement state = conn.prepareStatement("select Amount from userDetails where accountNumber = ?");
			state.setLong(1, accountNumber);
			ResultSet set = state.executeQuery();
			if(set.next()) {
				if(set.getLong(1) < amount) {
					System.out.println("Insufficient Amount");
				}
				else {
					PreparedStatement statement = conn.prepareStatement("update userDetails set amount = ? where accountNumber = ?;");
					statement.setLong(1, set.getLong(1) - amount);
					statement.setLong(2, accountNumber);
					statement.executeUpdate();
					System.out.println("WithDraw Sucessful");
				}
			}
		}
		catch(Exception e) {
			System.out.println("ERROR IN CONNECTING TO DATABASE - TRY AGAIN"+e);
		}
	}
	public static void deposit(Scanner scan, Connection conn, long accountNumber) {
		System.out.print("Enter Amount to credit : ");
		long amount = scan.nextLong();
		try {
			PreparedStatement statement = conn.prepareStatement("select amount from userDetails where accountNumber = ? ;");
			statement.setLong(1, accountNumber);
			ResultSet s = statement.executeQuery();
			s.next();
			
			PreparedStatement state = conn.prepareStatement("Update userDetails set amount = ? where accountNumber = ?;");
			state.setLong(1, amount+s.getLong(1));
			state.setLong(2, accountNumber);
			state.executeUpdate();
			System.out.println("AMOUNT SUCESSFULLY CREDITED INTO YOUR ACCOUNT");
			
		}
		catch(Exception e) {
			System.out.println("Error in connecting to data base "+e);
		}
	}
	
	public static void transfer(Scanner scan, Connection conn, long AccountNumber) {
		System.out.print("Enter Receiver's Account Number : ");
		long receiverAccountNumber = scan.nextLong();
		try {
			PreparedStatement state = conn.prepareStatement("Select amount from userDetails where AccountNumber  = ? ;");
			state.setLong(1, receiverAccountNumber);
			ResultSet set = state.executeQuery();
			if(set.next()) {
				System.out.print("Enter amount to transfer : ");
				long transferAmount = scan.nextLong();
				
				PreparedStatement s = conn.prepareStatement("select amount from userDetails where AccountNumber = ? ;");
				s.setLong(1, AccountNumber);
				ResultSet sett = s.executeQuery();
				sett.next();
				long senderAmount = sett.getLong(1);
				if(senderAmount < transferAmount) {
					System.out.println("Insufficient Amount in your Account");
				}
				else {
					String query = "Update userDetails set amount = ? where accountNumber = ? ;";
					PreparedStatement st = conn.prepareStatement(query);
					st.setLong(1, senderAmount-transferAmount);
					st.setLong(2, AccountNumber);
					st.executeUpdate();
					PreparedStatement sta = conn.prepareStatement(query);
					sta.setLong(1, set.getLong(1)+transferAmount);
					sta.setLong(2, receiverAccountNumber);
					sta.executeUpdate();
					System.out.println("AMOUNT SUCESSFULLY TRANSFERED");
				}
			}
			else {
				System.out.println("Invalid AccountNumber - try Again");
			}
		}
		catch(Exception e) {
			System.out.println("Error in connecting to dataBase : "+e);
		}
	}
	
}



/*
 Database Data
+---------------------------------+
| Account Number | PIN   | Amount |
|----------------|-------|--------|
| 123451234512   | 1212  | 1000   |
| 123456789012   | 1234  | 800    |
+---------------------------------+

*/