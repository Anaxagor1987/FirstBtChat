package ua.lviv.anax.bluetoothapitest.bt.ui;

import android.app.Activity;
import android.app.Fragment;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import ua.lviv.anax.bluetoothapitest.bt.R;
import ua.lviv.anax.bluetoothapitest.bt.tools.ChatManager;
import ua.lviv.anax.bluetoothapitest.bt.tools.MessageId;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yurii.ostrovskyi
 */
public class IdleFragment extends Fragment implements AdapterView.OnItemClickListener, Handler.Callback {

    private ChatManager mChatManager;
    private Adapter mAdapter = new Adapter();

    public static IdleFragment newInstance() {
        return new IdleFragment();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        MainActivity mainActivity = (MainActivity) activity;

        mChatManager = mainActivity.getChatManager();
        mChatManager.addCallBack(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.f_idle, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ListView deviceList = (ListView) view.findViewById(R.id.lvServiceList);
        deviceList.setAdapter(mAdapter);
        deviceList.setOnItemClickListener(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mChatManager.removeCallback(this);
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MessageId.DeviceFound:
                mAdapter.addDevice((BluetoothDevice) msg.obj);
                return true;
        }
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mChatManager.connect(mAdapter.getItem(position));
    }

    class Adapter extends BaseAdapter {

        private List<BluetoothDevice> mDeviceList = new ArrayList<BluetoothDevice>();

        @Override
        public int getCount() {
            return mDeviceList.size();
        }

        @Override
        public BluetoothDevice getItem(int position) {
            return mDeviceList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null){
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.v_item_in_device_list, parent, false);
            }
            BluetoothDevice btDevice = mDeviceList.get(position);
            TextView deviceName = (TextView) view.findViewById(R.id.tvDeviceName);
            deviceName.setText(btDevice.getName() + "\n" + btDevice.getAddress());
            return view;
        }

        public void addDevice(BluetoothDevice bluetoothDevice){
            mDeviceList.add(bluetoothDevice);
            notifyDataSetChanged();
        }
    }
}