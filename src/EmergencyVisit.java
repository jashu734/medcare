/**
 * EmergencyVisit — Emergency Ward Visit
 *
 * BILLING FORMULA:
 *   Total = baseCharge + treatmentCharge + EMERGENCY_SURCHARGE (Rs.500 mandatory)
 *
 * Emergency patients arrive through the emergency ward. Treatment is
 * time-critical. A mandatory Rs.500 emergency-handling surcharge is always
 * added — it is a private static final constant so it can NEVER be changed
 * or forgotten in the calculation.
 *
 * WHY static final:
 *   - static  → one copy shared across all EmergencyVisit objects (saves memory,
 *               semantically correct — it's a ward policy, not per-patient data)
 *   - final   → cannot be reassigned anywhere in code; the 500 is truly fixed
 */
public class EmergencyVisit extends HospitalVisit {

    // =========================================================
    // MANDATORY SURCHARGE — hospital policy, fixed, non-negotiable
    // =========================================================
    private static final double EMERGENCY_SURCHARGE = 500.0;

    // =========================================================
    // EMERGENCY-SPECIFIC FIELDS
    // =========================================================
    private double baseCharge;      // base emergency ward charge
    private double treatmentCharge; // cost of emergency treatment / procedures

    // =========================================================
    // CONSTRUCTOR
    // =========================================================
    /**
     * Creates an Emergency visit record.
     * The Rs.500 surcharge is automatically included in calculateBill()
     * — the front-desk clerk cannot forget it, and cannot remove it.
     *
     * @param patientName     patient's name
     * @param baseCharge      base emergency charge (must be >= 0)
     * @param treatmentCharge emergency treatment cost (must be >= 0)
     * @throws IllegalArgumentException if any charge is negative
     */
    public EmergencyVisit(String patientName,
                          double baseCharge,
                          double treatmentCharge) {
        super(patientName);

        if (baseCharge < 0) {
            throw new IllegalArgumentException(
                "Base charge cannot be negative for patient: " + patientName);
        }
        if (treatmentCharge < 0) {
            throw new IllegalArgumentException(
                "Treatment charge cannot be negative for patient: " + patientName);
        }

        this.baseCharge      = baseCharge;
        this.treatmentCharge = treatmentCharge;
    }

    // =========================================================
    // FORCED CONTRACT — @Override required by abstract parent
    // =========================================================
    /**
     * Emergency-specific billing formula:
     *   base charge + treatment charge + mandatory Rs.500 surcharge.
     *
     * EMERGENCY_SURCHARGE is added here — no clerk or developer can
     * produce an EmergencyVisit bill without it being included.
     *
     * @return total Emergency bill (always includes Rs.500 surcharge)
     */
    @Override
    public double calculateBill() {
        return baseCharge + treatmentCharge + EMERGENCY_SURCHARGE;
    }

    // =========================================================
    // OPTIONAL GETTERS
    // =========================================================
    public double getBaseCharge()           { return baseCharge; }
    public double getTreatmentCharge()      { return treatmentCharge; }
    public static double getSurcharge()     { return EMERGENCY_SURCHARGE; }
}
