package io.cptpackage.bluetoothchat.broadcast.callbacks;

import io.cptpackage.bluetoothchat.db.entities.Message;

public interface MessagesDeliveryRequester {
    void notifyIncomingMessage(Message message);

    void notifyOutgoingMessage(Message message);

}
