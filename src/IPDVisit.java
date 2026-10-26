/**
 * IPDVisit — Inpatient Department Visit
 *
 * BILLING FORMULA:
 *   Total = (roomChargePerDay × numberOfDays) + treatmentCharge
 *
 * A patient is admitted, occupies a bed for one or more days,
 * and receives a treatment that carries its own cost.
 *
 * EDGE CASE HANDLED:
 *   days = 0 is allowed (patient observed but not admitted overnight).
 *   The room component contributes Rs.0, but treatmentCharge can still make
 *   the total positive and valid. validateAmount() (inherited) catches it
 *   if the final total is still <= 0.
 */
public class IPDVisit extends HospitalVisit {

    // =========================================================
    // IPD-SPECIFIC FIELDS
    // =========================================================
    private double roomChargePerDay; // charge for a single day's bed/room
    private int    numberOfDays;     // number of days admitted (0 = observation only)
    private double treatmentCharge;  // cost of treatment / procedures

    // =========================================================
    // CONSTRUCTOR
    // =========================================================
    /**
     * Creates an IPD visit record.
     *
     * @param patientName      patient's name
     * @param roomChargePerDay daily room/bed charge (must be >= 0)
     * @param numberOfDays     number of days admitted (must be >= 0)
     * @param treatmentCharge  treatment and procedure cost (must be >= 0)
     * @throws IllegalArgumentException if any parameter is negative
     */
    public IPDVisit(String patientName,
                    double roomChargePerDay,
                    int    numberOfDays,
                    double treatmentCharge) {
        super(patientName);

        if (roomChargePerDay < 0) {
            throw new IllegalArgumentException(
                "Room charge per day cannot be negative for patient: " + patientName);
        }
        if (numberOfDays < 0) {
            throw new IllegalArgumentException(
                "Number of days cannot be negative for patient: " + patientName);
        }
        if (treatmentCharge < 0) {
            throw new IllegalArgumentException(
                "Treatment charge cannot be negative for patient: " + patientName);
        }

        this.roomChargePerDay = roomChargePerDay;
        this.numberOfDays     = numberOfDays;
        this.treatmentCharge  = treatmentCharge;
    }

    // =========================================================
    // FORCED CONTRACT — @Override required by abstract parent
    // =========================================================
    /**
     * IPD-specific billing formula:
     *   (room charge per day × number of days) + treatment charge.
     *
     * @return total IPD bill
     */
    @Override
    public double calculateBill() {
        return (roomChargePerDay * numberOfDays) + treatmentCharge;
    }

    // =========================================================
    // OPTIONAL GETTERS
    // =========================================================
    public double getRoomChargePerDay() { return roomChargePerDay; }
    public int    getNumberOfDays()     { return numberOfDays; }
    public double getTreatmentCharge()  { return treatmentCharge; }
}
