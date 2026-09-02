package com.atm.model;

public class ATMVault {

    private int count100;
    private int count50;
    private int count20;
    private int count10;

    public ATMVault(int count100, int count50, int count20, int count10) {
        this.count100 = count100;
        this.count50 = count50;
        this.count20 = count20;
        this.count10 = count10;
    }

    public synchronized double getTotalCashAvailable() {
        return (count100 * 100.0) + (count50 * 50.0) + (count20 * 20.0) + (count10 * 10.0);
    }

    public synchronized boolean canDispense(double amount) {
        if (amount <= 0 || amount % 10 != 0) {
            return false;
        }
        return amount <= getTotalCashAvailable();
    }

    public synchronized boolean dispense(double amount) {
        if (!canDispense(amount)) {
            return false;
        }

        int remaining = (int) amount;

        int use100 = Math.min(remaining / 100, count100);
        remaining -= use100 * 100;

        int use50 = Math.min(remaining / 50, count50);
        remaining -= use50 * 50;

        int use20 = Math.min(remaining / 20, count20);
        remaining -= use20 * 20;

        int use10 = Math.min(remaining / 10, count10);
        remaining -= use10 * 10;

        if (remaining == 0) {
            count100 -= use100;
            count50 -= use50;
            count20 -= use20;
            count10 -= use10;
            return true;
        }

        return false;
    }

    public synchronized void replenish(int count100, int count50, int count20, int count10) {
        this.count100 += Math.max(0, count100);
        this.count50 += Math.max(0, count50);
        this.count20 += Math.max(0, count20);
        this.count10 += Math.max(0, count10);
    }

    public int getCount100() {
        return count100;
    }

    public int getCount50() {
        return count50;
    }

    public int getCount20() {
        return count20;
    }

    public int getCount10() {
        return count10;
    }
}
