package ua.lviv.anax.bluetoothapitest.bt.ui;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import ua.lviv.anax.bluetoothapitest.bt.tools.ChatManager;
import ua.lviv.anax.bluetoothapitest.bt.R;
import ua.lviv.anax.bluetoothapitest.bt.tools.MessageId;
import ua.lviv.anax.bluetoothapitest.bt.tools.State;

/**
 * @author yurii.ostrovskyi
 */
public class MainActivity extends Activity implements Handler.Callback {

    private ChatManager mChatManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.a_main);

        mChatManager = new ChatManager();
        mChatManager.addCallBack(this);
        switchFragment(mChatManager.getStateId());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mChatManager.removeCallback(this);
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (msg.what != MessageId.StateChanged) {
            return false;
        }
        switchFragment((State.Id) msg.obj);
        return true;
    }

    public ChatManager getChatManager() {
        return mChatManager;
    }

    private void switchFragment(State.Id stateId) {
        switch (stateId) {
            case Idle:
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, IdleFragment.newInstance())
                        .commit();
                break;
            case Connecting:
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, ConnectingFragment.newInstance())
                        .commit();
                break;
            case Pending:
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, PendingFragment.newInstance())
                        .commit();
			case Ready:
				getFragmentManager().beginTransaction()
						.replace(R.id.fragment_container, ReadyFragment.newInstance())
						.commit();
        }
    }
}
