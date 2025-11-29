
package Admin;


public class Slot {
    public String id;              // e.g., "A01"
    public String name;            // same as id
    public SlotStatus status;      // AVAILABLE / RESERVED / OCCUPIED
    public String vehicleNumber;   // set when OCCUPIED
    public String reservedBy;      // optional for RESERVED

    public Slot() {}


    public Slot(String id, SlotStatus status, String vehicleNumber, String reservedBy) {
        this.id = id;
        this.name = id;
        this.status = status;
        this.vehicleNumber = vehicleNumber;
        this.reservedBy = reservedBy;
    }
}
