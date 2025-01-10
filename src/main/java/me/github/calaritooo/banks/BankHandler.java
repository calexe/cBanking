package me.github.calaritooo.banks;

import me.github.calaritooo.cBanking;
import me.github.calaritooo.data.BankDataHandler;

import java.util.Set;

public class BankHandler implements BankInterface {
    private final cBanking plugin;
    private BankDataHandler bankDataHandler;

    public BankHandler(cBanking plugin) {
        this.plugin = plugin;
    }

    public void initializeHandlers(BankDataHandler bankDataHandler) {
        this.bankDataHandler = new BankDataHandler(plugin);
    }

    @Override
    public void createBank(String bankID, String bankName, String ownerName) {
        double assets = plugin.getConfig().getDouble("bank-settings.new-bank-assets");
        double interestRate = plugin.getConfig().getDouble("loan-settings.default-interest-rate");
        double accountGrowthRate = plugin.getConfig().getDouble("bank-settings.default-acct-growth-rate");
        double accountOpeningFee = plugin.getConfig().getDouble("bank-settings.default-acct-opening-fee");
        double maintenanceFeeRate = plugin.getConfig().getDouble("bank-settings.default-maintenance-fee");
        double depositFeeRate = plugin.getConfig().getDouble("bank-settings.default-deposit-fee-rate");
        double withdrawalFeeRate = plugin.getConfig().getDouble("bank-settings.default-withdrawal-fee-rate");
        bankDataHandler.saveBankData(bankID, bankName, ownerName, assets, interestRate, accountGrowthRate, accountOpeningFee, maintenanceFeeRate, depositFeeRate, withdrawalFeeRate);
    }

    @Override
    public void deleteBank(String bankID) {
        bankDataHandler.deleteBankData(bankID);
    }

    @Override
    public void deleteBankAndTransferBalances(String bankID) {
        bankDataHandler.deleteBankAndTransferBalances(bankID);
    }

    @Override
    public boolean bankExists(String bankID) {
        return bankDataHandler.getBanksConfig().getString("banks. " + bankID) != null;
    }

    public Set<String> getBankIDs() {
        return bankDataHandler.getBanksConfig().getConfigurationSection("banks").getKeys(false);
    }

    @Override
    public String getBankIDByName(String bankName) {
        return bankDataHandler.getBanksConfig().getConfigurationSection("banks").getKeys(false).stream()
                .filter(id -> bankDataHandler.getBanksConfig().getString("banks." + id + ".bankName").equalsIgnoreCase(bankName))
                .findFirst()
                .orElse(null);
    }

    @Override
    public String getBankIDByOwner(String bankOwner) {
        return bankDataHandler.getBanksConfig().getConfigurationSection("banks").getKeys(false).stream()
                .filter(id -> bankDataHandler.getBanksConfig().getString("banks." + id + ".ownerName").equalsIgnoreCase(bankOwner))
                .findFirst()
                .orElse(null);
    }

    @Override
    public String getBankNameByID(String bankID) {
        return bankDataHandler.getBanksConfig().getString("banks." + bankID + ".bankName");
    }

    @Override
    public String getBankOwnerByID(String bankID) {
        return bankDataHandler.getBanksConfig().getString("banks." + bankID + ".ownerName");
    }

    @Override
    public void setBankName(String bankID, String newBankName) {
        bankDataHandler.getBanksConfig().set("banks." + bankID + ".bankNamne", newBankName);
        bankDataHandler.saveBanksConfig();
    }

    @Override
    public void setBankOwner(String bankID, String newOwner) {
        bankDataHandler.getBanksConfig().set("banks." + bankID + ".ownerName", newOwner);
        bankDataHandler.saveBanksConfig();
    }

    @Override
    public double getAssets(String bankID) {
        return bankDataHandler.getBanksConfig().getDouble("banks." + bankID + ".assets");
    }

    @Override
    public void setAssets(String bankID, double amount) {
        bankDataHandler.getBanksConfig().set("banks." + bankID + ".assets", amount);
        bankDataHandler.saveBanksConfig();
    }

    @Override
    public double getInterestRate(String bankID) {
        return bankDataHandler.getBanksConfig().getDouble("banks." + bankID + ".interestRate");
    }

    @Override
    public void setInterestRate(String bankID, double rate) {
        bankDataHandler.getBanksConfig().set("banks." + bankID + ".interestRate", rate);
        bankDataHandler.saveBanksConfig();
    }

    @Override
    public double getAccountGrowthRate(String bankID) {
        return bankDataHandler.getBanksConfig().getDouble("banks." + bankID + ".accountGrowthRate");
    }

    @Override
    public void setAccountGrowthRate(String bankID, double rate) {
        bankDataHandler.getBanksConfig().set("banks." + bankID + ".accountGrowthRate", rate);
        bankDataHandler.saveBanksConfig();
    }

    @Override
    public double getAccountOpeningFee(String bankID) {
        return bankDataHandler.getBanksConfig().getDouble("banks." + bankID + ".accountOpeningFee");
    }

    @Override
    public void setAccountOpeningFee(String bankID, double fee) {
        bankDataHandler.getBanksConfig().set("banks." + bankID + ".accountOpeningFee", fee);
        bankDataHandler.saveBanksConfig();
    }

    @Override
    public double getMaintenanceFeeRate(String bankID) {
        return bankDataHandler.getBanksConfig().getDouble("banks." + bankID + ".maintenanceFeeRate");
    }

    @Override
    public void setMaintenanceFeeRate(String bankID, double rate) {
        bankDataHandler.getBanksConfig().set("banks." + bankID + ".maintenanceFeeRate", rate);
        bankDataHandler.saveBanksConfig();
    }

    @Override
    public double getDepositFeeRate(String bankID) {
        return bankDataHandler.getBanksConfig().getDouble("banks." + bankID + ".depositFeeRate");
    }

    @Override
    public void setDepositFeeRate(String bankID, double fee) {
        bankDataHandler.getBanksConfig().set("banks." + bankID + ".depositFeeRate", fee);
        bankDataHandler.saveBanksConfig();
    }

    @Override
    public double getWithdrawalFeeRate(String bankID) {
        return bankDataHandler.getBanksConfig().getDouble("banks." + bankID + ".withdrawalFeeRate");
    }

    @Override
    public void setWithdrawalFeeRate(String bankID, double fee) {
        bankDataHandler.getBanksConfig().set("banks." + bankID + ".withdrawalFeeRate", fee);
        bankDataHandler.saveBanksConfig();
    }
}