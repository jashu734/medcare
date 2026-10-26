/**
 * HospitalVisit — Abstract Base Class (Template Method Pattern)
 *
 * PURPOSE:
 *   Defines the shared structure and guaranteed behavior for every type of
 *   hospital visit (OPD, IPD, Emergency, and any future types).
 *
 * TEMPLATE METHOD PATTERN:
 *   generateReceipt() is the template method. It defines the FIXED skeleton:
 *       1. calculateBill()  ← varies per subclass  (abstract → forced contract)
 *       2. validateAmount() ← same for everyone     (concrete → inherited once)
 *       3. logVisit()       ← same for everyone     (concrete → inherited once)
 *       4. printReceipt()   ← same for everyone     (concrete → inherited once)
 *
 * WHY ABSTRACT:
 *   - Cannot be instantiated directly → enforces that only real visit types exist.
 *   - calculateBill() has no body here → every subclass MUST supply its own
 *     billing formula or the compiler refuses to compile that subclass.
 */
public abstract class HospitalVisit {

    // =========================================================
    // SHARED FIELDS — available to every subclass via 'protected'
    // =========================================================
    protected String visitId;       // globally unique, auto-generated (e.g. MC0001)
    protected String patientName;   // name entered at front desk
    protected double amount;        // calculated total bill (set inside generateReceipt)

    // Static counter lives in the METHOD AREA — shared across ALL visit objects,
    // regardless of type. This guarantees globally unique visit IDs.
    private static int visitCounter = 0;

    // =========================================================
    // CONSTRUCTOR — real, callable via super() from subclasses
    // =========================================================
    /**
     * Initialises shared fields for every visit type.
     * Subclasses MUST call super(patientName) as their first line because
     * HospitalVisit deliberately has no no-argument constructor.
     *
     * @param patientName name of the patient (must not be null or blank)
     * @throws IllegalArgumentException if patientName is null or blank
     */
    public HospitalVisit(String patientName) {
        if (patientName == null || patientName.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Patient name cannot be null or blank.");
        }
        visitCounter++;
        // Zero-pads the counter to 4 digits → MC0001, MC0002, …, MC9999
        this.visitId      = "MC" + String.format("%04d", visitCounter);
        this.patientName  = patientName.trim();
    }

    // Shared separator line used by printReceipt()
    private static final String CARD_LINE =
        "  ....................................................";

    // =========================================================
    // CONCRETE (SHARED) METHODS — written ONCE, inherited by ALL
    // =========================================================

    /**
     * Logs this visit as a compact audit entry.
     * Identical for every visit type — lives here once, never duplicated.
     */
    public void logVisit() {
        System.out.printf("  [LOG] %s  |  %-22s  |  visit recorded.%n",
            visitId, patientName);
    }

    /**
     * Validates that the computed bill is a positive, non-zero value.
     * Identical for every visit type — lives here once, never duplicated.
     *
     * @throws IllegalArgumentException if amount is zero or negative
     */
    public void validateAmount() {
        if (amount <= 0) {
            throw new IllegalArgumentException(
                "Invalid amount for " + visitId
                + " [" + patientName + "]: Rs." + amount
                + " (must be > 0)");
        }
    }

    /**
     * Prints a formatted receipt card to the console.
     * Layout is identical for every visit type.
     * Visit type is derived from the subclass name automatically.
     */
    public void printReceipt() {
        // Derive visit type label from subclass name automatically
        // OPDVisit -> OPD  |  IPDVisit -> IPD  |  EmergencyVisit -> EMERGENCY
        String visitType = getClass().getSimpleName()
            .replace("Visit", "").toUpperCase();

        // Format date and time as separate strings (avoids substring index bugs)
        String date = java.time.LocalDate.now()
            .format(java.time.format.DateTimeFormatter.ofPattern("dd-MMM-yyyy"));
        String time = java.time.LocalTime.now()
            .format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));

        System.out.println(CARD_LINE);
        System.out.printf("  %-6s  %-13s  %s%n",
            visitId, "[" + visitType + "]", patientName);
        System.out.printf("  Amount  : Rs. %s%n",
            String.format("%,.2f", amount));
        System.out.printf("  Date    : %s    Time : %s%n", date, time);
        System.out.println(CARD_LINE);
    }

    // =========================================================
    // ABSTRACT METHOD — the FORCED, COMPILER-CHECKED CONTRACT
    // =========================================================

    /**
     * Calculates and returns the total bill for this visit.
     *
     * WHY ABSTRACT:
     *   OPD, IPD, and Emergency visits each have a completely different
     *   billing formula. Making this method abstract means:
     *     - Every subclass MUST provide its own @Override.
     *     - Forgetting to implement it → compile-time error (not a silent runtime bug).
     *     - No wrong "default" amount can be inherited accidentally.
     *
     * @return total bill amount in rupees (must be > 0)
     */
    public abstract double calculateBill();

    // =========================================================
    // TEMPLATE METHOD — the fixed skeleton, called from Main
    // =========================================================

    /**
     * Processes a complete visit receipt in four guaranteed steps:
     *   Step 1 — calculateBill()  : subclass-specific billing formula
     *   Step 2 — validateAmount() : shared validation (amount > 0)
     *   Step 3 — logVisit()       : shared audit log entry
     *   Step 4 — printReceipt()   : shared formatted output
     *
     * This is the TEMPLATE METHOD. The overall sequence is fixed here once.
     * Only Step 1 varies — and even that variation is controlled because
     * calculateBill() is abstract.
     *
     * The front desk calls this one method for every visit type —
     * no instanceof, no if/else — and the JVM automatically routes
     * calculateBill() to the correct subclass at runtime.
     */
    public void generateReceipt() {
        this.amount = calculateBill();   // Step 1 — subclass-specific (polymorphic)
        validateAmount();                // Step 2 — shared, cannot be skipped
        logVisit();                      // Step 3 — shared, cannot be skipped
        printReceipt();                  // Step 4 — shared, cannot be skipped
        System.out.println();            // blank line after each card for readability
    }

    // =========================================================
    // GETTERS — read-only access from outside the hierarchy
    // =========================================================

    /** @return auto-generated visit ID (e.g. MC0001) */
    public String getVisitId()     { return visitId; }

    /** @return patient's name as entered at front desk */
    public String getPatientName() { return patientName; }

    /** @return final calculated bill amount (0.0 until generateReceipt() runs) */
    public double getAmount()      { return amount; }

    /** @return total visits created so far across all types (for reporting) */
    public static int getTotalVisits() { return visitCounter; }
}
