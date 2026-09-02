# Nexus Bank - NextGen ATM Kiosk Terminal

A high-tech, production-grade **ATM Kiosk & Banking Terminal System** engineered in **Java 17**. Features a custom 3D anti-aliased metallic Swing GUI (`ATMFrame`), digital OLED display, interactive CLI mode (`ATMConsoleApp`), Indian Rupee (`₹`) multi-currency operations, SHA-256 PIN security, cash vault bill dispenser simulation, and JUnit 5 automated test suite.

![Nexus Bank NextGen ATM Kiosk Terminal UI](assets/atm_kiosk_demo.png)

---

## 🌟 Key System Features

### 1. High-Tech Metallic GUI Console (`ATMFrame`)
- **Custom 3D Vector `AtmButton` Component**: Java 2D anti-aliased gradient keycaps with inner bevel highlights, rounded borders, realistic drop shadows, and physical click press state animations.
- **Digital OLED Screen**: Dark midnight OLED screen (`#030712`) with glowing cyan (`#38bdf8`), emerald green (`#4ade80`), and gold (`#facc15`) typography.
- **Hardware Kiosk Simulation**: Tactile metallic keycaps (`1-9`, `0`, `CLEAR`, `OK`), Cash Dispenser Tray slot indicator, and Encrypted Chip/Contactless Card Reader slot.
- **Vivid Quick Cash Tiers**: Dedicated Rupee quick cash buttons (**₹500**, **₹1,000**, **₹2,000**, **₹5,000**, **₹10,000**).

### 2. Core Financial Engine & Security
- **Indian Rupee (`₹` / `INR`) Currency System**: Full support for Indian Rupee cash notes (`₹2000`, `₹500`, `₹200`, `₹100` notes with a default vault inventory of **₹5,00,000.00**).
- **Daily Withdrawal Enforcement**: Automated daily limit enforcement (Maximum **₹50,000.00** / day).
- **SHA-256 Security & Account Lockout**: SHA-256 PIN hash validation with automatic 3-attempt account lock protection against brute-force attacks.
- **P2P Fund Transfers**: Direct transfer between accounts with atomic balance updates.
- **Mini Statement & Thermal Receipts**: Last 10 transactions table with timestamps, types, amounts, and remaining balance plus pop-up printable thermal ATM receipt preview.

---

## 🔑 Pre-Seeded Demo Accounts

| Account Number | 4-Digit PIN | Account Holder | Account Type | Opening Balance (₹) |
|---|---|---|---|---|
| **1001** | `1234` | John Doe | Checking | **₹25,000.00** |
| **1002** | `5678` | Jane Smith | Savings | **₹1,50,000.00** |
| **1003** | `1111` | Alex Johnson | Checking | **₹7,500.00** |
| **9999** | `9999` | ATM Administrator | Savings | **₹5,00,000.00** |

---

## 📜 Thermal Receipt Demo Preview

When completing any withdrawal, deposit, or fund transfer operation, a printable thermal ATM receipt modal is generated:

```text
=========================================
         NEXUS GLOBAL BANK ATM           
            TRANSACTION RECEIPT          
=========================================
Terminal ID  : #ATM-8092
Account #    : 1001 (John Doe)
Date / Time  : 2026-09-02 20:36:15
-----------------------------------------
Operation    : WITHDRAWAL
Amount       : ₹2,000.00
New Balance  : ₹23,000.00
-----------------------------------------
      Thank you for banking with Nexus!   
=========================================
```

---

## 💻 CLI Terminal Mode Demo Output

```text
=================================================
     NEXUS GLOBAL BANK - ATM CONSOLE TERMINAL    
=================================================

--- ATM SYSTEM READY ---
Enter Account Number: 1001
Enter 4-Digit PIN: 1234

[SUCCESS] Welcome, John Doe! (Account #1001)

-------------------------------------------------
  ACCOUNT: #1001 (John Doe) | BAL: ₹25,000.00
-------------------------------------------------
1. Check Balance
2. Withdraw Cash
3. Deposit Cash
4. Transfer Funds
5. Print Mini Statement
6. Logout / Eject Card
Select Operation (1-6): 1

-> Current Available Balance: ₹25,000.00
```

---

## ⚡ How to Run

### 1. Launch Modern GUI Terminal
```powershell
cd f:\works\Java\atm-interface
..\ai-riddle-game\mvnw.cmd compile exec:java "-Dexec.mainClass=com.atm.ATMApplication"
```

### 2. Launch Interactive CLI Console Mode
```powershell
cd f:\works\Java\atm-interface
..\ai-riddle-game\mvnw.cmd compile exec:java "-Dexec.mainClass=com.atm.ATMApplication" "-Dexec.args=--cli"
```

### 3. Run Automated JUnit 5 Unit Tests
```powershell
cd f:\works\Java\atm-interface
..\ai-riddle-game\mvnw.cmd -f pom.xml test
```
