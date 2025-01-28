package me.calaritooo.cBanking.banks;

import java.util.Map;

public interface BankInterface {

    // BANK ID IS MAIN FORM OF BANK RECOGNITION
    void createBank(String bankID, String bankName, String ownerName);
    void deleteBank(String bankID);
    Map<String, Double> deleteBankAndTransferBalances(String bankID);
    boolean bankExists(String bankID);
    String getBankIDByName(String bankName);
    String getBankIDByOwner(String bankOwner);
    String getBankNameByID(String bankID);
    boolean bankNameExists(String bankName);
    String getBankOwnerByID(String bankID);
    void setBankName(String bankID, String newBankName);
    void setBankOwner(String bankID, String newOwner);
    double getAssets(String bankID);
    void setAssets(String bankID, double amount);
    double getInterestRate(String bankID);
    void setInterestRate(String bankID, double rate);
    double getAccountGrowthRate(String bankID);
    void setAccountGrowthRate(String bankID, double rate);
    double getAccountOpeningFee(String bankID);
    void setAccountOpeningFee(String bankID, double fee);
    double getMaintenanceFeeRate(String bankID);
    void setMaintenanceFeeRate(String bankID, double rate);
    double getDepositFeeRate(String bankID);
    void setDepositFeeRate(String bankID, double fee);
    double getWithdrawalFeeRate(String bankID);
    void setWithdrawalFeeRate(String bankID, double fee);
}
