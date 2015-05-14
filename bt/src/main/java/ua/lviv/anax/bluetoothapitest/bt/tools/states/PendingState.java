package ua.lviv.anax.bluetoothapitest.bt.tools.states;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import ua.lviv.anax.bluetoothapitest.bt.tools.Connection;
import ua.lviv.anax.bluetoothapitest.bt.tools.MessageId;
import ua.lviv.anax.bluetoothapitest.bt.tools.State;
import ua.lviv.anax.bluetoothapitest.bt.tools.message.Message;
import ua.lviv.anax.bluetoothapitest.bt.tools.message.ServiceMessageAccept;
import ua.lviv.anax.bluetoothapitest.bt.tools.message.ServiceMessageDecline;

/**
 * @author rostyslav.lesovyi
 */
public class PendingState extends State implements Handler.Callback{

    private Connection mConnection;

	public PendingState(BluetoothSocket socket) {
        mConnection = new Connection(socket.getRemoteDevice());
	}

	@Override
	public State.Id getId() {
		return Id.Pending;
	}

	@Override
	protected void onStart() {
        mManager.addCallBack(this);
	}

	@Override
	protected void onStop() {
        mManager.removeCallback(this);
	}

    @Override
    public boolean handleMessage(android.os.Message msg) {
        if (msg.what !=  MessageId.PermissionMessage) {
            return false;
        }
        Message message = (Message) msg.obj;
        switch (message.getId()) {
            case Message.SERVICE_MESSAGE_ACCEPT:
                sendMessage(new ServiceMessageAccept());
                switchState(new ReadyState(mConnection));
                break;
            case Message.SERVICE_MESSAGE_DECLINE:
                sendMessage(new ServiceMessageDecline());
                switchState(new IdleState());
                mConnection.close();
                break;
        }
        return true;
    }
}
