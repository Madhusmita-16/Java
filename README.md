# Java Projects & Applications

A repository of Java projects — showcasing object-oriented software architecture, modern Swing GUI design, Spring Boot REST services, JWT security, unit testing, and financial banking simulations.

---

## 🚀 Projects Showcase

| Project | Description | Main Tech Stack | Execution Mode |
|---|---|---|---|
| **[women-safety-app/](file:///f:/works/Java/women-safety-app)** | Aegis Guard Women Safety Platform with 3D SOS panic button, fake call escape generator, live GPS geofencing, siren alarm, and trusted contacts vault. | Java 17, Swing 2D Graphics, Java Sound API, JUnit 5 | **Desktop App** |
| **[atm-interface/](file:///f:/works/Java/atm-interface)** | NextGen ATM Terminal with 3D gradient vector keycaps, digital OLED display, Indian Rupee (`₹`) operations, SHA-256 PIN security, cash vault dispenser, and thermal receipts. | Java 17, Swing 2D Graphics, JUnit 5, Maven | **GUI Terminal** & **CLI Console** |
| **[ai-riddle-game/](file:///f:/works/Java/ai-riddle-game)** | AI Riddle Arena with adaptive difficulty scaling, Spring Boot 3, Spring Security JWT, JPA/H2 database, fuzzy text matching engine, and responsive gaming UI. | Java 17, Spring Boot 3, Spring Security, JPA, JUnit 5 | **Spring Web App** (`localhost:8080`) |
| **[calculator/](file:///f:/works/Java/calculator)** | Smart calculator application supporting standard arithmetic operations and keyboard listeners. | Java 17, Swing | **Desktop App** |
| **[tictactoe/](file:///f:/works/Java/tictactoe)** | Two-player Tic-Tac-Toe game built with interactive Swing component grid. | Java 17, Swing | **Desktop App** |

---

## 🏦 ATM Interface Demo & Quick Start

![Nexus Bank NextGen ATM Kiosk Terminal UI](assets/atm_kiosk_demo.png)

### Key Features
- **Indian Rupee (`₹`) Multi-Currency**: Vault inventory loaded with **₹5,00,000.00** in `₹2000`, `₹500`, `₹200`, and `₹100` bill notes.
- **Security**: SHA-256 hashed PINs with automatic 3-attempt lock protection.
- **Visual Kiosk**: Custom 3D anti-aliased `AtmButton` keycaps with mouse hover animations, OLED midnight display, and printable thermal receipt popup.

### Pre-Seeded Accounts
- `1001` (PIN: `1234`) — John Doe (**₹25,000.00**)
- `1002` (PIN: `5678`) — Jane Smith (**₹1,50,000.00**)
- `1003` (PIN: `1111`) — Alex Johnson (**₹7,500.00**)

### Quick Commands

```powershell
# Run ATM GUI Terminal
cd f:\works\Java\atm-interface
..\ai-riddle-game\mvnw.cmd compile exec:java "-Dexec.mainClass=com.atm.ATMApplication"

# Run ATM Terminal CLI
cd f:\works\Java\atm-interface
..\ai-riddle-game\mvnw.cmd compile exec:java "-Dexec.mainClass=com.atm.ATMApplication" "-Dexec.args=--cli"

# Run AI Riddle Game Server
cd f:\works\Java\ai-riddle-game
.\mvnw.cmd spring-boot:run
```

---

## 🧪 Test Suite Execution

All projects include 100% passing automated test suites (22 / 22 tests passing):

```powershell
# Test ATM Interface (7 tests)
cd f:\works\Java\atm-interface
..\ai-riddle-game\mvnw.cmd -f pom.xml test

# Test AI Riddle Arena (15 tests)
cd f:\works\Java\ai-riddle-game
.\mvnw.cmd test
```
