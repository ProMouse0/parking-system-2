
package Admin;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SlotStore implements SlotDao {

    @Override
    public List<Slot> findAll() {
        List<Slot> slots = new ArrayList<>();
        String sql = "SELECT id, status, vehicle_number, reserved_by FROM slots ORDER BY id";

        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                slots.add(new Slot(
                        rs.getString("id"),
                        SlotStatus.fromDb(rs.getString("status")),
                        rs.getString("vehicle_number"),
                        rs.getString("reserved_by")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return slots;
    }

    @Override
    public Slot findById(String id) {
        String sql = "SELECT id, status, vehicle_number, reserved_by FROM slots WHERE id = ?";
        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Slot(
                        rs.getString("id"),
                        SlotStatus.fromDb(rs.getString("status")),
                        rs.getString("vehicle_number"),
                        rs.getString("reserved_by")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean parkVehicle(String id, String vehicleNumber) {
        String sql = """
            UPDATE slots 
            SET status='OCCUPIED', vehicle_number=? 
            WHERE id=? AND (status='AVAILABLE' OR status='RESERVED')
        """;

        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, vehicleNumber);
            ps.setString(2, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean reserveSlot(String id, String reservedBy) {
        String sql = """
            UPDATE slots 
            SET status='RESERVED', reserved_by=? 
            WHERE id=? AND status='AVAILABLE'
        """;

        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, reservedBy);
            ps.setString(2, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean removeVehicle(String id) {
        String sql = """
            UPDATE slots 
            SET status='AVAILABLE', vehicle_number=NULL, reserved_by=NULL
            WHERE id=? AND (status='OCCUPIED' OR status='RESERVED')
        """;

        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
