package com.food.kuruyia.foodretriever.connectscreen;

import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

class ServiceDiscovery extends Thread {
    private static final String TAG = "ServiceDiscovery";

    private boolean bKeepRunning = true;
    private String lastMessage = "";
    private DatagramSocket m_datagramSocket;
    private int m_messageId;

    private ConnectivityManager m_connectivityManager;

    private ServiceDiscoveryCallback m_serviceDiscoveryCallback;

    public ServiceDiscovery(Context context) {
        m_connectivityManager = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public void run() {
        String message;
        byte[] lmessage = new byte[1024];
        DatagramPacket packet = new DatagramPacket(lmessage, lmessage.length);

        try {
            m_datagramSocket = new DatagramSocket(3100);

            sendDiscoveryMessage();

            while(bKeepRunning) {
                m_datagramSocket.receive(packet);
                message = new String(lmessage, 0, packet.getLength());
                lastMessage = message;

                if (m_serviceDiscoveryCallback != null) {
                    JsonElement jsonElement = new JsonParser().parse(message);
                    if (jsonElement.isJsonObject()) {
                        JsonObject m_json = jsonElement.getAsJsonObject();

                        if (m_json.has("name") && m_json.has("ip")) {
                            if (m_serviceDiscoveryCallback != null) {
                                m_serviceDiscoveryCallback.onServiceDiscoveryMessage(m_json.get("name").getAsString(), m_json.get("ip").getAsString());
                            }
                        }
                    }
                }
            }

            m_datagramSocket.close();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void kill() {
        bKeepRunning = false;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setCallback(ServiceDiscoveryCallback serviceDiscoveryCallback) {
        m_serviceDiscoveryCallback = serviceDiscoveryCallback;
    }

    public void sendDiscoveryMessage() {
        try {
            byte[] currentAddress = getCurrentIP().getAddress();
            currentAddress[3] = (byte)0xFF;
            InetAddress broadcastAddress = InetAddress.getByAddress(currentAddress);
            m_messageId++;

            String msg = String.valueOf(m_messageId);
            DatagramPacket sendPacket = new DatagramPacket(msg.getBytes(), msg.length(), broadcastAddress, 3100);
            m_datagramSocket.send(sendPacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public InetAddress getCurrentIP() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en
                    .hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                if (intf.getName().contains("wlan")) {
                    for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr
                            .hasMoreElements();) {
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        if (!inetAddress.isLoopbackAddress()
                                && (inetAddress.getAddress().length == 4)) {
                            return inetAddress;
                        }
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}

interface ServiceDiscoveryCallback {
    void onServiceDiscoveryMessage(String dogName, String ip);
}