package ua.lviv.anax.bluetoothapitest.bt.ui;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.squareup.picasso.Picasso;
import ua.lviv.anax.bluetoothapitest.bt.R;
import ua.lviv.anax.bluetoothapitest.bt.tools.ChatManager;
import ua.lviv.anax.bluetoothapitest.bt.tools.MessageId;
import ua.lviv.anax.bluetoothapitest.bt.tools.message.ChatMessage;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author yurii.ostrovskyi
 */
public class ReadyFragment extends Fragment implements Handler.Callback, View.OnClickListener {

    private ChatManager mChatManager;
    private EditText mMessageText;
    private Adapter mChatAdapter = new Adapter();

    public static ReadyFragment newInstance() {
        return new ReadyFragment();
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
       return inflater.inflate(R.layout.f_ready, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mMessageText = (EditText) view.findViewById(R.id.etMsg);
        Button buttonSend = (Button) view.findViewById(R.id.btnSend);
        ListView chatMessagesList = (ListView) view.findViewById(R.id.lvChat);

        chatMessagesList.setAdapter(mChatAdapter);
        buttonSend.setOnClickListener(this);
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MessageId.ChatMessageReceived:
                mChatAdapter.addReceivedMessage((ChatMessage)msg.obj);
                break;
            case MessageId.ChatMessageSent:
                mChatAdapter.addMessage((ChatMessage)msg.obj);
                break;

        }
        return false;
    }

    @Override
    public void onClick(View v) {
        String messageToSend = mMessageText.getText().toString();
        if (!messageToSend.isEmpty()) {
            Message message = mChatManager.getHandler().obtainMessage(MessageId.ChatMessageSent);
            message.obj = new ChatMessage(messageToSend, "Bobby", Calendar.getInstance().getTimeInMillis());
            message.sendToTarget();
        }
        mMessageText.setText("");
    }

    public class Adapter extends BaseAdapter {
        private static final int TYPE_SENT = 0;
        private static final int TYPE_RECEIVED = 1;
        private static final int TYPE_MAX_COUNT = 2;

        private List<ChatMessage> mMessages = new ArrayList<ChatMessage>();
        private TreeSet<Integer> receivedMessages = new TreeSet<Integer>();

        public void addMessage(ChatMessage msg) {
            mMessages.add(msg);
            notifyDataSetChanged();
        }

        public void addReceivedMessage(ChatMessage msg) {
            mMessages.add(msg);
            receivedMessages.add(mMessages.size() - 1);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mMessages.size();
        }

        @Override
        public ChatMessage getItem(int position) {
            return mMessages.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getViewTypeCount() {
            return TYPE_MAX_COUNT;
        }

        @Override
        public int getItemViewType(int position) {
            if (receivedMessages.contains(position)) {
                return TYPE_RECEIVED;
            }
            return TYPE_SENT;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MessageHolder holder;
            int type = getItemViewType(position);
            if (convertView == null) {
                holder = new MessageHolder();
                switch (type) {
                    case TYPE_SENT:
                        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.v_chat_item_sent, parent, false);
                        holder.messageTv = (TextView) convertView.findViewById(R.id.tvMessage);
                        holder.nickName = (TextView) convertView.findViewById(R.id.tvNickName);
                        holder.avatar = (ImageView) convertView.findViewById(R.id.iv);
                        break;
                    default:
                        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.v_chat_item_received, parent, false);
                        holder.messageTv = (TextView) convertView.findViewById(R.id.tvMessageReceived);
                        holder.nickName = (TextView) convertView.findViewById(R.id.tvNickNameOponent);
                        holder.avatar = (ImageView) convertView.findViewById(R.id.ivAvatarOponent);
                        break;
                }
                convertView.setTag(holder);
            } else {
                holder = (MessageHolder) convertView.getTag();
            }
            Spannable spanText = getSmiledText(parent.getContext(), mMessages.get(position).getText());
            holder.messageTv.setText(spanText);
            holder.nickName.setText(mMessages.get(position).getMessageTime().toString());
            Picasso.with(parent.getContext()).load("http://i.imgur.com/XGHjON3.jpg").into(holder.avatar);

            return convertView;
        }
    }

    static class MessageHolder {
        TextView messageTv;
        TextView nickName;
        ImageView avatar;
    }

    private static final Spannable.Factory spannableFactory = Spannable.Factory
            .getInstance();

    private static final Map<Pattern, Integer> emoticons = new HashMap<Pattern, Integer>();

    static {
        addPattern(emoticons, ":)", R.drawable.emo_im_smiling);
        addPattern(emoticons, ":(", R.drawable.emo_im_sad);
    }

    private static void addPattern(Map<Pattern, Integer> map, String smile,
                                   int resource) {
        map.put(Pattern.compile(Pattern.quote(smile)), resource);
    }

    public static boolean addSmiles(Context context, Spannable spannable) {
        boolean hasChanges = false;
        for (Map.Entry<Pattern, Integer> entry : emoticons.entrySet()) {
            Matcher matcher = entry.getKey().matcher(spannable);
            while (matcher.find()) {
                boolean set = true;
                for (ImageSpan span : spannable.getSpans(matcher.start(),
                        matcher.end(), ImageSpan.class))
                    if (spannable.getSpanStart(span) >= matcher.start()
                            && spannable.getSpanEnd(span) <= matcher.end())
                        spannable.removeSpan(span);
                    else {
                        set = false;
                        break;
                    }
                if (set) {
                    hasChanges = true;
                    spannable.setSpan(new ImageSpan(context, entry.getValue()),
                            matcher.start(), matcher.end(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        }
        return hasChanges;
    }

    public static Spannable getSmiledText(Context context, CharSequence text) {
        Spannable spannable = spannableFactory.newSpannable(text);
        addSmiles(context, spannable);
        return spannable;
    }
}

