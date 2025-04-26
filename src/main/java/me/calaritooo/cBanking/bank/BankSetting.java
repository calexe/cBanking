package me.calaritooo.cBanking.bank;

/**
 * Enum for all configurable settings stored in each bank's settings.yml.
 * Use this with EconomyService to safely get/set values.
 */
public enum BankSetting {

    NAME("bank-name", ""),
    OWNER_UUID("owner-uuid", ""),
    OWNER_NAME("owner-name", ""),
    ASSETS("assets", 0.0),
    INTEREST_RATE("interest-rate", 0.01),
    ACCOUNT_GROWTH_RATE("account-growth-rate", 0.0),
    ACCOUNT_OPENING_FEE("account-opening-fee", 100.0),
    MAINTENANCE_FEE_RATE("maintenance-fee-rate", 0.005),
    DEPOSIT_FEE_RATE("deposit-fee-rate", 0.01),
    WITHDRAWAL_FEE_RATE("withdrawal-fee-rate", 0.01);

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

    public static BankSetting fromString(String input) {
        String normalized = input.replace("_", "").toLowerCase();

        for (BankSetting setting : values()) {
            String settingKey = setting.name().replace("_", "").toLowerCase();
            if (settingKey.equals(normalized)) {
                return setting;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public <T> T defaultValue(Class<T> type) {
        return (T) defaultValue;
    }
}

