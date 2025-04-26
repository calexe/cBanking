package me.calaritooo.cBanking.util.configuration;

public enum ConfigurationOption {

    // ───── General Plugin Settings ─────
    LOG_TRANSACTIONS("plugin-settings.logging.enable-log-transactions", false),
    LOG_LOAN_REQUESTS("plugin-settings.logging.enable-log-loan-requests", false),
    ENABLE_ADMIN_MESSAGES("plugin-settings.notifications.enable-admin-messages", true),

    // ───── Modules ─────
    MODULE_BANKS("modules.enable-banks", true),
    MODULE_LOANS("modules.enable-loans", true),
    MODULE_DEATH_LOSS("modules.enable-death-loss", false),

    // ───── Economy Settings ─────
    ECONOMY_STARTING_BALANCE("economy-settings.starting-bal", 0),
    ECONOMY_CURRENCY_SYMBOL("economy-settings.currency-symbol", "ȼ"),
    ECONOMY_CURRENCY_NAME("economy-settings.currency-name", "Coin"),
    ECONOMY_CURRENCY_NAME_PLURAL("economy-settings.currency-name-plural", "Coins"),
    ECONOMY_RATE_PERIOD("economy-settings.rate-period", 48),
    ECONOMY_DEATH_LOSS_TYPE("economy-settings.death-loss-type", "percentage"),
    ECONOMY_DEATH_LOSS_VALUE("economy-settings.death-loss-value", 1),

    // ───── Bank Settings ─────
    BANK_CREATION_FEE("bank-settings.new-bank-fee", 250),
    BANK_CREATION_ASSETS("bank-settings.new-bank-assets", 500),
    BANK_MIN_ASSETS("bank-settings.min-bank-assets", 100),
    BANK_BANKRUPTCY("bank-settings.bankruptcy", false),
    BANK_CLOSURE_FEE_TYPE("bank-settings.bank-closure-fee-type", "player"),
    BANK_CLOSURE_FEE("bank-settings.bank-closure-fee", 20),
    BANK_DEFAULT_ACCOUNT_OPENING_FEE("bank-settings.default-acct-opening-fee", 10),
    BANK_MIN_ACCOUNT_OPENING_FEE("bank-settings.min-acct-opening-fee", 0),
    BANK_MAX_ACCOUNT_OPENING_FEE("bank-settings.max-acct-opening-fee", 50),
    BANK_MAINTENANCE_FEE_TYPE("bank-settings.maintenance-fee-type", "flat"),
    BANK_DEFAULT_MAINTENANCE_FEE("bank-settings.default-maintenance-fee-rate", 2),
    BANK_DEFAULT_ACCOUNT_GROWTH("bank-settings.default-acct-growth-rate", 2),
    BANK_MIN_BAL_FOR_GROWTH("bank-settings.min-acct-bal-for-growth", 50),
    BANK_MIN_ACCOUNT_GROWTH("bank-settings.min-acct-growth-rate", 0),
    BANK_MAX_ACCOUNT_GROWTH("bank-settings.max-acct-growth-rate", 15),
    BANK_TRANSACTION_FEE_TYPE("bank-settings.transaction-fee-type", "percentage"),
    BANK_DEFAULT_DEPOSIT_FEE("bank-settings.default-deposit-fee-rate", 0),
    BANK_MIN_DEPOSIT_FEE("bank-settings.min-deposit-rate", 0),
    BANK_MAX_DEPOSIT_FEE("bank-settings.max-deposit-rate", 15),
    BANK_DEFAULT_WITHDRAWAL_FEE("bank-settings.default-withdrawal-fee-rate", 2),
    BANK_MIN_WITHDRAWAL_FEE("bank-settings.min-withdrawal-rate", 0),
    BANK_MAX_WITHDRAWAL_FEE("bank-settings.max-withdrawal-rate", 15),

    // ───── Loan Settings ─────
    LOAN_DEFAULT_INTEREST_RATE("loan-settings.default-interest-rate", 2),
    LOAN_MIN_BAL_FOR_LOAN("loan-settings.min-acct-bal-for-loan", 50),
    LOAN_MIN_INTEREST("loan-settings.min-loan-interest-rate", 0),
    LOAN_MAX_INTEREST("loan-settings.max-loan-interest-rate", 15),
    LOAN_MAX_BY_ASSETS("loan-settings.max-loan-by-assets", true),
    LOAN_MIN_AMOUNT("loan-settings.min-loan-amt", 250),
    LOAN_MAX_AMOUNT("loan-settings.max-loan-amt", 500),
    LOAN_MAX_REQUESTS("loan-settings.max-loan-requests", 1),
    LOAN_MIN_PERIOD("loan-settings.min-loan-period", 1),
    LOAN_MAX_PERIOD("loan-settings.max-loan-period", 7);

    private final String path;
    private final Object defaultValue;

    ConfigurationOption(String path, Object defaultValue) {
        this.path = path;
        this.defaultValue = defaultValue;
    }

    public String path() {
        return path;
    }

    public Object defaultValue() {
        return defaultValue;
    }

    public <T> T defaultValue(Class<T> type) {
        return type.cast(defaultValue);
    }

    @Override
    public String toString() {
        return path;
    }
}

