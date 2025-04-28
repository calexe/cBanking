package me.calaritooo.cBanking.util.money;

public class Money {

    private final double amount;

    private Money(double amount) {
        this.amount = trim(amount);
    }

    public static Money of(double amount) {
        return new Money(amount);
    }

    public static Money parse(String input) {
        try {
            double parsed = Double.parseDouble(input);
            if (parsed >= 0) {
                return new Money(parsed);
            } else {
                return null;
            }
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static Money zero() {
        return new Money(0.0);
    }

    private double trim(double value) {
        return Math.floor(value * 100.0) / 100.0;
    }

    public double value() {
        return amount;
    }

    public Money add(Money other) {
        return new Money(this.amount + other.amount);
    }

    public Money subtract(Money other) {
        return new Money(this.amount - other.amount);
    }

    public boolean greaterOrEqual(Money other) {
        return this.amount >= other.amount;
    }

    public boolean lessThan(Money other) {
        return this.amount < other.amount;
    }

    public String format() {
        if (amount % 1 == 0) {
            return String.format("%.0f", amount);
        } else {
            return String.format("%.2f", amount);
        }
    }

    public String formatWithCurrency(String currencySymbol) {
        return currencySymbol + format();
    }

    @Override
    public String toString() {
        return format();
    }
}
