package com.atm.service;

import com.atm.model.Account;
import com.atm.model.ATMVault;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ATMServiceTest {

    private BankService bankService;
    private ATMVault atmVault;
    private ATMService atmService;

    @BeforeEach
    void setUp() {
        bankService = new BankService();
        atmVault = new ATMVault(100, 200, 300, 500); // Vault with ₹4,10,000 cash
        atmService = new ATMService(bankService, atmVault);
    }

    @Test
    @DisplayName("Login - Success with valid account and PIN")
    void testLogin_Success() {
        boolean result = atmService.login("1001", "1234");
        assertTrue(result);
        assertTrue(atmService.isLoggedIn());
        assertEquals("John Doe", atmService.getActiveAccount().getHolderName());
    }

    @Test
    @DisplayName("Login - Failed attempts lock account after 3 tries")
    void testLogin_AccountLockingAfterFailedAttempts() {
        // Attempt 1
        assertThrows(IllegalArgumentException.class, () -> atmService.login("1001", "0000"));
        // Attempt 2
        assertThrows(IllegalArgumentException.class, () -> atmService.login("1001", "0000"));
        // Attempt 3 -> Account gets locked!
        assertThrows(IllegalStateException.class, () -> atmService.login("1001", "0000"));

        // Attempt 4 with correct PIN should fail because account is LOCKED
        assertThrows(IllegalStateException.class, () -> atmService.login("1001", "1234"));
    }

    @Test
    @DisplayName("Withdrawal - Success reduces balance and vault cash")
    void testWithdrawCash_Success() {
        atmService.login("1001", "1234");
        double initialBal = atmService.getActiveAccount().getBalance(); // ₹25,000

        atmService.withdrawCash(2000);

        assertEquals(initialBal - 2000, atmService.getActiveAccount().getBalance());
    }

    @Test
    @DisplayName("Withdrawal - Fails if amount exceeds account balance")
    void testWithdrawCash_InsufficientBalance() {
        atmService.login("1003", "1111"); // Alex Johnson has ₹7,500 balance

        assertThrows(IllegalStateException.class, () -> atmService.withdrawCash(10000));
    }

    @Test
    @DisplayName("Withdrawal - Fails if amount exceeds daily limit of ₹50,000")
    void testWithdrawCash_ExceedsDailyLimit() {
        atmService.login("1002", "5678"); // Jane Smith has ₹1,50,000 balance

        assertThrows(IllegalArgumentException.class, () -> atmService.withdrawCash(60000));
    }

    @Test
    @DisplayName("Deposit - Increases account balance")
    void testDepositCash() {
        atmService.login("1001", "1234");
        double initialBal = atmService.getActiveAccount().getBalance();

        atmService.depositCash(5000);

        assertEquals(initialBal + 5000, atmService.getActiveAccount().getBalance());
    }

    @Test
    @DisplayName("Transfer - Success transfers funds to recipient account")
    void testTransferFunds_Success() {
        atmService.login("1001", "1234");
        Account sender = atmService.getActiveAccount();
        Account recipient = bankService.findAccount("1002").orElseThrow();

        double senderInit = sender.getBalance();
        double recipientInit = recipient.getBalance();

        atmService.transferFunds("1002", 3000);

        assertEquals(senderInit - 3000, sender.getBalance());
        assertEquals(recipientInit + 3000, recipient.getBalance());
    }
}
