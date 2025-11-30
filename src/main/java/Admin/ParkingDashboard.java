
package Admin;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import java.util.List;

public class ParkingDashboard extends JFrame {

    // --- Role-aware dashboard: pass "ADMIN" or "CLIENT" ---
    private final String role; // "ADMIN" or "CLIENT"

    // DAO (use your implementation: MySqlSlotDao or SlotStore)
    private final SlotDao dao = new MySqlSlotDao(); // or: new SlotStore();

    // Data
    private List<Slot> slots = new ArrayList<>();

    // UI references
    private final Map<String, JButton> slotButtons = new HashMap<>();
    private final Map<String, JLabel> statValues = new HashMap<>();
    private JComboBox<String> slotSelect;
    private JTextField vehicleField;
    private JLabel selectedInfo;
    private JButton logoutBtn; // Declare logout button

    // Action buttons
    private JButton parkBtn;
    private JButton reserveBtn;
    private JButton removeBtn;

    // --- Constructors ---
    public ParkingDashboard(String role) {
        super("Parking Management System");
        this.role = (role == null) ? "ADMIN" : role.toUpperCase(Locale.ROOT);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setSize(1100, 720);

        // Gradient background container
        UiUtils.GradientPanel bg = new UiUtils.GradientPanel();
        bg.setLayout(new BorderLayout(16, 16));
        bg.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        setContentPane(bg);

        // Header
        JLabel header = new JLabel("Parking Management System");
        header.setFont(header.getFont().deriveFont(Font.BOLD, 22f));
        header.setForeground(Color.WHITE);
        bg.add(header, BorderLayout.NORTH);

        // Center area
        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);
        bg.add(center, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.BOTH;

        // --- Top stats card ---
        UiUtils.CardPanel statsCard = new UiUtils.CardPanel(UiUtils.CARD_BG);
        statsCard.setLayout(new GridBagLayout());
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.weightx = 1.0; gbc.weighty = 0.0;
        center.add(statsCard, gbc);

        GridBagConstraints sg = new GridBagConstraints();
        sg.insets = new Insets(8, 8, 8, 8);
        sg.fill = GridBagConstraints.BOTH;
        sg.weightx = 1.0; sg.weighty = 1.0;

        JPanel totalTile = UiUtils.statTile("Total Slots", "0", UiUtils.GREEN);
        JPanel availTile = UiUtils.statTile("Available", "0", UiUtils.GREEN.darker());
        JPanel occTile   = UiUtils.statTile("Occupied", "0", UiUtils.RED);
        JPanel resTile   = UiUtils.statTile("Reserved", "0", UiUtils.ORANGE);

        statsCard.add(totalTile, tileConstraints(sg, 0));
        statsCard.add(availTile, tileConstraints(sg, 1));
        statsCard.add(occTile,   tileConstraints(sg, 2));
        statsCard.add(resTile,   tileConstraints(sg, 3));

        // Capture value labels
        statValues.put("total", (JLabel)((BorderLayout) totalTile.getLayout()).getLayoutComponent(BorderLayout.CENTER));
        statValues.put("available", (JLabel)((BorderLayout) availTile.getLayout()).getLayoutComponent(BorderLayout.CENTER));
        statValues.put("occupied", (JLabel)((BorderLayout) occTile.getLayout()).getLayoutComponent(BorderLayout.CENTER));
        statValues.put("reserved", (JLabel)((BorderLayout) resTile.getLayout()).getLayoutComponent(BorderLayout.CENTER));

        // --- Slots card (grid) ---
        UiUtils.CardPanel slotsCard = new UiUtils.CardPanel(UiUtils.CARD_BG);
        slotsCard.setLayout(new BorderLayout(8, 8));
        JLabel slotsTitle = new JLabel("Parking Slots");
        slotsTitle.setFont(slotsTitle.getFont().deriveFont(Font.BOLD, 16f));
        slotsCard.add(slotsTitle, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(4, 5, 12, 12));
        grid.setOpaque(false);
        slotsCard.add(grid, BorderLayout.CENTER);

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1; gbc.weightx = 0.65; gbc.weighty = 1.0;
        center.add(slotsCard, gbc);

        // --- Manage card (right) ---
        UiUtils.CardPanel manageCard = new UiUtils.CardPanel(UiUtils.CARD_BG);
        manageCard.setLayout(new GridBagLayout());
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0.35; gbc.weighty = 1.0;
        center.add(manageCard, gbc);

        GridBagConstraints mg = new GridBagConstraints();
        mg.insets = new Insets(8, 8, 8, 8);
        mg.fill = GridBagConstraints.HORIZONTAL;
        mg.gridx = 0; mg.weightx = 1.0;

        JLabel manageTitle = new JLabel("Vehicle Management");
        manageTitle.setFont(manageTitle.getFont().deriveFont(Font.BOLD, 16f));
        mg.gridy = 0;
        manageCard.add(manageTitle, mg);

        mg.gridy = 1; manageCard.add(new JLabel("Select Slot"), mg);
        slotSelect = new JComboBox<>();
        slotSelect.setEditable(false);
        mg.gridy = 2; manageCard.add(slotSelect, mg);

        mg.gridy = 3; manageCard.add(new JLabel("Vehicle plate Number"), mg);
        vehicleField = new JTextField();
        vehicleField.setToolTipText("e.g., ABC-1234");
        mg.gridy = 4; manageCard.add(vehicleField, mg);

        selectedInfo = new JLabel("—");
        mg.gridy = 5; manageCard.add(selectedInfo, mg);

        // Buttons
        parkBtn = new JButton("Park Vehicle");
        reserveBtn = new JButton("Reserve Slot");
        removeBtn = new JButton("Remove Vehicle");

        parkBtn.setBackground(new Color(80, 120, 250)); parkBtn.setForeground(Color.BLACK);
        reserveBtn.setBackground(new Color(80, 80, 180)); reserveBtn.setForeground(Color.BLACK);
        removeBtn.setBackground(new Color(240, 120, 80)); removeBtn.setForeground(Color.BLACK);

        mg.gridy = 6; manageCard.add(parkBtn, mg);
        mg.gridy = 7; manageCard.add(reserveBtn, mg);
        mg.gridy = 8; manageCard.add(removeBtn, mg);
        mg.gridy = 9; mg.weighty = 1.0; manageCard.add(new JPanel(), mg); // spacer

        // --- Minimal role-based visibility (THIS IS THE CHANGE YOU NEEDED) ---
        boolean isAdmin = "ADMIN".equalsIgnoreCase(this.role);
        parkBtn.setVisible(isAdmin);     // hide for CLIENT
        removeBtn.setVisible(isAdmin);   // hide for CLIENT
        reserveBtn.setVisible(true);     // both roles can reserve

        // --- Load data & wire actions ---
        slots = dao.findAll();

        rebuildGrid(grid);
        rebuildSlotSelect();
        refreshStats();

        slotSelect.addActionListener(e -> {
            String id = (String) slotSelect.getSelectedItem();
            if (id != null) {
                Slot s = findSlot(id);
                selectedInfo.setText(statusLine(s));
                vehicleField.setText(s.vehicleNumber == null ? "" : s.vehicleNumber);
            }
        });

        // Actions
        parkBtn.addActionListener(e -> {
            String id = (String) slotSelect.getSelectedItem();
            String veh = vehicleField.getText().trim();
            if (id == null || veh.isEmpty()) { toast("Choose slot and enter vehicle."); return; }
            if (dao.parkVehicle(id, veh)) {
                toast("Vehicle parked successfully.");
                reloadSlots(grid);
            } else {
                toast("Failed to park vehicle.");
            }
        });

        reserveBtn.addActionListener(e -> {
            String id = (String) slotSelect.getSelectedItem();
            String reservedBy = vehicleField.getText().trim();
            if (id == null) { toast("Choose slot."); return; }
            // NOTE: If your DAO expects username (to resolve FK), pass the logged-in username here.
            // Since you don't use Model.User, we're retaining your existing behavior using vehicleField.
            if (dao.reserveSlot(id, reservedBy)) {
                toast("Slot reserved.");
                reloadSlots(grid);
            } else {
                toast("Failed to reserve slot.");
            }
        });

        removeBtn.addActionListener(e -> {
            String id = (String) slotSelect.getSelectedItem();
            if (id == null) { toast("Choose slot."); return; }
            if (dao.removeVehicle(id)) {
                toast("Vehicle removed.");
                reloadSlots(grid);
            } else {
                toast("Failed to remove vehicle.");
            }
        });


        // --- Action buttons ---
// In the constructor, add the logout button to the UI
        logoutBtn = new JButton("Logout");
        logoutBtn.setBackground(Color.RED); // You can customize the color
        logoutBtn.setForeground(Color.WHITE);

// Add the logout button to the layout (for example, at the bottom)
        mg.gridy = 10; // Adjust this based on where you want it
        mg.weighty = 0.0;
        manageCard.add(logoutBtn, mg);

// Wire the logout action
        logoutBtn.addActionListener(e -> {
            // Close current dashboard
            dispose();
            // Open the login window again (AdminLogin is assumed here)
            SwingUtilities.invokeLater(() -> new AdminLogin().setVisible(true));
        });
    }

    // Keep a default constructor to preserve existing calls
    public ParkingDashboard() {
        this("ADMIN");
    }

    // --- Helpers ---

    private void reloadSlots(JPanel grid) {
        slots = dao.findAll();
        rebuildGrid(grid);
        rebuildSlotSelect();
        refreshStats();
    }

    private static GridBagConstraints tileConstraints(GridBagConstraints base, int x) {
        GridBagConstraints c = (GridBagConstraints) base.clone();
        c.gridx = x;
        c.gridy = 0;
        return c;
    }

    private void rebuildGrid(JPanel grid) {
        grid.removeAll();
        slotButtons.clear();
        for (Slot s : slots) {
            JButton tile = UiUtils.slotTile(s);
            tile.addActionListener(e -> {
                slotSelect.setSelectedItem(s.id);
            });
            grid.add(tile);
            slotButtons.put(s.id, tile);
        }
        grid.revalidate();
        grid.repaint();
    }

    private void rebuildSlotSelect() {
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        slots.stream().map(sl -> sl.id).forEach(model::addElement);
        slotSelect.setModel(model);
        if (model.getSize() > 0) slotSelect.setSelectedIndex(0);

        String id = (String) slotSelect.getSelectedItem();
        if (id != null) {
            Slot s = findSlot(id);
            selectedInfo.setText(statusLine(s));
            vehicleField.setText(s != null && s.vehicleNumber != null ? s.vehicleNumber : "");
        }
    }

    private void refreshStats() {
        UiUtils.updateStats(slots, statValues);
    }

    private Slot findSlot(String id) {
        return slots.stream().filter(s -> Objects.equals(s.id, id)).findFirst().orElse(null);
    }

    private String statusLine(Slot s) {
        if (s == null) return "—";
        return switch (s.status) {
            case AVAILABLE -> s.id + " — Available";
            case RESERVED  -> s.id + " — Reserved" + (s.reservedBy == null ? "" : " by " + s.reservedBy);
            case OCCUPIED  -> s.id + " — Occupied" + (s.vehicleNumber == null ? "" : " (" + s.vehicleNumber + ")");
        };
    }

    private void toast(String msg) {
        JOptionPane.showMessageDialog(this, msg);
    }
}
