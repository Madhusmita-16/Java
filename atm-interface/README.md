# Nexus ATM Interface — Java Project 🏦🏧

A complete, production-grade **ATM Interface Application** built in **Java 17** featuring a modern **Dark Glassmorphic Swing GUI**, an interactive **CLI Mode**, SHA-256 PIN security with 3-attempt account lockout, cash vault dispenser inventory tracking, and full transaction history logging.

---

## 🌟 Key Features

1. **Dual Execution Modes**:
   - **Modern Swing GUI (`ATMFrame`)**: Interactive LCD screen display, tactile PIN pad, quick cash buttons ($20, $50, $100, $200, $500), custom deposit slot simulation, mini statement table, and thermal receipt modal.
   - **CLI Console App (`ATMConsoleApp`)**: Command-line interface for terminal environments.

2. **Security & Account Protection**:
   - SHA-256 hashed PIN authentication.
   - Automatic 3-attempt PIN lockout protection to prevent brute-force intrusion.

3. **Core Financial Operations**:
   - **Check Balance**: Instant inquiry of available account balance.
   - **Cash Withdrawal**: Quick cash & custom amounts with $1,000/day withdrawal limit check & cash vault bill inventory validation.
   - **Cash Envelope Deposit**: Envelope cash deposit with instant account crediting and vault replenishment.
   - **Fund Transfer**: Instant P2P transfer between registered bank accounts.
   - **Mini Statement**: Recent 10 transactions table with timestamps, types, amounts, and remaining balances.
   - **Receipt Generator**: Thermal ATM paper receipt dialog preview.

---

## 🔑 Pre-Seeded Demo Accounts

| Account Number | 4-Digit PIN | Account Holder | Account Type | Opening Balance |
|---|---|---|---|---|
| **1001** | `1234` | John Doe | Checking | $2,500.00 |
| **1002** | `5678` | Jane Smith | Savings | $10,500.50 |
| **1003** | `1111` | Alex Johnson | Checking | $750.25 |
| **9999** | `9999` | ATM Administrator | Savings | $50,000.00 |

---

## ⚡ Run Instructions

### 1. Run GUI Mode (Default)
```bash
cd f:\works\Java\atm-interface
..\ai-riddle-game\mvnw.cmd compile exec:java -Dexec.mainClass="com.atm.ATMApplication"
```
*Or run directly in your IDE by executing `com.atm.ATMApplication.main()`.*

### 2. Run CLI Terminal Mode
```bash
..\ai-riddle-game\mvnw.cmd compile exec:java -Dexec.mainClass="com.atm.ATMApplication" -Dexec.args="--cli"
```

### 3. Run Automated Unit Tests
```bash
..\ai-riddle-game\mvnw.cmd test
```
