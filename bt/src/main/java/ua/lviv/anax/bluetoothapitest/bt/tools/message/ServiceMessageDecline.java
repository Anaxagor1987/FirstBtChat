package ua.lviv.anax.bluetoothapitest.bt.tools.message;

import java.nio.ByteBuffer;

/**
 * @author yurii.ostrovskyi
 */
public class ServiceMessageDecline extends Message{

    @Override
    public int getId() {
        return Message.SERVICE_MESSAGE_DECLINE;
    }

    @Override
    public byte[] serialize() {
        int totalMessageLength = 8;
		return ByteBuffer.allocate(8).putInt(0, totalMessageLength).putInt(4, Message.SERVICE_MESSAGE_DECLINE).array();
    }
}
