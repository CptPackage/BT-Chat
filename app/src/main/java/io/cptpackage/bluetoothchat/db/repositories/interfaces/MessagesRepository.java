package io.cptpackage.bluetoothchat.db.repositories.interfaces;

import java.util.List;

import io.cptpackage.bluetoothchat.db.entities.Device;
import io.cptpackage.bluetoothchat.db.entities.Entity;
import io.cptpackage.bluetoothchat.db.entities.Message;

public interface MessagesRepository<T extends Entity> {
    boolean addMessage(T message);

    List<Message> getMessagesByDevice(Device device);

    boolean exist(Message message);

    void deleteMessages(List<Message> message);

    void deleteMessagesByDevice(Device device);

    void deleteAllMessages();

    List<Message> getLatestMessagesByDevices(List<Device> devices);
}
