package Admin;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MySqlSlotDao implements SlotDao {

    public MySqlSlotDao() {}

    @Override
    public List<Slot> findAll() {
        String sql = """
            SELECT
              id,
              name,
              status,
              vehicle_number,
              reserved_by
            FROM slots
            ORDER BY id
            """;

        List<Slot> out = new ArrayList<>();
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                out.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return out;
    }

    @Override
    public Slot findById(String id) {
        String sql = """
            SELECT
              id,
              name,
              status,
              vehicle_number,
              reserved_by
            FROM slots
            WHERE id = ?
            """;

        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean parkVehicle(String id, String vehicleNumber) {
        String sql = """
            UPDATE slots
               SET status = 'OCCUPIED',
                   vehicle_number = ?
             WHERE id = ?
               AND status IN ('AVAILABLE','RESERVED')
            """;

        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, vehicleNumber);
            ps.setString(2, id);
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean reserveSlot(String id, String reservedBy) {
        String sql = """
            UPDATE slots
               SET status = 'RESERVED',
                   reserved_by = ?
             WHERE id = ?
               AND status = 'AVAILABLE'
            """;

        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, reservedBy);
            ps.setString(2, id);
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean removeVehicle(String id) {
        String sql = """
            UPDATE slots
               SET status = 'AVAILABLE',
                   vehicle_number = NULL,
                   reserved_by = NULL
             WHERE id = ?
               AND status IN ('OCCUPIED','RESERVED')
            """;

        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, id);
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Slot map(ResultSet rs) throws SQLException {
        Slot s = new Slot();

        // DB → UI
        s.id = rs.getString("id");
        s.name = rs.getString("name");
        s.status = SlotStatus.fromDb(rs.getString("status"));
        s.vehicleNumber = rs.getString("vehicle_number");
        s.reservedBy = rs.getString("reserved_by");

        return s;
    }
}
