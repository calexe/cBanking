package me.calaritooo.cBanking.util.configuration;

public enum ConfigurationOption {

    // ───── General Plugin Settings ─────
    AUTOSAVE_INTERVAL("plugin-settings.autosave-interval", 10, Integer.class),
    LOG_TRANSACTIONS("plugin-settings.logging.enable-log-transactions", false, Boolean.class),
    LOG_LOAN_REQUESTS("plugin-settings.logging.enable-log-loan-requests", false, Boolean.class),
    ENABLE_ADMIN_MESSAGES("plugin-settings.notifications.enable-admin-messages", true, Boolean.class),

    // ───── Modules ─────
    MODULE_BANKS("modules.enable-banks", true, Boolean.class),
    MODULE_LOANS("modules.enable-loans", true, Boolean.class),
    MODULE_DEATH_LOSS("modules.enable-death-loss", false, Boolean.class),

    // ───── Economy Settings ─────
    ECONOMY_STARTING_BALANCE("economy-settings.starting-bal", 0.00, Double.class),
    ECONOMY_CURRENCY_SYMBOL("economy-settings.currency-symbol", "ȼ", String.class),
    ECONOMY_CURRENCY_NAME("economy-settings.currency-name", "Coin", String.class),
    ECONOMY_CURRENCY_NAME_PLURAL("economy-settings.currency-name-plural", "Coins", String.class),
    ECONOMY_RATE_PERIOD("economy-settings.rate-period", 48, Integer.class),
    ECONOMY_DEATH_LOSS_TYPE("economy-settings.death-loss-type", "percentage", String.class),
    ECONOMY_DEATH_LOSS_VALUE("economy-settings.death-loss-value", 1, Double.class),

    // ───── Bank Settings ─────
    BANK_CREATION_FEE("bank-settings.new-bank-fee", 250.00, Double.class),
    BANK_CREATION_ASSETS("bank-settings.new-bank-assets", 500.00, Double.class),
    BANK_MIN_ASSETS("bank-settings.min-bank-assets", 100.00, Double.class),
    BANK_BANKRUPTCY("bank-settings.bankruptcy", false, Boolean.class),
    BANK_CLOSURE_FEE_TYPE("bank-settings.bank-closure-fee-type", "player", String.class),
    BANK_CLOSURE_FEE("bank-settings.bank-closure-fee", 20.00, Double.class),
    BANK_DEFAULT_ACCOUNT_OPENING_FEE("bank-settings.default-acct-opening-fee", 10.00, Double.class),
    BANK_MIN_ACCOUNT_OPENING_FEE("bank-settings.min-acct-opening-fee", 0.00, Double.class),
    BANK_MAX_ACCOUNT_OPENING_FEE("bank-settings.max-acct-opening-fee", 50.00, Double.class),
    BANK_MAINTENANCE_FEE_TYPE("bank-settings.maintenance-fee-type", "flat", String.class),
    BANK_DEFAULT_MAINTENANCE_FEE("bank-settings.default-maintenance-fee-rate", 2.00, Double.class),
    BANK_DEFAULT_ACCOUNT_GROWTH("bank-settings.default-acct-growth-rate", 2.00, Double.class),
    BANK_MIN_BAL_FOR_GROWTH("bank-settings.min-acct-bal-for-growth", 50.00, Double.class),
    BANK_MIN_ACCOUNT_GROWTH("bank-settings.min-acct-growth-rate", 0.00, Double.class),
    BANK_MAX_ACCOUNT_GROWTH("bank-settings.max-acct-growth-rate", 15.00, Double.class),
    BANK_TRANSACTION_FEE_TYPE("bank-settings.transaction-fee-type", "percentage", String.class),
    BANK_DEFAULT_DEPOSIT_FEE("bank-settings.default-deposit-fee-rate", 0.00, Double.class),
    BANK_MIN_DEPOSIT_FEE("bank-settings.min-deposit-rate", 0.00, Double.class),
    BANK_MAX_DEPOSIT_FEE("bank-settings.max-deposit-rate", 15.00, Double.class),
    BANK_DEFAULT_WITHDRAWAL_FEE("bank-settings.default-withdrawal-fee-rate", 2.00, Double.class),
    BANK_MIN_WITHDRAWAL_FEE("bank-settings.min-withdrawal-rate", 0.00, Double.class),
    BANK_MAX_WITHDRAWAL_FEE("bank-settings.max-withdrawal-rate", 15.00, Double.class),

    // ───── Loan Settings ─────
    LOAN_DEFAULT_INTEREST_RATE("loan-settings.default-interest-rate", 2.00, Double.class),
    LOAN_MIN_BAL_FOR_LOAN("loan-settings.min-acct-bal-for-loan", 50.00, Double.class),
    LOAN_MIN_INTEREST("loan-settings.min-loan-interest-rate", 0.00, Double.class),
    LOAN_MAX_INTEREST("loan-settings.max-loan-interest-rate", 15.00, Double.class),
    LOAN_MIN_AMOUNT("loan-settings.min-loan-amt", 250.00, Double.class),
    LOAN_MAX_AMOUNT("loan-settings.max-loan-amt", 500.00, Double.class),
    LOAN_MIN_PERIOD("loan-settings.min-loan-period", 1, Integer.class),
    LOAN_MAX_PERIOD("loan-settings.max-loan-period", 7, Integer.class),;

    private final String path;
    private final Object defaultValue;
    private final Class<?> type;

    ConfigurationOption(String path, Object defaultValue, Class<?> type) {
        this.path = path;
        this.defaultValue = defaultValue;
        this.type = type;
    }

    public String path() {
        return path;
    }

    public Object defaultValue() {
        return defaultValue;
    }

    public Class<?> type() {
        return type;
    }

    @Override
    public String toString() {
        return path;
    }
}

