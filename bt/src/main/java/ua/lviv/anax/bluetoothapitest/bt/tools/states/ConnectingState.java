package ua.lviv.anax.bluetoothapitest.bt.tools.states;

import android.bluetooth.BluetoothDevice;
import ua.lviv.anax.bluetoothapitest.bt.tools.Connection;
import ua.lviv.anax.bluetoothapitest.bt.tools.State;
import ua.lviv.anax.bluetoothapitest.bt.tools.message.Message;

/**
 * @author rostyslav.lesovyi
 */
public class ConnectingState extends State implements Connection.Callback{

    private Connection mConnection;

	public ConnectingState(BluetoothDevice device) {
        mConnection = new Connection(device);
	}

	@Override
	public Id getId() {
		return Id.Connecting;
	}

	@Override
	protected void onStart() {
        mConnection.addCallback(this);
	}

	@Override
	protected void onStop() {
        mConnection.removeCallback(this);
	}

    @Override
    public void onConnected(Connection connection) {

    }

    @Override
    public void onDisconnected(Connection connection) {
        switchState(new IdleState());
    }

    @Override
    public void onMessageReceived(Connection connection, Message message) {
        switch (message.getId()) {
            case Message.SERVICE_MESSAGE_ACCEPT:
				switchState(new ReadyState(connection));
                break;
            case Message.SERVICE_MESSAGE_DECLINE:
                connection.close();
				switchState(new IdleState());
                break;
        }
    }
}
