package me.calaritooo.cBanking.bank;

/**
 * Enum for all configurable settings stored in each bank's settings.yml.
 * Use this with EconomyService to safely get/set values.
 */
public enum BankSetting {

    NAME("bankName", ""),
    OWNER_UUID("ownerUUID", ""),
    OWNER_NAME("ownerName", ""),
    ASSETS("assets", 0.0),
    INTEREST_RATE("interestRate", 0.01),
    ACCOUNT_GROWTH_RATE("accountGrowthRate", 0.0),
    ACCOUNT_OPENING_FEE("accountOpeningFee", 100.0),
    MAINTENANCE_FEE_RATE("maintenanceFeeRate", 0.005),
    DEPOSIT_FEE_RATE("depositFeeRate", 0.01),
    WITHDRAWAL_FEE_RATE("withdrawalFeeRate", 0.01);

    private final String path;
    private final Object defaultValue;

    BankSetting(String path, Object defaultValue) {
        this.path = path;
        this.defaultValue = defaultValue;
    }

    public String path() {
        return path;
    }

    public Object defaultValue() {
        return defaultValue;
    }

    @SuppressWarnings("unchecked")
    public <T> T defaultValue(Class<T> type) {
        return (T) defaultValue;
    }
}

