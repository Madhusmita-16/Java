package com.atm.model;

public class ATMVault {

    private int count2000;
    private int count500;
    private int count200;
    private int count100;

    public ATMVault(int count2000, int count500, int count200, int count100) {
        this.count2000 = count2000;
        this.count500 = count500;
        this.count200 = count200;
        this.count100 = count100;
    }

    public synchronized double getTotalCashAvailable() {
        return (count2000 * 2000.0) + (count500 * 500.0) + (count200 * 200.0) + (count100 * 100.0);
    }

    public synchronized boolean canDispense(double amount) {
        if (amount <= 0 || amount % 100 != 0) {
            return false;
        }
        return amount <= getTotalCashAvailable();
    }

    public synchronized boolean dispense(double amount) {
        if (!canDispense(amount)) {
            return false;
        }

        int remaining = (int) amount;

        int use2000 = Math.min(remaining / 2000, count2000);
        remaining -= use2000 * 2000;

        int use500 = Math.min(remaining / 500, count500);
        remaining -= use500 * 500;

        int use200 = Math.min(remaining / 200, count200);
        remaining -= use200 * 200;

        int use100 = Math.min(remaining / 100, count100);
        remaining -= use100 * 100;

        if (remaining == 0) {
            count2000 -= use2000;
            count500 -= use500;
            count200 -= use200;
            count100 -= use100;
            return true;
        }

        return false;
    }

    public synchronized void replenish(int count2000, int count500, int count200, int count100) {
        this.count2000 += Math.max(0, count2000);
        this.count500 += Math.max(0, count500);
        this.count200 += Math.max(0, count200);
        this.count100 += Math.max(0, count100);
    }

    public int getCount2000() {
        return count2000;
    }

    public int getCount500() {
        return count500;
    }

    public int getCount200() {
        return count200;
    }

    public int getCount100() {
        return count100;
    }
}
