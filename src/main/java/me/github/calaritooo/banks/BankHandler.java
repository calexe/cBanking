package me.github.calaritooo.banks;

import me.github.calaritooo.cBanking;
import me.github.calaritooo.data.BankDataHandler;

import java.util.Map;

public class BankHandler implements BankInterface {
    private final cBanking plugin;
    private final BankDataHandler bankDataHandler;
    private String bankID;
    private String bankName;
    private String ownerName;
    private double assets;
    private double interestRate;
    private double accountGrowthRate;
    private double maintenanceFeeRate;
    private double depositFeeRate;
    private double withdrawalFeeRate;

    // Constructor
    public BankHandler(cBanking plugin, String bankID, String bankName, String ownerName) {
        this.plugin = plugin;
        this.bankDataHandler = new BankDataHandler(plugin);
        this.bankID = bankID;
        this.bankName = bankName;
        this.ownerName = ownerName;
        setDefaultValues();
    }

    private void setDefaultValues() {
        this.assets = plugin.getConfig().getDouble("bank-settings.new-bank-assets");
        this.interestRate = plugin.getConfig().getDouble("loan-settings.default-interest-rate");
        this.accountGrowthRate = plugin.getConfig().getDouble("bank-settings.default-acct-growth-rate");
        this.maintenanceFeeRate = plugin.getConfig().getDouble("bank-settings.default-maintenance-fee");
        this.depositFeeRate = plugin.getConfig().getDouble("bank-settings.default-deposit-fee-rate");
        this.withdrawalFeeRate = plugin.getConfig().getDouble("bank-settings.default-withdrawal-fee-rate");
    }

    @Override
    public void createBank(String bankID, String bankName, String ownerName) {
        this.bankID = bankID;
        this.bankName = bankName;
        this.ownerName = ownerName;
        setDefaultValues();

        bankDataHandler.saveBankData(bankID, this);
    }

    @Override
    public void deleteBank(String bankID) {
        bankDataHandler.deleteBankData(bankID);
    }

    @Override
    public String getBankID(String bankName) {
        return bankDataHandler.loadAllBanks().entrySet().stream()
                .filter(entry -> entry.getValue().getBankName().equals(bankName))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }

    @Override
    public String getBankName(String bankID) {
        BankHandler bank = bankDataHandler.loadBankData(bankID);
        return bank != null ? bank.getBankName() : null;
    }

    @Override
    public String getBankOwnerByID(String bankID) {
        BankHandler bank = bankDataHandler.loadBankData(bankID);
        return bank != null ? bank.getOwnerName() : null;
    }

    @Override
    public double getAssets(String bankID) {
        BankHandler bank = bankDataHandler.loadBankData(bankID);
        return bank != null ? bank.getAssets() : 0;
    }

    @Override
    public void setAssets(String bankID, double amount) {
        BankHandler bank = bankDataHandler.loadBankData(bankID);
        if (bank != null) {
            bank.setAssets(amount);
            bankDataHandler.saveBankData(bankID, bank);
        }
    }

    @Override
    public double getInterestRate(String bankID) {
        BankHandler bank = bankDataHandler.loadBankData(bankID);
        return bank != null ? bank.getInterestRate() : 0;
    }

    @Override
    public void setInterestRate(String bankID, double rate) {
        BankHandler bank = bankDataHandler.loadBankData(bankID);
        if (bank != null) {
            bank.setInterestRate(rate);
            bankDataHandler.saveBankData(bankID, bank);
        }
    }

    @Override
    public double getAccountGrowthRate(String bankID) {
        BankHandler bank = bankDataHandler.loadBankData(bankID);
        return bank != null ? bank.getAccountGrowthRate() : 0;
    }

    @Override
    public void setAccountGrowthRate(String bankID, double rate) {
        BankHandler bank = bankDataHandler.loadBankData(bankID);
        if (bank != null) {
            bank.setAccountGrowthRate(rate);
            bankDataHandler.saveBankData(bankID, bank);
        }
    }

    @Override
    public double getMaintenanceFeeRate(String bankID) {
        BankHandler bank = bankDataHandler.loadBankData(bankID);
        return bank != null ? bank.getMaintenanceFeeRate() : 0;
    }

    @Override
    public void setMaintenanceFeeRate(String bankID, double rate) {
        BankHandler bank = bankDataHandler.loadBankData(bankID);
        if (bank != null) {
            bank.setMaintenanceFeeRate(rate);
            bankDataHandler.saveBankData(bankID, bank);
        }
    }

    @Override
    public double getDepositFeeRate(String bankID) {
        BankHandler bank = bankDataHandler.loadBankData(bankID);
        return bank != null ? bank.getDepositFeeRate() : 0;
    }

    @Override
    public void setDepositFeeRate(String bankID, double fee) {
        BankHandler bank = bankDataHandler.loadBankData(bankID);
        if (bank != null) {
            bank.setDepositFeeRate(fee);
            bankDataHandler.saveBankData(bankID, bank);
        }
    }

    @Override
    public double getWithdrawalFeeRate(String bankID) {
        BankHandler bank = bankDataHandler.loadBankData(bankID);
        return bank != null ? bank.getWithdrawalFeeRate() : 0;
    }

    @Override
    public void setWithdrawalFeeRate(String bankID, double fee) {
        BankHandler bank = bankDataHandler.loadBankData(bankID);
        if (bank != null) {
            bank.setWithdrawalFeeRate(fee);
            bankDataHandler.saveBankData(bankID, bank);
        }
    }

    public String getBankID() {
        return bankID;
    }

    public String getBankName() {
        return bankName;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public double getAssets() {
        return assets;
    }

    public void setAssets(double assets) {
        this.assets = assets;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }

    public double getAccountGrowthRate() {
        return accountGrowthRate;
    }

    public void setAccountGrowthRate(double accountGrowthRate) {
        this.accountGrowthRate = accountGrowthRate;
    }

    public double getMaintenanceFeeRate() {
        return maintenanceFeeRate;
    }

    public void setMaintenanceFeeRate(double maintenanceFeeRate) {
        this.maintenanceFeeRate = maintenanceFeeRate;
    }

    public double getDepositFeeRate() {
        return depositFeeRate;
    }

    public void setDepositFeeRate(double depositFeeRate) {
        this.depositFeeRate = depositFeeRate;
    }

    public double getWithdrawalFeeRate() {
        return withdrawalFeeRate;
    }

    public void setWithdrawalFeeRate(double withdrawalFeeRate) {
        this.withdrawalFeeRate = withdrawalFeeRate;
    }
}