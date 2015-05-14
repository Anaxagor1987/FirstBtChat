package ua.lviv.anax.bluetoothapitest.bt.tools.message;

import java.nio.ByteBuffer;
import java.util.Calendar;

/**
 * @author yurii.ostrovskyi
 */
public abstract class Message {
    public static final int CHAT_MESSAGE_ID = 1;
    public static final int PROFILE_MESSAGE_ID = 2;
	public static final int SERVICE_MESSAGE_ACCEPT = 3;
	public static final int SERVICE_MESSAGE_DECLINE = 4;

    public abstract byte[] serialize();

    public abstract int getId();

    public static Message deserialize(byte[] bytes) {
        int id = ByteBuffer.wrap(bytes, 4, 4).getInt();
        switch (id) {
            case CHAT_MESSAGE_ID:
                return new ChatMessage(bytes);

            case PROFILE_MESSAGE_ID:
                return new ProfileMessage(bytes);
        }
        return new ChatMessage("null", "null", Calendar.getInstance().getTimeInMillis());
    }
}
