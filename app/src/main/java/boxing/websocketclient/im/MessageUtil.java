package boxing.websocketclient.im;

import com.alibaba.fastjson.JSON;

import boxing.websocketclient.base.BaseActivity;
import boxing.websocketclient.data.Constants;
import boxing.websocketclient.models.IMMessage;

/**
 * @author Liuyuli
 * @date 2018/5/29.
 */

public class MessageUtil {
    /**
     * 登录
     */
    public static void sendLogin() {
        IMMessage imMessage = new IMMessage();
        imMessage.setType(Constants.MessageType.LOGIN);
        int userId = BaseActivity.getUserId();
        imMessage.setSenderId(userId);
        imMessage.setReceiverId(userId);
        imMessage.setContent("login");
        SocketListener.getINSTENCE().send(JSON.toJSONString(imMessage));
    }

    /**
     * 私聊
     * @param receiverId 接受者id
     * @param content 消息内容
     */
    public static void sendPrivate(int receiverId, String content) {
        IMMessage imMessage = new IMMessage();
        imMessage.setType(Constants.MessageType.PRIVATE);
        imMessage.setSenderId(BaseActivity.getUserId());
        imMessage.setReceiverId(receiverId);
        imMessage.setContent(content);
        SocketListener.getINSTENCE().send(JSON.toJSONString(imMessage));
    }

    /**
     * 私聊
     * @param groupId 接受者id
     * @param content 消息内容
     */
    public static void sendGroup(int groupId, String content) {
        IMMessage imMessage = new IMMessage();
        imMessage.setType(Constants.MessageType.PRIVATE);
        imMessage.setSenderId(BaseActivity.getUserId());
        imMessage.setGroupId(groupId);
        imMessage.setContent(content);
        SocketListener.getINSTENCE().send(JSON.toJSONString(imMessage));
    }
}
