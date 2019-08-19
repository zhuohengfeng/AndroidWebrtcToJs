package com.ryan.webrtctojs;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import org.webrtc.AudioTrack;
import org.webrtc.EglBase;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoTrack;

public class CallActivity extends Activity {

    // 打印log
    private TextView mLogcatView;


    // Opengl es
    private EglBase mRootEglBase;
    // 纹理渲染
    private SurfaceTextureHelper mSurfaceTextureHelper;

    private SurfaceViewRenderer mLocalSurfaceView;
    private SurfaceViewRenderer mRemoteSurfaceView;

    // 音视频数据
    private VideoTrack mVideoTrack;
    private AudioTrack mAudioTrack;

    // 视频采集
    private VideoCapturer mVideoCapture;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);


        mRootEglBase = EglBase.create();

        String serverAddr = getIntent().getStringExtra("ServerAdd");
        String roomName = getIntent().getStringExtra("RoomName");

        // 进入这个界面后就开始连接信令服务器，并加入房间
        boolean result = WebRTCSignalClient.getInstance().joinRoom(serverAddr, roomName);
        if (!result) {
            Toast.makeText(this, "无法连接信令服务器，退出！", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 离开服务器
        WebRTCSignalClient.getInstance().leaveRoom();
    }
}
