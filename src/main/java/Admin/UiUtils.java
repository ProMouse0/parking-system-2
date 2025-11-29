
package Admin;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class UiUtils {

    // Gradient background panel
    public static class GradientPanel extends JPanel {
        private final Color start = new Color(99, 36, 189);   // purple
        private final Color end = new Color(52, 157, 221);  // blue-violet

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();
            GradientPaint gp = new GradientPaint(0, 0, start, 0, h, end);
            g2.setPaint(gp);
            g2.fillRect(0, 0, w, h);
            g2.dispose();
        }
    }

    // Rounded card panel
    public static class CardPanel extends JPanel {
        private final int arc = 16;
        private final Color bg;

        public CardPanel(Color bg) {
            this.bg = bg;
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bg);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // Colors inspired by the screenshot
    public static final Color GREEN = new Color(46, 204, 113);   // available
    public static final Color RED = new Color(231, 76, 60);    // occupied
    public static final Color ORANGE = new Color(243, 156, 18);   // reserved
    public static final Color CARD_BG = new Color(250, 250, 250); // white-ish
    public static final Color SLOT_BORDER = new Color(200, 200, 200);

    public static JPanel statTile(String title, String value, Color color) {
        CardPanel p = new CardPanel(color);
        p.setLayout(new BorderLayout(6, 6));
        JLabel t = new JLabel(title);
        t.setForeground(Color.WHITE);
        t.setFont(t.getFont().deriveFont(Font.BOLD, 14f));
        JLabel v = new JLabel(value);
        v.setForeground(Color.WHITE);
        v.setFont(v.getFont().deriveFont(Font.BOLD, 22f));
        p.add(t, BorderLayout.NORTH);
        p.add(v, BorderLayout.CENTER);
        return p;
    }

    public static JButton slotTile(Slot s) {
        JButton b = new JButton();
        b.setLayout(new BorderLayout());
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createLineBorder(SLOT_BORDER, 2));
        b.setBackground(colorForStatus(s.status));
        b.setOpaque(true);
        b.setPreferredSize(new Dimension(110, 90));

        JLabel id = new JLabel(s.id, SwingConstants.CENTER);
        id.setFont(id.getFont().deriveFont(Font.BOLD, 16f));
        JLabel status = new JLabel(labelForStatus(s), SwingConstants.CENTER);
        status.setFont(status.getFont().deriveFont(Font.PLAIN, 12f));
        b.add(id, BorderLayout.NORTH);
        b.add(status, BorderLayout.CENTER);
        return b;
    }

    public static Color colorForStatus(SlotStatus status) {
        return switch (status) {
            case AVAILABLE -> GREEN;
            case RESERVED -> ORANGE;
            case OCCUPIED -> RED;
        };
    }

    public static String labelForStatus(Slot s) {
        return switch (s.status) {
            case AVAILABLE -> "Available";
            case RESERVED -> "Reserved";
            case OCCUPIED -> (s.vehicleNumber == null ? "Occupied" : "Occupied: " + s.vehicleNumber);
        };
    }

    public static void updateStats(List<Slot> slots, Map<String, JLabel> statLabels) {
        int total = slots.size();
        int available = (int) slots.stream().filter(x -> x.status == SlotStatus.AVAILABLE).count();
        int occupied = (int) slots.stream().filter(x -> x.status == SlotStatus.OCCUPIED).count();
        int reserved = (int) slots.stream().filter(x -> x.status == SlotStatus.RESERVED).count();

        statLabels.get("total").setText(String.valueOf(total));
        statLabels.get("available").setText(String.valueOf(available));
        statLabels.get("occupied").setText(String.valueOf(occupied));
        statLabels.get("reserved").setText(String.valueOf(reserved));
    }
}
