package boxing.websocketclient;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.NotYetConnectedException;
import boxing.websocketclient.data.Constants.MessageType;

public class MainActivity extends AppCompatActivity {

    EditText mEtContent;
    Button mBtSent;
    TextView mTvMessage;

    private WebSocketClient mSocketClient;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mTvMessage.setText(mTvMessage.getText() + "\n" + msg.obj);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTvMessage = findViewById(R.id.tv_message);
        mEtContent = findViewById(R.id.et_content);
        mBtSent = findViewById(R.id.bt_send);
        init();


    }

    private void init() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mSocketClient = new WebSocketClient(new URI("ws://192.168.60.101:2018/"), new
                            Draft_6455()) {
                        @Override
                        public void onOpen(ServerHandshake handshakedata) {
                            Log.d("picher_log", "打开通道" + handshakedata.getHttpStatus());
                            handler.obtainMessage(0, "打开通道").sendToTarget();
                            IMMessage imMessage = new IMMessage();
                            imMessage.setType(MessageType.LOGIN);
                            imMessage.setSenderId(MainActivity.getUserId());
                            mSocketClient.send(JSON.toJSONString(imMessage));
                        }

                        @Override
                        public void onMessage(String message) {
                            Log.d("picher_log", "接收消息" + message);
                            IMMessage imMessage = JSON.parseObject(message, IMMessage.class);
                            handler.obtainMessage(0, imMessage.getContent()).sendToTarget();
                        }

                        @Override
                        public void onClose(int code, String reason, boolean remote) {
                            Log.d("picher_log", "通道关闭" + code + " reson:" + reason + remote);
                            handler.obtainMessage(0, "通道关闭").sendToTarget();
                        }

                        @Override
                        public void onError(Exception ex) {
                            Log.d("picher_log", "链接错误" + ex.getMessage());
                        }
                    };
                    mSocketClient.connect();

                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
        }).start();


        mBtSent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSocketClient != null) {
                    try {
                        String content = mEtContent.getText().toString().trim();
                        IMMessage imMessage = new IMMessage();
                        imMessage.setType(MessageType.PRIVATE);
                        imMessage.setSenderId(MainActivity.getUserId());
                        imMessage.setReceiverId(0);
                        imMessage.setContent(content);
                        mSocketClient.send(JSON.toJSONString(imMessage));
                    } catch ( NotYetConnectedException e) {
                        Log.e("", e.getMessage(), e);
                    }
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSocketClient != null) {
            mSocketClient.close();
        }
    }

    public static int getUserId(){
        return 1;
    }
}
