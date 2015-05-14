package ua.lviv.anax.bluetoothapitest.bt.tools;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import org.apache.http.util.ByteArrayBuffer;
import ua.lviv.anax.bluetoothapitest.bt.App;
import ua.lviv.anax.bluetoothapitest.bt.tools.message.Message;
import ua.lviv.anax.bluetoothapitest.bt.utils.Invoker;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * @author yurii.ostrovskyi
 */
public class Connection {

    public interface Callback {
        void onConnected(Connection connection);

        void onDisconnected(Connection connection);

        void onMessageReceived(Connection connection, Message message);
    }

    private BluetoothDevice mDevice;
    private BluetoothSocket mSocket;
    private ConnectedThread mConnectedThread;
    private Invoker<Callback> mInvoker = Invoker.create(Callback.class);

    public Connection(BluetoothDevice device) {
        mDevice = device;
        mConnectedThread = new ConnectedThread();
        mConnectedThread.start();
    }

    public void addCallback(Callback callback) {
        mInvoker.callbacks.add(callback);
    }

    public void removeCallback(Callback callback) {
        mInvoker.callbacks.remove(callback);
    }

    public boolean isConnected() {
        return mConnectedThread.isAlive();
    }

    public void sendMessage(Message message) {
        try {
            mSocket.getOutputStream().write(message.serialize());
        } catch (Exception e) {
            App.self().showToast(e.getLocalizedMessage());
        }
    }

    public void close() {
        mConnectedThread.interrupt();
        mInvoker.object.onDisconnected(this);
    }

    private class ConnectedThread extends Thread {

        @Override
        public void run() {
            try {
                mSocket = mDevice.createRfcommSocketToServiceRecord(ua.lviv.anax.bluetoothapitest.bt.tools.State.MY_UUID);
                mSocket.connect();
				Log.d("MyLog", ua.lviv.anax.bluetoothapitest.bt.tools.State.MY_UUID.toString());
                mInvoker.object.onConnected(Connection.this);
            } catch (Exception e) {
                e.printStackTrace();
				App.self().showToast(e.getLocalizedMessage());
				return;
            }
			try {
				int position = 0;
				int messageLength = 0;
				ByteArrayBuffer byteArrayBuffer = new ByteArrayBuffer(4096);
				while (!Thread.interrupted()) {
					byte[] buffer = new byte[1024];
					int bytesRead = mSocket.getInputStream().read(buffer);
					if (bytesRead > 0) {
						byteArrayBuffer.append(buffer, 0, bytesRead);
						while (byteArrayBuffer.length() >= position + messageLength) {
							if (byteArrayBuffer.length() >= position + 4) {
								messageLength = ByteBuffer.wrap(byteArrayBuffer.toByteArray(), position, 4).getInt();
							}
							if (byteArrayBuffer.length() >= position + messageLength) {
								mInvoker.object.onMessageReceived(Connection.this
										, Message.deserialize(Arrays.copyOfRange(byteArrayBuffer.toByteArray()
										, position, position + messageLength)));

								if(byteArrayBuffer.length() == position + messageLength) {
									System.out.println("Cleared buffer");
									byteArrayBuffer.clear();
									messageLength = 0;
									position = 0;
									break;
								}
								position += messageLength;
								messageLength = 0;
							}

						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				App.self().showToast(e.getLocalizedMessage());
			}
		}
	}
}
