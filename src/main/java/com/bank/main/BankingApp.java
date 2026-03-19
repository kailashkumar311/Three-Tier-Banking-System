package com.bank.main;

import com.bank.dao.AccountDAO;
import com.bank.dao.UserDAO;
import com.bank.exception.InsufficientBalanceException;
import com.bank.model.Transaction;
import com.bank.model.User;

import java.util.List;
import java.util.Scanner;

public class BankingApp {
   void main() {
        Scanner scanner = new Scanner(System.in);
        UserDAO userDAO = new UserDAO();
        AccountDAO accountDAO = new AccountDAO(userDAO);

        System.out.println("==========================================================================");
        System.out.println("                    WELCOME TO THE BANKING SYSTEM");
        System.out.println("==========================================================================");

        while (true) {
            System.out.println("\n1. Register New Account");
            System.out.println("2. Login to Account");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    System.out.print("Enter Full Name: ");
                    String name = scanner.nextLine().trim();
                    if (name.isEmpty()) {
                        System.out.println("Error: Name cannot be empty!");
                        break;
                    }

                    System.out.print("Enter 4-digit Security PIN: ");
                    String pin = scanner.nextLine();
                    if (!pin.matches("\\d{4}")) {
                        System.out.println("Error: PIN must be exactly 4 digits!");
                        break;
                    }

                    System.out.print("Enter Initial Deposit (Min Rs. 500): ");
                    double deposit = scanner.nextDouble();
                    if (deposit < 500) {
                        System.out.println("Error: Minimum initial deposit is Rs. 500!");
                        break;
                    }
                    // ... register logic

                    // Generate unique 10-digit account number based on timestamp
                    long accNumber = System.currentTimeMillis() % 10000000000L;
                    if (accNumber < 1000000000L) {
                        accNumber += 1000000000L;
                    }

                    User user = new User(accNumber, name, pin, deposit);
                    if (userDAO.register(user)) {
                        System.out.println("Registration Successful!");
                        System.out.println("Your Account Number is: " + accNumber);
                    } else {
                        System.out.println("Registration Failed!");
                    }
                    break;

                case 2:
                    System.out.print("Enter Account Number: ");
                    long loginAcc = scanner.nextLong();
                    scanner.nextLine();

                    System.out.print("Enter Security PIN: ");
                    String loginPin = scanner.nextLine();

                    User loggedInUser = userDAO.login(loginAcc, loginPin);

                    if (loggedInUser != null) {
                        System.out.println("\nLogin Successful! Welcome, " + loggedInUser.getFullName());
                        boolean loggedIn = true;

                        while (loggedIn) {
                            System.out.println("\n--- Account Menu ---");
                            System.out.println("1. Check Balance");
                            System.out.println("2. Deposit Money");
                            System.out.println("3. Withdraw Money");
                            System.out.println("4. Transfer Money");
                            System.out.println("5. Mini Statement");
                            System.out.println("6. Logout");
                            System.out.print("Enter choice: ");

                            int actChoice = scanner.nextInt();

                            switch (actChoice) {
                                case 1:
                                    double bal = userDAO.getBalance(loginAcc);
                                    System.out.println("Current Balance: Rs. " + bal);
                                    break;

                                case 2: // Deposit
                                    System.out.print("Enter amount to deposit: ");
                                    double depAmount = scanner.nextDouble();
                                    if (depAmount <= 0) {
                                        System.out.println("Error: Deposit amount must be positive!");
                                    } else {
                                        if(accountDAO.deposit(loginAcc, depAmount)) {
                                            System.out.println("Deposit Successful!");
                                        }
                                    }
                                    break;

                                case 3:
                                    System.out.print("Enter amount to withdraw: ");
                                    double withdrawAmt = scanner.nextDouble();
                                    try {
                                        if (accountDAO.withdraw(loginAcc, withdrawAmt)) {
                                            System.out.println("Withdrawal Successful!");
                                        }
                                    } catch (InsufficientBalanceException e) {
                                        System.out.println(e.getMessage());
                                    }
                                    break;

                                case 4:
                                    System.out.print("Enter Receiver Account Number: ");
                                    long receiverAcc = scanner.nextLong();
                                    System.out.print("Enter Amount to Transfer: ");
                                    double transferAmt = scanner.nextDouble();

                                    try {
                                        if (accountDAO.transferMoney(loginAcc, receiverAcc, transferAmt)) {
                                            System.out.println("Transfer Successful!");
                                        } else {
                                            System.out.println("Transfer Failed!");
                                        }
                                    } catch (InsufficientBalanceException e) {
                                        System.out.println(e.getMessage());
                                    }
                                    break;

                                case 5:
                                    List<Transaction> miniStmt = accountDAO.getMiniStatement(loginAcc);
                                    System.out.println("\n--- Mini Statement ---");
                                    if (miniStmt.isEmpty()) {
                                        System.out.println("No transactions found.");
                                    } else {
                                        System.out.println("+---------------+---------------+--------------+--------------+-------------------------+");
                                        System.out.printf("| %-13s | %-13s | %-12s | %-12s | %-23s | \n",
                                                "Sender", "Receiver", "Amount", "Type", "Date/Time");
                                        System.out.println("+---------------+---------------+--------------+--------------+-------------------------+");

                                        for (Transaction t : miniStmt) {
                                            // if account is default in case of deposite or withdraw
                                            String sender = (t.getSenderAcc() == null) ? "CASH/SELF" : String.valueOf(t.getSenderAcc());

                                            String receiver = (t.getReceiverAcc() == null) ? "CASH/SELF" : String.valueOf(t.getReceiverAcc());
                                            System.out.printf("| %-13s | %-13s | %-12s | %-12s | %-23s | \n",
                                                     sender, receiver ,t.getAmount(),t.getType(), t.getTimestamp().toString() );
                                        }
                                        System.out.println("+---------------+---------------+--------------+--------------+-------------------------+");

                                    }
                                    break;

                                case 6:
                                    loggedIn = false;
                                    System.out.println("Logged out successfully.");
                                    break;

                                default:
                                    System.out.println("Invalid choice!");
                            }
                        }
                    } else {
                        System.out.println("Invalid Account Number or PIN!");
                    }
                    break;

                case 3:
                    System.out.println("Exiting System. Thank you!");
                    scanner.close();
                    System.exit(0);
                    break;

                default:
                    System.out.println("Invalid choice! Please try again.");
            }
        }
    }
}
