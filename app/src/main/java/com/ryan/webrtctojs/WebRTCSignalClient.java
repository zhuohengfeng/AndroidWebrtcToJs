package com.ryan.webrtctojs;

import android.util.Log;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

/**
 * 处理客户端收发信令
 */
public class WebRTCSignalClient {
    private static final String TAG = "zhf_webrtc";

    private static WebRTCSignalClient mInstance;

    public static final String SIG_SERVER_JOINED = "joined";
    public static final String SIG_SERVER_OTHER_JOINED = "other_joined";
    public static final String SIG_SERVER_LEAVED = "leaved";
    public static final String SIG_SERVER_BYE = "bye";
    public static final String SIG_SERVER_FULL = "full";

    public static final String SIG_CLIENT_JOIN = "join";
    public static final String SIG_CLIENT_LEAVE = "leave";
    public static final String SIG_CLIENT_MESSAGE = "message";

    private Socket mSocket = null;
    private String mRoomName;

    private WebRTCSignalClient() {}

    public static WebRTCSignalClient getInstance() {
        if (mInstance == null) {
            synchronized (WebRTCSignalClient.class) {
                if (mInstance == null) {
                    mInstance = new WebRTCSignalClient();
                }
            }
        }
        return mInstance;
    }

    /**
     * 主动连接信令服务器，并加入房间
     * @param url
     * @param roomName
     * @return
     */
    public boolean joinRoom(String url, String roomName) {
        Log.d(TAG, "===>joinRoom() 开始连接信令服务器，加入房间 url="+url+", roomName="+roomName);

        // 连接信令服务器
        try {
            mSocket = IO.socket(url);
            mSocket.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        if (mSocket == null) {
            Log.e(TAG, "信令服务器连接失败");
            return false;
        }

        // 保存房间名
        mRoomName = roomName;

        /** ------注册信令消息-------- */
        // 1. 加入成功的消息
        mSocket.on(SIG_SERVER_JOINED, args -> {
            String room_name = (String) args[0];
            String userId = (String) args[1];
            Log.d(TAG, "<=== 收到Joined消息 room_name="+room_name+", userId="+userId);

        });

        // 2. 其他用户加入的消息
        mSocket.on(SIG_SERVER_OTHER_JOINED, args -> {
            Log.d(TAG, "<=== 收到other_Joined消息 args="+args);


        });

        // 3. 用户离开的消息
        mSocket.on(SIG_SERVER_LEAVED, args -> {
            Log.d(TAG, "<=== 收到leaved消息 args="+args);
            String room_name = (String) args[0];
            String userId = (String) args[1];
            Log.d(TAG, "<=== 收到leaved消息 room_name="+room_name+", userId="+userId);
        });

        // 4. 退出的消息
        mSocket.on(SIG_SERVER_BYE, args -> {
            Log.d(TAG, "<=== 收到bye消息 args="+args);


        });

        // 5. 当前服务器满的消息
        mSocket.on(SIG_SERVER_FULL, args -> {
            Log.d(TAG, "<=== 收到full消息 args="+args);

        });

        /** ------主动发送join消息, 告知服务器加入那个房间-------- */
        mSocket.emit(SIG_CLIENT_JOIN, mRoomName);

        return true;
    }

    /**
     * 离开房间
     */
    public void leaveRoom() {
        Log.d(TAG, "===>leaveRoom() 离开房间 roomName="+mRoomName);

        if (mSocket == null) {
            Log.e(TAG, "[错误] leaveRoom() mSocket为空");
            return;
        }
        // 离开房间，断开socket
        mSocket.emit(SIG_CLIENT_LEAVE, mRoomName);
        //mSocket.close(); // 这里关闭后，可能回调就收不到了
        //mSocket = null;
    }

    /**
     * 发送消息
     * @param message
     */
    public void sendMessage(String message) {
        Log.d(TAG, "===>sendMessage() 发送消息 message="+message + ", mRoomName="+mRoomName);

        if (mSocket == null) {
            Log.e(TAG, "[错误] sendMessage() mSocket为空");
            return;
        }

        mSocket.emit(SIG_CLIENT_MESSAGE, mRoomName, message);
    }








}
