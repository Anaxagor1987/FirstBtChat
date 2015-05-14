package ua.lviv.anax.bluetoothapitest.bt.adapters;

import android.content.Context;
import android.text.Spannable;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import ua.lviv.anax.bluetoothapitest.bt.R;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yurii.ostrovskyi on 10/22/2014.
 */
public class ChatAdapter extends BaseAdapter{
    private List<String> mMessages;
    private LayoutInflater mInflater;
    private Context mContext;

    private static final int TYPE_SENT = 0;
    private static final int TYPE_RECEIVED = 1;
    private static final int TYPE_MAX_COUNT = 2;

    private TreeSet<Integer> receivedMessages;

    public ChatAdapter(Context context) {
        this.mContext=context;
        this.mMessages = new ArrayList<String>();
        receivedMessages = new TreeSet<Integer>();
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void addMessage(String msg) {
        mMessages.add(msg);
        notifyDataSetChanged();
    }

    public void addReceivedMessage(String msg) {
        mMessages.add(msg);
        receivedMessages.add(mMessages.size() - 1);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mMessages.size();
    }

    @Override
    public String getItem(int position) {
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
            switch(type){
                case TYPE_SENT:
                    convertView = mInflater.inflate(R.layout.v_chat_item_sent, parent, false);
                    holder.messageTv = (TextView)convertView.findViewById(R.id.tvMessage);
                    holder.nickName = (TextView)convertView.findViewById(R.id.tvNickName);
                    holder.avatar = (ImageView)convertView.findViewById(R.id.iv);
//                    holder.messageTv.setText(mMessages.get(position));
//                    holder.nickName.setText("Lord");
//                    Picasso.with(mContext).load("http://i.imgur.com/oRa6Q.jpg").into(holder.avatar);
                    break;
                default:
                    convertView = mInflater.inflate(R.layout.v_chat_item_received, parent, false);
                    holder.messageTv = (TextView)convertView.findViewById(R.id.tvMessageReceived);
                    holder.nickName = (TextView)convertView.findViewById(R.id.tvNickNameOponent);
                    holder.avatar = (ImageView)convertView.findViewById(R.id.ivAvatarOponent);
//                    holder.messageTv.setText(mMessages.get(position));
//                    holder.nickName.setText("Wolf");
//                    Picasso.with(mContext).load("http://i.imgur.com/XGHjON3.jpg").into(holder.avatar);
                    break;
            }
            convertView.setTag(holder);
        }else{
            holder = (MessageHolder)convertView.getTag();
        }
        Spannable spanText = getSmiledText( mContext, mMessages.get(position));
        holder.messageTv.setText(spanText);
        holder.nickName.setText("Wolf");
        Picasso.with(mContext).load("http://i.imgur.com/XGHjON3.jpg").into(holder.avatar);
//        holder.nickName.setText(mMessages.get(position).getData().getString("nickName"));
//        Picasso.with(mContext).load(mMessages.get(position).getData().getString("avatar"));

        return convertView;
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
