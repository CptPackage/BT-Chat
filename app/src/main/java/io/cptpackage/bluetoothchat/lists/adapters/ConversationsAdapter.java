package io.cptpackage.bluetoothchat.lists.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import io.cptpackage.bluetoothchat.R;
import io.cptpackage.bluetoothchat.db.entities.Device;
import io.cptpackage.bluetoothchat.db.entities.Message;
import io.cptpackage.bluetoothchat.lists.viewholders.ConversationViewHolder;

public class ConversationsAdapter extends RecyclerView.Adapter<ConversationViewHolder> {
    private List<Device> devices;
    private List<Message> lastMessages;
    private Context context;

    public ConversationsAdapter(Context context, List<Device> devices, List<Message> lastMessages) {
        this.devices = devices;
        this.lastMessages = lastMessages;
        this.context = context;
    }

    @NonNull
    @Override
    public ConversationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_conversation, parent, false);
        return new ConversationViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationViewHolder holder, int position) {
        Device device = devices.get(position);
        if(lastMessages.size() > position){
            Message message = lastMessages.get(position);
            holder.setLastMessage(message.getContent());
        }
        holder.setContactName(device.getName());
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }
}
