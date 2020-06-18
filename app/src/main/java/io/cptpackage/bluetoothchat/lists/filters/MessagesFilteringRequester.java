package io.cptpackage.bluetoothchat.lists.filters;

import java.util.List;

import io.cptpackage.bluetoothchat.db.entities.Message;

public interface MessagesFilteringRequester {
    void postFiltering(List<Message> filteredMessages);
}
