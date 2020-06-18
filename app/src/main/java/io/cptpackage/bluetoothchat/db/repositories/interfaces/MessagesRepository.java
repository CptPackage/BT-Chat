package io.cptpackage.bluetoothchat.db.repositories.interfaces;

import java.util.List;

import io.cptpackage.bluetoothchat.db.entities.Device;
import io.cptpackage.bluetoothchat.db.entities.Entity;
import io.cptpackage.bluetoothchat.db.entities.Message;

public interface MessagesRepository<T extends Entity> {
    boolean addMessage(T message);

    List<Message> getMessagesByDevice(Device device);

    List<Message> getMessagesContaining(String keyword);

    boolean deleteMessage(Message message);

    boolean exist(Message message);

    boolean deleteMessages(List<Message> message);

    boolean deleteMessagesByDevice(Device device);

    boolean deleteAllMessages();

    List<Message> getLatestMessagesByDevices(List<Device> devices);
}
