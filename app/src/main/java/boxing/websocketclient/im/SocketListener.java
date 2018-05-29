package boxing.websocketclient.im;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

import boxing.websocketclient.activitys.MainActivity;
import boxing.websocketclient.base.BaseActivity;
import boxing.websocketclient.data.Constants;
import boxing.websocketclient.models.IMMessage;
import boxing.websocketclient.utils.L;

/**
 * @author Liuyuli
 * @date 2018/5/29.
 */

public class SocketListener extends WebSocketClient {

    private static SocketListener INSTENCE;

    /**
     * 单例模式
     * @return SocketListener
     */
    public static SocketListener getINSTENCE() {
        if (INSTENCE == null) {
            synchronized (SocketListener.class) {
                if (INSTENCE == null) {
                    try {
                        INSTENCE = new SocketListener(
                                new URI("ws://192.168.60.101:2018/"), new Draft_6455());
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return INSTENCE;
    }

    private SocketListener(URI serverUri, Draft protocolDraft) {
        super(serverUri, protocolDraft);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        L.i("打开通道" + handshakedata.getHttpStatus());
        MessageUtil.sendLogin();
    }

    @Override
    public void onMessage(String message) {
        IMMessage imMessage = JSON.parseObject(message, IMMessage.class);
        Logger.i("消息类型%s,%s收到%s的消息:%s",imMessage.getType(),imMessage.getReceiverId(),
        imMessage.getSenderId(),imMessage.getContent());
        EventBus.getDefault().post(imMessage);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        L.i("通道关闭" + code + " reson:" + reason + remote);
    }

    @Override
    public void onError(Exception ex) {
        L.i( "链接错误" + ex.getMessage());
    }

    @Override
    public void onWebsocketPing(WebSocket conn, Framedata f) {
        super.onWebsocketPing(conn, f);
        L.i("ping");
    }
}
