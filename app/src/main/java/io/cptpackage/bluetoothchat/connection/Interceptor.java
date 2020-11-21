package io.cptpackage.bluetoothchat.connection;

import io.cptpackage.bluetoothchat.db.entities.Message;

import static io.cptpackage.bluetoothchat.security.SecurityConstants.ENCRYPTION_PREFIX;

public class Interceptor {

    private String messageContent;

    public Interceptor(String messageContent) {
        this.messageContent = messageContent;
    }

    public Interceptor(Message interceptedMessage) {
        messageContent = interceptedMessage.getContent();
    }

    public boolean isEncrypted() {
        return messageContent.contains(ENCRYPTION_PREFIX);
    }

    public String getPayload(boolean prefixed) {
        if (isEncrypted()) {
            if (!prefixed) {
                removePrefix(ENCRYPTION_PREFIX);
            }
        } else {
            if (prefixed) {
                addPrefix(ENCRYPTION_PREFIX);
            }
        }
        return messageContent;
    }

    public void addPrefix(String prefix) {
        messageContent = prefix + messageContent;
    }

    public void removePrefix(String prefix) {
        int prefixLength = prefix.length();
        messageContent = messageContent.substring(prefixLength);
    }
}
