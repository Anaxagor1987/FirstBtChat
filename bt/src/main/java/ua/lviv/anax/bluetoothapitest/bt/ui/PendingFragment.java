package ua.lviv.anax.bluetoothapitest.bt.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import ua.lviv.anax.bluetoothapitest.bt.R;
import ua.lviv.anax.bluetoothapitest.bt.tools.MessageId;
import ua.lviv.anax.bluetoothapitest.bt.tools.message.ServiceMessageAccept;
import ua.lviv.anax.bluetoothapitest.bt.tools.message.ServiceMessageDecline;

/**
 * @author yurii.ostrovskyi
 */
public class PendingFragment extends Fragment implements View.OnClickListener {

    public static PendingFragment newInstance() {
        return new PendingFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.f_pending, container, false);

        Button btnAccept = (Button)view.findViewById(R.id.btnAccept);
        Button btnDecline = (Button)view.findViewById(R.id.btnDecline);

        btnAccept.setOnClickListener(this);
        btnDecline.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        MainActivity mainActivity = (MainActivity) getActivity();
        switch (v.getId()){
            case R.id.btnAccept:
                mainActivity.getChatManager().getHandler()
                        .obtainMessage(MessageId.PermissionMessage, new ServiceMessageAccept())
                        .sendToTarget();
                break;
            case R.id.btnDecline:
                mainActivity.getChatManager().getHandler()
                        .obtainMessage(MessageId.PermissionMessage, new ServiceMessageDecline())
                        .sendToTarget();
                break;
        }
    }
}
