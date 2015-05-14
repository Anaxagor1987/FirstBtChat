package ua.lviv.anax.bluetoothapitest.bt.tools.message;

import java.nio.ByteBuffer;

/**
 * @author yurii.ostrovskyi
 */
public class ServiceMessageAccept extends Message{

    @Override
    public int getId() {
        return Message.SERVICE_MESSAGE_ACCEPT;
    }

    @Override
    public byte[] serialize() {
		int totalMessageLength = 8;
        return ByteBuffer.allocate(8).putInt(0, totalMessageLength).putInt(4, Message.SERVICE_MESSAGE_ACCEPT).array();
    }
}
