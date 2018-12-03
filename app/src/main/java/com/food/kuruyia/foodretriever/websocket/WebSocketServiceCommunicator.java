package com.food.kuruyia.foodretriever.websocket;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class WebSocketServiceCommunicator {
    private static final String TAG = "ChatServiceComm";

    private boolean m_bound = false;

    private IWebSocketStateChange m_stateChangedCallback;
    private IWebSocketMessage m_messageCallback;

    private final Context m_context;
    private WebSocketService m_webSocketService;

    public WebSocketServiceCommunicator(Context context) {
        m_context = context;
    }

    private final Handler chatHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message inputMessage) {
            switch (inputMessage.what) {
                case WebSocketService.MESSAGE_FAILURE:
                    if (m_stateChangedCallback != null)
                        m_stateChangedCallback.onWSFailure();

                    break;
                case WebSocketService.MESSAGE_CONNECTION_CHANGED:
                    if (m_stateChangedCallback != null && m_webSocketService != null)
                        m_stateChangedCallback.onWSConnectionStatusChanged(m_webSocketService.isWebSocketConnected());

                    break;
                case WebSocketService.MESSAGE_SERVICE_STOPPED:
                    if (m_stateChangedCallback != null)
                        m_stateChangedCallback.onWSServiceStopped();

                    break;
                case WebSocketService.MESSAGE_RECEIVED:
                    if (m_messageCallback != null)
                        m_messageCallback.onWSMessage((String)inputMessage.obj);

                    break;
                default:
                    Log.d(TAG, "Got messageView " + inputMessage.what);
                    break;
            }
        }
    };

    private final ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            WebSocketService.LocalBinder binder = (WebSocketService.LocalBinder)iBinder;
            m_webSocketService = binder.getService();
            m_webSocketService.setHandler(chatHandler);
            m_bound = true;

            if (m_stateChangedCallback != null)
                m_stateChangedCallback.onWSBindingChanged(true);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            m_bound = false;

            if (m_stateChangedCallback != null)
                m_stateChangedCallback.onWSBindingChanged(false);
        }
    };

    public void bind() {
        Intent serviceIntent = new Intent(m_context, WebSocketService.class);
        m_context.startService(serviceIntent);
        m_context.bindService(serviceIntent, mConnection, Context.BIND_AUTO_CREATE);
    }

    public void unbind() {
        if (m_bound) {
            m_context.unbindService(mConnection);
            m_bound = false;
        }
    }

    public void connectWebsocket(String address) {
        if (m_bound && !m_webSocketService.isWebSocketConnected())
            m_webSocketService.connect(address);
    }

    public void setStateChangedCallback(IWebSocketStateChange stateChangedCallback) {
        m_stateChangedCallback = stateChangedCallback;
    }

    public void disconnectWebsocket() {
        if (m_bound) {
            m_webSocketService.disconnect();
        }
    }

    public boolean isConnected() {
        return m_bound && m_webSocketService.isWebSocketConnected();
    }

    public void sendMessage(String message) {
        if (m_bound) {
            m_webSocketService.sendRequest(message);
            Log.d(TAG, "WS Send: " + message);
        }
    }

    public void setMessageCallback(IWebSocketMessage messageCallback) {
        m_messageCallback = messageCallback;
    }

    public interface IWebSocketStateChange {
        void onWSFailure();
        void onWSConnectionStatusChanged(boolean isConnected);
        void onWSBindingChanged(boolean isBound);
        void onWSServiceStopped();
    }

    public interface IWebSocketMessage {
        void onWSMessage(String message);
    }
}
