import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

/**
 * MedCareBillingSystem — Interactive Front Desk Driver
 *
 * WHAT CHANGED FROM THE PREVIOUS VERSION:
 *   All hardcoded patient data has been removed.
 *   The program now shows a menu, reads user input via Scanner,
 *   validates every value entered, creates the correct visit object,
 *   and then calls generateReceipt() exactly as before.
 *
 * WHAT REMAINS UNCHANGED:
 *   HospitalVisit (abstract class + template method)
 *   OPDVisit, IPDVisit, EmergencyVisit (subclasses + calculateBill())
 *   The polymorphic HospitalVisit[] loop at the end of the session.
 *   All formatting helpers (header, label, printError).
 */
public class MedCareBillingSystem {

    // =========================================================
    // SCANNER — single shared instance for the whole program
    // =========================================================
    private static final Scanner sc = new Scanner(System.in);

    // =========================================================
    // FORMATTING HELPERS (unchanged from previous version)
    // =========================================================

    private static void header(String title) {
        System.out.println();
        System.out.println("  ====================================================");
        System.out.println("    " + title);
        System.out.println("  ====================================================");
        System.out.println();
    }

    private static void printError(String message) {
        System.out.println("  [ERROR] " + message);
        System.out.println();
    }

    // =========================================================
    // INPUT HELPERS — each reads and validates one value type
    // =========================================================

    /**
     * Reads a non-blank patient name from the user.
     * Keeps asking until a valid name is entered.
     */
    private static String readName(String prompt) {
        while (true) {
            System.out.print(prompt);
            String name = sc.nextLine().trim();
            if (!name.isEmpty()) {
                return name;
            }
            System.out.println("  [!] Name cannot be blank. Please try again.");
        }
    }

    /**
     * Reads a non-negative double from the user.
     * Rejects negative numbers and non-numeric input.
     * allowZero controls whether 0.0 is accepted (e.g., days=0 is valid for IPD).
     */
    private static double readDouble(String prompt, boolean allowZero) {
        while (true) {
            System.out.print(prompt);
            try {
                double value = sc.nextDouble();
                sc.nextLine();                       // consume leftover newline
                if (value < 0) {
                    System.out.println("  [!] Value cannot be negative. Try again.");
                } else if (!allowZero && value == 0) {
                    System.out.println("  [!] Value cannot be zero. Try again.");
                } else {
                    return value;
                }
            } catch (InputMismatchException e) {
                sc.nextLine();                       // discard bad input
                System.out.println("  [!] Please enter a valid number.");
            }
        }
    }

    /**
     * Reads a non-negative integer from the user.
     * Used for number of days (IPD) and number of patients.
     */
    private static int readInt(String prompt, int min) {
        while (true) {
            System.out.print(prompt);
            try {
                int value = Integer.parseInt(sc.nextLine().trim());
                if (value < min) {
                    System.out.printf(
                        "  [!] Value must be at least %d. Try again.%n", min);
                } else {
                    return value;
                }
            } catch (NumberFormatException e) {
                System.out.println("  [!] Please enter a whole number.");
            }
        }
    }

    /**
     * Reads the menu choice (1-4).
     * Rejects anything outside that range.
     */
    private static int readMenuChoice() {
        while (true) {
            System.out.print("  Enter your choice (1-4): ");
            try {
                int choice = Integer.parseInt(sc.nextLine().trim());
                if (choice >= 1 && choice <= 4) {
                    return choice;
                }
                System.out.println("  [!] Please enter 1, 2, 3, or 4.");
            } catch (NumberFormatException e) {
                System.out.println("  [!] Invalid input. Enter a number between 1 and 4.");
            }
        }
    }

    // =========================================================
    // VISIT BUILDERS — collect input and return a visit object
    // =========================================================

    /**
     * Collects OPD data from the user and returns an OPDVisit.
     * Formula: consultationFee + doctorFee
     */
    private static OPDVisit buildOPDVisit() {
        System.out.println();
        System.out.println("  -- OPD Visit Details --");
        String name            = readName  ("  Patient name         : ");
        double consultationFee = readDouble("  Consultation fee (Rs): ", false);
        double doctorFee       = readDouble("  Doctor fee       (Rs): ", false);
        return new OPDVisit(name, consultationFee, doctorFee);
    }

    /**
     * Collects IPD data from the user and returns an IPDVisit.
     * Formula: (roomChargePerDay x numberOfDays) + treatmentCharge
     * Note: days=0 is allowed (observation visit — room component = 0).
     */
    private static IPDVisit buildIPDVisit() {
        System.out.println();
        System.out.println("  -- IPD Visit Details --");
        String name            = readName  ("  Patient name              : ");
        double roomCharge      = readDouble("  Room charge per day  (Rs): ", false);
        int    days            = readInt   ("  Number of days admitted  : ", 0);
        double treatmentCharge = readDouble("  Treatment charge     (Rs): ", false);
        return new IPDVisit(name, roomCharge, days, treatmentCharge);
    }

    /**
     * Collects Emergency data from the user and returns an EmergencyVisit.
     * Formula: baseCharge + treatmentCharge + Rs.500 (mandatory surcharge, auto-added)
     */
    private static EmergencyVisit buildEmergencyVisit() {
        System.out.println();
        System.out.println("  -- Emergency Visit Details --");
        System.out.println("  (Note: Rs.500 emergency surcharge is added automatically)");
        String name            = readName  ("  Patient name          : ");
        double baseCharge      = readDouble("  Base charge      (Rs): ", false);
        double treatmentCharge = readDouble("  Treatment charge (Rs): ", false);
        return new EmergencyVisit(name, baseCharge, treatmentCharge);
    }

    // =========================================================
    // MAIN — interactive entry point
    // =========================================================
    public static void main(String[] args) {

        header("MedCare Hospital Billing System");

        // Collect all visits entered during this session into a list.
        // Using ArrayList instead of a fixed array because we don't know
        // how many visits the user will enter.
        // The list type is HospitalVisit (parent type) — same polymorphic
        // pattern as the original HospitalVisit[] array.
        List<HospitalVisit> sessionVisits = new ArrayList<>();

        // ─────────────────────────────────────────────────────────────
        // MAIN MENU LOOP
        // Runs until the user chooses option 4 (Exit).
        // ─────────────────────────────────────────────────────────────
        boolean running = true;

        while (running) {
            // Print the menu
            System.out.println("  ----------------------------------------------------");
            System.out.println("  Select Visit Type:");
            System.out.println("    1. OPD Visit        (Consultation + Doctor fee)");
            System.out.println("    2. IPD Visit        (Room/day x Days + Treatment)");
            System.out.println("    3. Emergency Visit  (Base + Treatment + Rs.500)");
            System.out.println("    4. Done — Print All Receipts and Exit");
            System.out.println("  ----------------------------------------------------");

            int choice = readMenuChoice();

            switch (choice) {
                case 1:
                    // ── OPD ──────────────────────────────────────────
                    try {
                        OPDVisit opd = buildOPDVisit();
                        sessionVisits.add(opd);
                        System.out.println();
                        System.out.println("  [OK] OPD visit registered for "
                            + opd.getPatientName() + ".");
                    } catch (IllegalArgumentException e) {
                        printError(e.getMessage());
                    }
                    break;

                case 2:
                    // ── IPD ──────────────────────────────────────────
                    try {
                        IPDVisit ipd = buildIPDVisit();
                        sessionVisits.add(ipd);
                        System.out.println();
                        System.out.println("  [OK] IPD visit registered for "
                            + ipd.getPatientName() + ".");
                    } catch (IllegalArgumentException e) {
                        printError(e.getMessage());
                    }
                    break;

                case 3:
                    // ── Emergency ────────────────────────────────────
                    try {
                        EmergencyVisit ev = buildEmergencyVisit();
                        sessionVisits.add(ev);
                        System.out.println();
                        System.out.println("  [OK] Emergency visit registered for "
                            + ev.getPatientName() + ".");
                    } catch (IllegalArgumentException e) {
                        printError(e.getMessage());
                    }
                    break;

                case 4:
                    // ── Exit ─────────────────────────────────────────
                    running = false;
                    break;
            }

            System.out.println();
        }

        // ─────────────────────────────────────────────────────────────
        // SESSION SUMMARY — generate receipts for every visit entered
        //
        // This is the SAME polymorphic loop from the original project:
        //   - Iterates over HospitalVisit (parent type)
        //   - Calls generateReceipt() on each
        //   - JVM routes calculateBill() to the correct subclass
        //   - Zero instanceof checks — works for any current/future type
        // ─────────────────────────────────────────────────────────────
        if (sessionVisits.isEmpty()) {
            header("No visits were registered this session.");
            sc.close();
            return;
        }

        header("Session Receipts  |  " + sessionVisits.size() + " Visit(s)");

        double sessionTotal = 0.0;

        for (HospitalVisit visit : sessionVisits) {
            try {
                visit.generateReceipt();     // template method: calc → validate → log → print
                sessionTotal += visit.getAmount();
            } catch (IllegalArgumentException e) {
                // Should not reach here (validated at entry), but guarded defensively
                printError("Skipped " + visit.getVisitId() + " — " + e.getMessage());
            }
        }

        // Print session-level summary
        System.out.println("  ====================================================");
        System.out.printf( "    Total visits this session : %d%n",
                           sessionVisits.size());
        System.out.printf( "    Session total amount       : Rs. %,.2f%n",
                           sessionTotal);
        System.out.println("  ====================================================");
        System.out.println();

        sc.close();
    }
}
