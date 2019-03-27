package com.food.kuruyia.foodretriever.setupscreen;

public class NetworkItem {
    private final static int ENCRYPTION_WPA_TKIP = 2;
    private final static int ENCRYPTION_WPA_CCMP = 4;
    private final static int ENCRYPTION_WEP      = 5;
    private final static int ENCRYPTION_NONE     = 7;
    private final static int ENCRYPTION_AUTO     = 8;

    private String m_ssid;
    private long m_rssi;
    private int m_encryption;

    public NetworkItem(String ssid, long rssi, int encryption) {
        m_ssid = ssid;
        m_rssi = rssi;
        m_encryption = encryption;
    }

    public String getSsid() {
        return m_ssid;
    }

    public void setSsid(String ssid) {
        m_ssid = ssid;
    }

    public long getRssi() {
        return m_rssi;
    }

    public void setRssi(long rssi) {
        m_rssi = rssi;
    }

    public int getEncryption() {
        return m_encryption;
    }

    public String getEncryptionAsString() {
        switch (getEncryption()) {
            case ENCRYPTION_WPA_TKIP: return "WPA (TKIP)";
            case ENCRYPTION_WPA_CCMP: return "WPA (CCMP)";
            case ENCRYPTION_WEP: return "WEP";
            case ENCRYPTION_NONE: return "Open";
            case ENCRYPTION_AUTO: return "Auto";
            default: return "Unknown";
        }
    }

    public void setEncryption(int encryption) {
        m_encryption = encryption;
    }
}
