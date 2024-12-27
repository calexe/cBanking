package me.github.calaritooo;

import java.util.Scanner;

// First, we create the class "account". This is a class because it has various moving and changing parts inside. //
public class Account {

    // We declare our variables first to be initialized in the constructor. //
    private String accountHolder;
    private String accountNumber;
    private double balance;

    /* Then, we create our constructor to apply the given parameters to every new Account type object's variables
     * as declared above.
     * Every time an account is made, "new Account(accountHolder, accountNumber, balance);", the values are applied. */
    public Account(String accountHolder, String accountNumber, double initialBalance) {
        this.accountHolder = accountHolder;
        this.accountNumber = accountNumber;
        this.balance = initialBalance;
    }

    // Here, we begin to create the methods, or "functions", that our class will hold. //
    public void deposit(double amount) {
        Scanner scanner = new Scanner(System.in);
        amount = scanner.nextDouble();
        while (amount <= 0) {
            System.out.println("Invalid entry! Select another amount.");
            amount = scanner.nextDouble();
        }
        balance += amount;
        System.out.println("Updated balance: $" + balance);
    }

    public void withdraw(double amount) {
        Scanner scanner = new Scanner(System.in);
        scanner.nextDouble();
        while (amount <= 0 || balance < amount) {
            System.out.println("Invalid entry! Select another amount.");
            amount = scanner.nextDouble();
        }
        balance -= amount;
        System.out.println("Updated balance: $" + balance);
    }

    public void getAccountDetails() {
        System.out.println("Account Number: " + accountNumber);
        System.out.println("Account Holder: " + accountHolder);
        System.out.println("Balance: $" + balance);
    }

    // Getters and setters are added here. //
    public String getAccountNumber() {
        return accountNumber;
    }

    public String getAccountHolder() {
        return accountHolder;
    }

    public double getBalance() {
        return balance;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public void setAccountHolder(String accountHolder) {
        this.accountHolder = accountHolder;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}
