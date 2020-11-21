package io.cptpackage.bluetoothchat.lists.adapters;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import io.cptpackage.bluetoothchat.R;
import io.cptpackage.bluetoothchat.db.entities.Device;
import io.cptpackage.bluetoothchat.db.entities.Message;
import io.cptpackage.bluetoothchat.lists.filters.MessagesFilter;
import io.cptpackage.bluetoothchat.lists.filters.MessagesFilteringRequester;
import io.cptpackage.bluetoothchat.lists.viewholders.MessageViewHolder;

/**
 *Chat Messages List Controller
 *
 * */
public class MessagesAdapter extends RecyclerView.Adapter<MessageViewHolder> implements Filterable, MessagesFilteringRequester {
    private List<Message> displayedMessagesList;
    private List<Message> allMessagesList;
    private List<Message> selectedMessages;
    private Device personalDevice;
    private MessagesSelectionController messagesSelectionController;
    private Drawable selectedMessageBackground;

    public MessagesAdapter(List<Message> messagesList, Device personalDevice, MessagesSelectionController selectionController,
                           Drawable selectedMessageBackground) {
        allMessagesList = messagesList;
        displayedMessagesList = new ArrayList<>();
        displayedMessagesList.addAll(allMessagesList);
        this.selectedMessages = new ArrayList<>();
        this.personalDevice = personalDevice;
        this.messagesSelectionController = selectionController;
        this.selectedMessageBackground = selectedMessageBackground;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_message, parent, false);
        return new MessageViewHolder(view, personalDevice, this, selectedMessageBackground);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        holder.setMessage(displayedMessagesList.get(position));
    }

    @Override
    public int getItemCount() {
        return displayedMessagesList.size();
    }

    public void selectMessage(Message message) {
        if (messagesSelectionController.isSelectionModeOn()) {
            if (selectedMessages.contains(message)) {
                selectedMessages.remove(message);
                if (selectedMessages.size() == 0) {
                    messagesSelectionController.disableSelectableMode();
                }
            } else {
                selectedMessages.add(message);
            }
            int messagePosition = displayedMessagesList.indexOf(message);
            notifyItemChanged(messagePosition);
            messagesSelectionController.scrollMessagesListToPosition(messagePosition);
        }
    }

    public void disableSelection() {
        selectedMessages.clear();
        notifyDataSetChanged();
    }

    public void enableSelection() {
        messagesSelectionController.enableSelectableMode();
    }

    public boolean canSelect() {
        return messagesSelectionController.isSelectionModeOn();
    }

    public boolean isSelected(Message message) {
        return selectedMessages.contains(message);
    }

    public List<Message> getSelectedMessages() {
        return selectedMessages;
    }

    public void addMessage(Message message) {
        if (displayedMessagesList.size() == allMessagesList.size()) {
            displayedMessagesList.add(message);
            notifyItemInserted(displayedMessagesList.size() - 1);
        }
        allMessagesList.add(message);
    }

    public void deleteSelectedMessages() {
        allMessagesList.removeAll(selectedMessages);
        displayedMessagesList.removeAll(selectedMessages);
        notifyDataSetChanged();
    }

    public void deleteAllMessages() {
        allMessagesList.clear();
        displayedMessagesList.clear();
        notifyDataSetChanged();
    }

    public List<Message> getAllMessages() {
        return allMessagesList;
    }

    public void setMessages(List<Message> messages) {
        allMessagesList.clear();
        allMessagesList.addAll(messages);
        displayedMessagesList.clear();
        displayedMessagesList.addAll(messages);
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return new MessagesFilter(allMessagesList, this);
    }

    @Override
    public void postFiltering(List<Message> filteredMessages) {
        displayedMessagesList.clear();
        displayedMessagesList.addAll(filteredMessages);
        notifyDataSetChanged();
    }
}
