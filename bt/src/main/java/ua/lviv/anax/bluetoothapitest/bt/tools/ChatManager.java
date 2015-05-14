package ua.lviv.anax.bluetoothapitest.bt.tools;

import android.bluetooth.BluetoothDevice;
import android.os.Handler;
import android.os.Message;
import ua.lviv.anax.bluetoothapitest.bt.tools.states.IdleState;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yurii.ostrovskyi
 */
public class ChatManager implements Handler.Callback {

    private State mState;

    Handler mHandler;
    private List<Handler.Callback> mCallback = new ArrayList<Handler.Callback>();

    public ChatManager() {
        mHandler = new Handler(this);
        switchState(null, new IdleState());
    }

    void switchState(State oldState, State newState) {
		if (oldState != mState) {
			return;
			}
		if (mState != null) {
			mState.stop();
			}
		mState = newState;
		mState.mManager = this;
		mState.start();
		mHandler.obtainMessage(MessageId.StateChanged, mState.getId()).sendToTarget();
	}

    public Handler getHandler() {
        return mHandler;
    }

    public State.Id getStateId() {
        return mState.getId();
    }


    public void addCallBack(Handler.Callback callback) {
        mCallback.add(callback);
    }

    public void removeCallback(Handler.Callback callback) {
        mCallback.remove(callback);
    }

    public void connect(BluetoothDevice device) {
        mState.connect(device);
    }

    @Override
    public boolean handleMessage(Message msg) {
        for (Handler.Callback callback : mCallback) {
            callback.handleMessage(msg);
        }
        return true;
    }
}