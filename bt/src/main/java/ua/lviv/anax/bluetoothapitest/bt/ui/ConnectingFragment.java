package ua.lviv.anax.bluetoothapitest.bt.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import ua.lviv.anax.bluetoothapitest.bt.R;

/**
 * Created by yurii.ostrovskyi on 10/22/2014.
 */
public class ConnectingFragment extends Fragment {

    public static ConnectingFragment newInstance () {
        return new ConnectingFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.f_connecting, container, false);
    }
}
