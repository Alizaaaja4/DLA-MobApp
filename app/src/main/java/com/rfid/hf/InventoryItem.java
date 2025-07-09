// File: com/rfid/hf/InventoryItem.java
package com.rfid.hf; // Ini sesuai dengan struktur folder Anda

import java.util.Objects;

public class InventoryItem {
    private int id;
    private String uid;
    private String decodedInfo;

    public InventoryItem(int id, String uid, String decodedInfo) {
        this.id = id;
        this.uid = uid;
        this.decodedInfo = decodedInfo;
    }

    // --- Getters ---
    public int getId() {
        return id;
    }

    public String getUid() {
        return uid;
    }

    public String getDecodedInfo() {
        return decodedInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InventoryItem that = (InventoryItem) o;
        return uid.equals(that.uid); // Unik berdasarkan UID String
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid);
    }

    @Override
    public String toString() {
        return "ID: " + id + ", UID: " + uid + ", Decoded: " + decodedInfo;
    }
}