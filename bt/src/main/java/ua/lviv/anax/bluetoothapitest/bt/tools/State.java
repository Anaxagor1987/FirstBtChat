package ua.lviv.anax.bluetoothapitest.bt.tools;


import android.bluetooth.BluetoothDevice;
import android.os.Handler;
import ua.lviv.anax.bluetoothapitest.bt.tools.message.Message;

import java.util.UUID;

/**
 * @author rostyslav.lesovyi
 */
public abstract class State {
	public enum Id { Idle, Connecting, Pending, Ready }

	protected static final String NAME = "BluetoothTest";
	protected static final UUID MY_UUID = UUID.fromString("fa87c0d0-afac-11de-8a39-0899200c9a66");

	public abstract Id getId();
	protected abstract void onStart();
	protected abstract void onStop();

	protected ChatManager mManager;

	void start() {
		onStart();
	}

	void stop() {
		onStop();
	}

	protected void switchState(State state) {
		mManager.switchState(this, state);
	}

    public Handler getHandler() {
        return mManager.mHandler;
    }

	public void connect(BluetoothDevice device) {
	}

	public void sendMessage(Message message) {
	}
}
