package boxing.websocketclient.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import boxing.websocketclient.R;
import boxing.websocketclient.base.BaseActivity;
import boxing.websocketclient.data.Constants;
import boxing.websocketclient.im.MessageUtil;
import boxing.websocketclient.im.SocketListener;
import boxing.websocketclient.models.IMMessage;

public class MainActivity extends BaseActivity {
    EditText mEtContent;
    Button mBtSent;
    TextView mTvMessage;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        if (TextUtils.isEmpty(BaseActivity.getUserName())) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(mActivity, LoginActivity.class);
                    startActivity(intent);
                }
            }).start();
        } else {
            SocketListener.getINSTENCE().connect();
        }
        EventBus.getDefault().register(this);
        mTvMessage = findViewById(R.id.tv_message);
        mEtContent = findViewById(R.id.et_content);
        mBtSent = findViewById(R.id.bt_send);
    }

    @Override
    protected void loadNetData() {
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveMessage(IMMessage imMessage) {
        switch (imMessage.getType()) {
            case Constants.MessageType.LOGIN:
                break;
            case Constants.MessageType.PRIVATE:
                String content = imMessage.getContent();
                mTvMessage.setText(mTvMessage.getText().toString() + "\n" + content);
                break;
            case Constants.MessageType.GROUP:

                break;
            default:
                break;
        }
    }

    public void onSend(View view) {
        String content = mEtContent.getText().toString().trim();
        MessageUtil.sendPrivate(1, content);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
