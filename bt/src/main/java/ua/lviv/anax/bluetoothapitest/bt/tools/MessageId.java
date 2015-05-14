package ua.lviv.anax.bluetoothapitest.bt.tools;

/**
 * @author yurii.ostrovskyi
 */
public interface MessageId {
    int DeviceFound = 1;
    int StateChanged = 2;
    int PermissionMessage = 3;
    int ChatMessageSent = 4;
    int ChatMessageReceived = 5;
    int ProfileMessageSent = 6;
    int ProfileMessageReceived = 7;
}
