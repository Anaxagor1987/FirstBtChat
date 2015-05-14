package ua.lviv.anax.bluetoothapitest.bt.tools.states;

import android.os.Handler;
import ua.lviv.anax.bluetoothapitest.bt.App;
import ua.lviv.anax.bluetoothapitest.bt.tools.Connection;
import ua.lviv.anax.bluetoothapitest.bt.tools.MessageId;
import ua.lviv.anax.bluetoothapitest.bt.tools.State;
import ua.lviv.anax.bluetoothapitest.bt.tools.message.Message;

/**
 * @author rostyslav.lesovyi
 */
public class ReadyState extends State implements Handler.Callback, Connection.Callback{

    private Connection mConnection;

	public ReadyState(Connection connection) {
        mConnection = connection;
	}

	@Override
	public Id getId() {
		return Id.Ready;
	}

	@Override
	protected void onStart() {
        mManager.addCallBack(this);
        mConnection.addCallback(this);
	}

	@Override
	protected void onStop() {
        mManager.removeCallback(this);
        mConnection.removeCallback(this);
	}

	@Override
	public void sendMessage(Message message) {
		mConnection.sendMessage(message);
	}

    @Override
    public boolean handleMessage(android.os.Message msg) {
        if (msg.what != MessageId.ChatMessageSent && msg.what != MessageId.ProfileMessageSent) {
            return false;
        }
        sendMessage((Message) msg.obj);
        return true;
    }

    @Override
    public void onConnected(Connection connection) {
        App.self().showToast("Connected");
    }

    @Override
    public void onDisconnected(Connection connection) {
        switchState(new IdleState());
    }

    @Override
    public void onMessageReceived(Connection connection, Message message) {
        switch (message.getId()){
            case Message.CHAT_MESSAGE_ID:
                mManager.getHandler().obtainMessage(MessageId.ChatMessageReceived, message)
                        .sendToTarget();
                break;
            case Message.PROFILE_MESSAGE_ID:
                mManager.getHandler().obtainMessage(MessageId.ProfileMessageReceived, message)
                        .sendToTarget();
                break;
        }
    }
}
