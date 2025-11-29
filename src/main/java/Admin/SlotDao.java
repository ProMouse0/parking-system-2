
package Admin;

import java.util.List;

public interface SlotDao {
    List<Slot> findAll();
    boolean parkVehicle(String id, String vehicleNumber);     // AVAILABLE/RESERVED -> OCCUPIED
    boolean reserveSlot(String id, String reservedBy);         // AVAILABLE -> RESERVED
    boolean removeVehicle(String id);                          // OCCUPIED/RESERVED -> AVAILABLE
    Slot findById(String id);
}
