package ua.lviv.anax.bluetoothapitest.bt.tools.message;

import org.apache.http.util.ByteArrayBuffer;

import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.util.Arrays;

/**
 * @author yurii.ostrovskyi
 */
public class ChatMessage extends Message{

    private String mText;
    private String mName;
    private Timestamp mMessageTime;

    public ChatMessage(String text, String name, long timeInMillis) {
        mText = text;
        mName = name;
        mMessageTime = new Timestamp(timeInMillis);
    }

    ChatMessage(byte[] bytes) {
        int nameSizeOffSet = 8;
        int textOffSet = 12;
        int nameOffSet = 16;

        int textLength = ByteBuffer.wrap(bytes, nameSizeOffSet, 4).getInt();
        mText = new String(Arrays.copyOfRange(bytes, textOffSet, textOffSet + textLength));
        int nameLength = ByteBuffer.wrap(bytes, textOffSet + textLength, 4).getInt();
        mName = new String(Arrays.copyOfRange(bytes, nameOffSet + textLength, nameOffSet + textLength + nameLength));
        mMessageTime = new Timestamp(ByteBuffer.wrap(bytes, nameOffSet + textLength + nameLength, 8).getLong());
    }

    public int getId(){
        return Message.CHAT_MESSAGE_ID;
    }

    public String getText() {
        return mText;
    }

	public String getName() {
		return mName;
	}

	public Timestamp getMessageTime() {
		return mMessageTime;
	}

    @Override
    public byte[] serialize() {
        ByteArrayBuffer bab = new ByteArrayBuffer(1024);

		byte[] totalLength = ByteBuffer.allocate(4).putInt(mText.getBytes().length + mName.getBytes().length + 24).array();
        byte[] idSize = ByteBuffer.allocate(4).putInt(Message.CHAT_MESSAGE_ID).array();
        byte[] textSize = ByteBuffer.allocate(4).putInt(mText.getBytes().length).array();
        byte[] nameSize = ByteBuffer.allocate(4).putInt(mName.getBytes().length).array();
        byte[] currentTime = ByteBuffer.allocate(8).putLong(mMessageTime.getTime()).array();

		bab.append(totalLength, 0, totalLength.length);
        bab.append(idSize, 0, idSize.length);
        bab.append(textSize, 0, textSize.length);
        bab.append(mText.getBytes(), 0, mText.getBytes().length);
        bab.append(nameSize, 0, nameSize.length);
        bab.append(mName.getBytes(), 0, mName.getBytes().length);
        bab.append(currentTime, 0, currentTime.length);

        return bab.toByteArray();
    }
}
