# MedCare Hospital Billing System

**GitHub repository:** https://github.com/jashu734/medcare

**Topic:** Abstract Classes — Template Methods & Forced Contracts
**Language:** Java (beginner-friendly, single-package)
**Pattern:** Template Method Pattern

---

## Project Structure

```text
MedCare/
├── src/
│   ├── HospitalVisit.java         <- abstract base class (template)
│   ├── OPDVisit.java              <- concrete subclass: Outpatient
│   ├── IPDVisit.java              <- concrete subclass: Inpatient
│   ├── EmergencyVisit.java        <- concrete subclass: Emergency
│   └── MedCareBillingSystem.java  <- main driver (front desk)
├── docs/
│   └── project_report.md
└── README.md
```

---

## How to Compile and Run

```bash
# From the MedCare/ folder:

# 1. Compile all source files (use forward slashes — works on both CMD and PowerShell)
javac -d . src/HospitalVisit.java src/OPDVisit.java src/IPDVisit.java src/EmergencyVisit.java src/MedCareBillingSystem.java

# 2. Run the program
java MedCareBillingSystem
```

> **Note for Windows PowerShell:** Forward slashes (`/`) work correctly.
> If you use backslashes (`\`), wrap the whole path list in quotes or compile one file at a time.

---

## Billing Formulas

| Visit Type | Formula |
| --- | --- |
| OPD | consultationFee + doctorFee |
| IPD | (roomChargePerDay x numberOfDays) + treatmentCharge |
| Emergency | baseCharge + treatmentCharge + **Rs.500 mandatory surcharge** |

---

## Sample Output (Section 1 — Daily Billing)

```text
===================================================================
  MedCare Hospital - Daily Billing Run
===================================================================

[LOG] Visit MC0001 for Riya Sharma recorded at 23-Sep-2026 10:00:00
Receipt  [MC0001]  Riya Sharma           |  Rs. 800.00

[LOG] Visit MC0002 for Arjun Mehta recorded at 23-Sep-2026 10:00:00
Receipt  [MC0002]  Arjun Mehta           |  Rs. 7,600.00

[LOG] Visit MC0003 for Kabir Singh recorded at 23-Sep-2026 10:00:00
Receipt  [MC0003]  Kabir Singh           |  Rs. 2,800.00

[LOG] Visit MC0004 for Sneha Rao recorded at 23-Sep-2026 10:00:00
Receipt  [MC0004]  Sneha Rao             |  Rs. 700.00

  Total visits processed today: 4
```

---

## Core Concept Summary

```text
Problem: Duplicated logging/validation logic, missed updates, unchecked billing formulas
        |
Concept: Abstract Class + Template Method Pattern
        |
Implementation:
  - HospitalVisit (abstract) -> shared fields, logVisit(), validateAmount(), generateReceipt()
  - calculateBill() abstract -> compiler-enforced per-subclass formula
  - OPDVisit / IPDVisit / EmergencyVisit -> each @Override calculateBill()
  - HospitalVisit[] loop -> zero instanceof checks, runtime polymorphism
        |
Result: One place to update logging/validation, impossible to skip billing formula,
        new visit types require only one @Override method
```
