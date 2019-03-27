package com.food.kuruyia.foodretriever.connectscreen;

public class DiscoveredItem {
    private String m_ip;
    private String m_dogName;

    public DiscoveredItem(String ip, String dogName) {
        m_ip = ip;
        m_dogName = dogName;
    }

    public String getIp() {
        return m_ip;
    }

    public void setIp(String ip) {
        m_ip = ip;
    }

    public String getDogName() {
        return m_dogName;
    }

    public void setDogName(String dogName) {
        m_dogName = dogName;
    }
}
