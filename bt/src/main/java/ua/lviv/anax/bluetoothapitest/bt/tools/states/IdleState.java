package ua.lviv.anax.bluetoothapitest.bt.tools.states;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Message;
import android.util.Log;
import ua.lviv.anax.bluetoothapitest.bt.App;
import ua.lviv.anax.bluetoothapitest.bt.tools.MessageId;
import ua.lviv.anax.bluetoothapitest.bt.tools.State;

import java.util.UUID;

/**
 * @author rostyslav.lesovyi
 */
public class IdleState extends State {

    private AcceptThread mAcceptThread;
    private BluetoothAdapter mBluetoothAdapter;

    @Override
    public Id getId() {
        return Id.Idle;
    }

    @Override
    protected void onStart() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		mBluetoothAdapter.startDiscovery();

        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        App.self().registerReceiver(mBroadcastReceiver, intentFilter);

        mAcceptThread = new AcceptThread();
        mAcceptThread.start();
    }

    @Override
    protected void onStop() {
        mAcceptThread.interrupt();
        mBluetoothAdapter.cancelDiscovery();
		Log.d("myLog", "stop accept thread, canceled discovery");
        App.self().unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    public void connect(final BluetoothDevice device) {
		mManager.getHandler().post(new Runnable() {
			@Override
			public void run() {
				switchState(new ConnectingState(device));
			}
		});
    }

    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (BluetoothDevice.ACTION_FOUND.equals(intent.getAction())) {
                Message message = getHandler().obtainMessage(MessageId.DeviceFound);
                message.obj = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                message.sendToTarget();
            }
        }
    };

    private class AcceptThread extends Thread {
        @Override
        public void run() {
            BluetoothServerSocket socket;
            try {
                socket = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
            	Log.d("MyLog", "bluetooth socket created with UUID = " +MY_UUID.toString());
            } catch (Exception e) {
                App.self().showToast(e.getLocalizedMessage());
                e.printStackTrace();
                return;
            }
            while (!Thread.interrupted()) {
                try {
					final BluetoothSocket bluetoothSocket = socket.accept();
					Log.d("MyLog", "Incomming connection");
					mManager.getHandler().post(new Runnable() {
						@Override
						public void run() {
							switchState(new PendingState(bluetoothSocket));
						}
					});
                } catch (Exception e) {
                    App.self().showToast(e.getLocalizedMessage());
                }
            }
        }
    }
}
