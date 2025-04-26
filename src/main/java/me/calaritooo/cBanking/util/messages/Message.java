package me.calaritooo.cBanking.util.messages;

public enum Message {

    // general
    GENERAL_DEATH_LOSS_ALL("general.death-loss-all"),
    GENERAL_DEATH_LOSS_PERCENTAGE("general.death-loss-percentage"),
    GENERAL_DEATH_LOSS_FLAT("general.death-loss-flat"),

    // balance
    BALANCE_SELF("balance.self"),
    BALANCE_OTHER("balance.other"),

    // pay
    PAY_SENT("pay.sent"),
    PAY_RECEIVED("pay.received"),
    PAY_SAME_PLAYER("pay.same-player"),
    PAY_SUCCESS("pay.success"),
    PAY_FAILED("pay.failed"),

    // transaction
    TRANSACTION_DEPOSIT("transaction.deposit"),
    TRANSACTION_WITHDRAW("transaction.withdraw"),
    TRANSACTION_FAIL_INSUFFICIENT_FUNDS("transaction.fail.insufficient-funds"),
    TRANSACTION_FAIL_NO_ACCOUNT("transaction.fail.no-account"),
    TRANSACTION_FAIL_UNKNOWN("transaction.fail.unknown"),

    // bank
    BANK_CREATED("bank.created"),
    BANK_EXISTS("bank.exists"),
    BANK_DELETED("bank.deleted"),
    BANK_NOT_FOUND("bank.not-found"),
    BANK_INFO_HEADER("bank.info.header"),
    BANK_INFO_OWNER("bank.info.owner"),
    BANK_INFO_ASSETS("bank.info.assets"),
    BANK_INFO_INTEREST("bank.info.interest"),
    BANK_FEES_OPENING("bank.info.fees.opening"),
    BANK_FEES_MAINTENANCE("bank.info.fees.maintenance"),
    BANK_FEES_DEPOSIT("bank.info.fees.deposit"),
    BANK_FEES_WITHDRAWAL("bank.info.fees.withdrawal"),
    BANK_SETTING_UPDATED("bank.setting-updated"),

    // account
    ACCOUNT_CREATED("account.created"),
    ACCOUNT_EXISTS("account.exists"),
    ACCOUNT_NOT_FOUND("account.not-found"),
    ACCOUNT_DELETED("account.deleted"),
    ACCOUNT_BALANCE("account.balance"),
    ACCOUNT_OTHER_BALANCE("account.other-balance"),

    // loan
    LOAN_NOT_IMPLEMENTED("loan.not-implemented"),

    // error
    ERROR_INVALID_AMOUNT("error.invalid-amount"),
    ERROR_INVALID_PLAYER("error.invalid-player"),
    ERROR_NOT_PLAYER("error.not-player"),
    ERROR_NO_PERMISSION("error.no-permission"),

    // usage
    USAGE_BALANCE_CMD("usage.balance"),
    USAGE_PAY_CMD("usage.pay");


    private final String path;

    Message(String path) {
        this.path = path;
    }

    public String path() {
        return path;
    }

    @Override
    public String toString() {
        return path;
    }
}