/**
 * OPDVisit — Outpatient Department Visit
 *
 * BILLING FORMULA:
 *   Total = consultationFee + doctorFee
 *
 * A patient walks in, consults a doctor, and leaves the same day.
 * No bed charge. No multi-day treatment. Just two flat fees.
 *
 * HOW THE TEMPLATE METHOD WORKS HERE:
 *   OPDVisit supplies ONLY calculateBill().
 *   Everything else (logVisit, validateAmount, printReceipt, generateReceipt)
 *   is inherited from HospitalVisit — written once, cannot be skipped.
 */
public class OPDVisit extends HospitalVisit {

    // =========================================================
    // OPD-SPECIFIC FIELDS
    // =========================================================
    private double consultationFee; // fixed charge for using the OPD facility
    private double doctorFee;       // variable charge based on the consulting doctor

    // =========================================================
    // CONSTRUCTOR
    // =========================================================
    /**
     * Creates an OPD visit record.
     * super(patientName) MUST be the first line — HospitalVisit has no
     * no-arg constructor, so Java would refuse to compile this if omitted.
     *
     * @param patientName     patient's name (validated in super constructor)
     * @param consultationFee facility charge (must be >= 0)
     * @param doctorFee       doctor's charge (must be >= 0)
     * @throws IllegalArgumentException if either fee is negative
     */
    public OPDVisit(String patientName, double consultationFee, double doctorFee) {
        super(patientName);   // Step 1: initialise shared fields (visitId, patientName)

        if (consultationFee < 0) {
            throw new IllegalArgumentException(
                "Consultation fee cannot be negative for patient: " + patientName);
        }
        if (doctorFee < 0) {
            throw new IllegalArgumentException(
                "Doctor fee cannot be negative for patient: " + patientName);
        }

        this.consultationFee = consultationFee;
        this.doctorFee       = doctorFee;
    }

    // =========================================================
    // FORCED CONTRACT — @Override required by abstract parent
    // =========================================================
    /**
     * OPD-specific billing formula: flat consultation fee + doctor fee.
     *
     * WHY @Override IS MANDATORY:
     *   calculateBill() is declared abstract in HospitalVisit.
     *   If this method were removed, the compiler would refuse to compile
     *   OPDVisit (unless OPDVisit itself were also declared abstract).
     *   This is the "compiler-enforced contract" that prevents silent billing bugs.
     *
     * @return total OPD bill (consultationFee + doctorFee)
     */
    @Override
    public double calculateBill() {
        return consultationFee + doctorFee;
    }

    // =========================================================
    // OPTIONAL GETTERS (for testing / reporting)
    // =========================================================
    public double getConsultationFee() { return consultationFee; }
    public double getDoctorFee()       { return doctorFee; }
}
