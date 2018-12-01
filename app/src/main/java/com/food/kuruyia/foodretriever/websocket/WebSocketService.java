package com.food.kuruyia.foodretriever.websocket;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import androidx.annotation.Nullable;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class WebSocketService extends Service {
    private static final String TAG = "WebSocketService";

    public static final int MESSAGE_FAILURE = -1;
    public static final int MESSAGE_CONNECTION_CHANGED = 0;
    public static final int MESSAGE_SERVICE_STOPPED = 1;
    public static final int MESSAGE_RECEIVED = 2;

    private final IBinder m_binder = new LocalBinder();
    private Handler m_handler;
    private final OkHttpClient m_httpClient = new OkHttpClient();

    private boolean m_webSocketConnected = false;
    private WebSocket m_webSocket = null;
    private final EchoWebSocketListener m_echoWebSocketListener = new EchoWebSocketListener();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "Bound");
        return m_binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "Started");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d(TAG, "Stopped");

        if (m_webSocketConnected)
            m_webSocket.close(1000, "");

        Message readMsg = m_handler.obtainMessage(MESSAGE_SERVICE_STOPPED);
        readMsg.sendToTarget();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "Unbind");

        return true;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);

        Log.d(TAG, "Rebind");
    }

    public void setHandler(Handler handler) {
        this.m_handler = handler;
    }

    void connect(String address) {
        Request wsRequest = new Request.Builder()
                .url(address)
                .build();

        m_webSocket = new OkHttpClient.Builder()
                .build()
                .newWebSocket(wsRequest, m_echoWebSocketListener);
    }

    public boolean isWebSocketConnected() {
        return m_webSocketConnected;
    }

    public void disconnect() {
        if (m_webSocketConnected)
            m_webSocket.close(1000, "");
    }

    void sendRequest(String request) {
        if (m_webSocketConnected) {
            m_webSocket.send(request);

            Log.d(TAG, "WebSocket send: " + request);
        }
    }

    class LocalBinder extends Binder {
        WebSocketService getService() {
            return WebSocketService.this;
        }
    }

    final class EchoWebSocketListener extends WebSocketListener {
        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            super.onOpen(webSocket, response);

            m_webSocketConnected = true;
            Log.d(TAG, "WebSocket opened");

            Message readMsg = m_handler.obtainMessage(MESSAGE_CONNECTION_CHANGED);
            readMsg.sendToTarget();
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            super.onMessage(webSocket, text);
            Log.d(TAG, "WebSocket got: " + text);

            Message readMsg = m_handler.obtainMessage(MESSAGE_RECEIVED, text);
            readMsg.sendToTarget();
        }

        @Override
        public void onClosed(WebSocket webSocket, int code, String reason) {
            super.onClosed(webSocket, code, reason);

            m_webSocketConnected = false;
            Log.d(TAG, "WebSocket closed");

            Message readMsg = m_handler.obtainMessage(MESSAGE_CONNECTION_CHANGED);
            readMsg.sendToTarget();
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, @Nullable Response response) {
            super.onFailure(webSocket, t, response);

            m_webSocketConnected = false;
            Log.d(TAG, "WebSocket failed");

            Log.e(TAG, t.getMessage());

            Message readMsg = m_handler.obtainMessage(MESSAGE_CONNECTION_CHANGED);
            readMsg.sendToTarget();
        }
    }
}
