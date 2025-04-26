package me.calaritooo.cBanking.util.messages;

public enum Message {

    // general
    GENERAL_DEATH_LOSS_ALL("general.death-loss-all", "&7You lost your entire balance of &c%amt% &7upon dying."),
    GENERAL_DEATH_LOSS_PERCENTAGE("general.death-loss-percentage", "&7You lost &c%percentage%&7 of your total balance (&c%amt%&7) upon dying."),
    GENERAL_DEATH_LOSS_FLAT("general.death-loss-flat", "&7You lost &c%amt% &7upon dying."),

    // balance
    BALANCE_SELF("balance.self", "&7Balance: &a%currency-symbol%%amt%"),
    BALANCE_OTHER("balance.other", "&7%player%'s balance: &a%currency-symbol%%amt%"),

    // pay
    PAY_SENT("pay.sent", "&a%currency-symbol%%amt% &7has been sent to &a%recipient%&7."),
    PAY_RECEIVED("pay.received", "&7You have received &a%currency-symbol%%amt%&7 from &a%player%&7."),
    PAY_SAME_PLAYER("pay.same-player", "&cYou can't pay yourself!"),
    PAY_SUCCESS("pay.success", "&aPayment successful."),
    PAY_FAILED("pay.failed", "&cPayment failed."),

    // transaction
    TRANSACTION_DEPOSIT("transaction.deposit", "&aDeposited &a%currency-symbol%%amt%&7 into &6%bank%&a."),
    TRANSACTION_WITHDRAW("transaction.withdraw", "&aWithdrew &a%currency-symbol%%amt%&7 from &6%bank%&a."),
    TRANSACTION_FAIL_INSUFFICIENT_FUNDS("transaction.fail.insufficient-funds", "&cInsufficient funds."),
    TRANSACTION_FAIL_NO_ACCOUNT("transaction.fail.no-account", "&cYou don't have an account with &6%bank%&c."),
    TRANSACTION_FAIL_UNKNOWN("transaction.fail.unknown", "&cTransaction failed."),

    // bank
    BANK_CREATED("bank.created", "&aBank '&6%bank-name%&a' has been opened!"),
    BANK_EXISTS("bank.exists", "&cA bank with that name already exists."),
    BANK_DELETED("bank.deleted", "&cBank '&6%bank-name%&c' has been closed."),
    BANK_NOT_FOUND("bank.not-found", "&cThat bank doesn't exist."),
    BANK_INFO_HEADER("bank.info.header", "&7----- &eBank: &6%bank% &7-----"),
    BANK_INFO_OWNER("bank.info.owner", "&7Owner: &a%value%"),
    BANK_INFO_ASSETS("bank.info.assets", "&7Assets: &a%currency-symbol%%value%"),
    BANK_INFO_INTEREST("bank.info.interest", "&7Interest: &a%value%"),
    BANK_FEES_OPENING("bank.info.fees.opening", "&7Opening Fee: &a%currency-symbol%%value%"),
    BANK_FEES_MAINTENANCE("bank.info.fees.maintenance", "&7Maintenance Fee: &a%currency-symbol%%value%"),
    BANK_FEES_DEPOSIT("bank.info.fees.deposit", "&7Deposit Fee: &a%value%%"),
    BANK_FEES_WITHDRAWAL("bank.info.fees.withdrawal", "&7Withdrawal Fee: &a%value%%"),
    BANK_SETTING_UPDATED("bank.setting-updated", "&a%setting% updated to &a%value%."),

    // account
    ACCOUNT_CREATED("account.created", "&7Account opened with &6%bank%&7 with a balance of &a%currency-symbol%%amt%&7!"),
    ACCOUNT_EXISTS("account.exists", "&cYou already have an account with &6%bank%&c."),
    ACCOUNT_NOT_FOUND("account.not-found", "&cNo account found with &6%bank%&c."),
    ACCOUNT_DELETED("account.deleted", "&cAccount with &6%bank% &chas been deleted."),
    ACCOUNT_BALANCE("account.balance", "&7Account balance with &6%bank%&7: &a%currency-symbol%%amt%"),
    ACCOUNT_OTHER_BALANCE("account.other-balance", "&7%player%'s account with &6%bank%&7: &a%currency-symbol%%amt%"),

    // loan
    LOAN_NOT_IMPLEMENTED("loan.not-implemented", "&cLoan system is not available yet."),

    // error
    ERROR_INVALID_AMOUNT("error.invalid-amount", "&cInvalid amount."),
    ERROR_INVALID_PLAYER("error.invalid-player", "&cPlayer not found."),
    ERROR_NOT_PLAYER("error.not-player", "&cOnly players may do this!"),
    ERROR_NO_PERMISSION("error.no-permission", "&cYou don't have access to this command!"),

    // usage
    USAGE_BALANCE_CMD("usage.balance", "&7Usage: /balance <player>"),
    USAGE_PAY_CMD("usage.pay", "&7Usage: /pay <player> <amount>");

    private final String path;
    private final String defaultMessage;

    Message(String path, String defaultMessage) {
        this.path = path;
        this.defaultMessage = defaultMessage;
    }

    public String path() {
        return path;
    }

    public String defaultMessage() {
        return defaultMessage;
    }

    @Override
    public String toString() {
        return path;
    }
}