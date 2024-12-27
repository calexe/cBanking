package me.github.calaritooo;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;

public class BankManager {
    private List<Bank> banks;

    public BankManager() {
        banks = new ArrayList<>();
    }
    public void createBank(Bank bank) {
        banks.add(bank);
    }

    public Bank findBankByName(String name) {
        for (Bank bank : banks) {
            if (bank.getName().equals(name)) {
                return bank;
            } else {
                System.out.println("Invalid entry! Bank not found");
                return null;
            }
        }
        return null;
    }

    public void displayAllBanks() {
        for (Bank bank : banks) {
            System.out.println("Bank name: " + bank.getName());
            bank.displayAllAccounts();
            System.out.println();
        }
    }
}
