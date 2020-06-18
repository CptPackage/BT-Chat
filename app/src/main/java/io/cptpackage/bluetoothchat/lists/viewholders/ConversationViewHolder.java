package io.cptpackage.bluetoothchat.lists.viewholders;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import io.cptpackage.bluetoothchat.R;
import io.cptpackage.bluetoothchat.activities.ChatActivity;

public class ConversationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private TextView contactName;
    private TextView lastMessage;
    private Context context;

    public ConversationViewHolder(@NonNull View itemView, Context context) {
        super(itemView);
        contactName = itemView.findViewById(R.id.contact_name);
        lastMessage = itemView.findViewById(R.id.last_message);
        this.context = context;
        itemView.setOnClickListener(this);
    }

    public void setContactName(String deviceName) {
        contactName.setText(deviceName);
    }

    public void setLastMessage(String message) {
        lastMessage.setText(message);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(context,ChatActivity.class);
            intent.putExtra(ChatActivity.INTENT_DEVICE_NAME_KEY,contactName.getText().toString());
        context.startActivity(intent);
    }
}
