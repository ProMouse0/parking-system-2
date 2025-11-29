
package Admin;

public enum SlotStatus {
    AVAILABLE, RESERVED, OCCUPIED;

    public static SlotStatus fromDb(String s) {
        if (s == null) return null;
        return SlotStatus.valueOf(s.toUpperCase());
    }
}

