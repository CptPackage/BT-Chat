package io.cptpackage.bluetoothchat.lists.viewholders;

import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import io.cptpackage.bluetoothchat.R;
import io.cptpackage.bluetoothchat.db.entities.Device;
import io.cptpackage.bluetoothchat.db.entities.Message;
import io.cptpackage.bluetoothchat.db.utils.DateAndTimeUtils;
import io.cptpackage.bluetoothchat.lists.adapters.MessagesAdapter;

public class MessageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
    private static final String TAG = "MessageViewHolder";
    private TextView messageContent;
    private TextView time;
    private RelativeLayout container;
    private int personalDeviceId;
    private MessagesAdapter adapter;
    private Drawable selectedBackground;
    private Message message;
    private DateAndTimeUtils timeUtils;

    public MessageViewHolder(@NonNull View itemView, Device personalDevice, MessagesAdapter adapter, Drawable selectedBackground) {
        super(itemView);
        messageContent = itemView.findViewById(R.id.chat_message_text);
        time = itemView.findViewById(R.id.chat_message_date_and_time);
        container = itemView.findViewById(R.id.chat_message_container);
        personalDeviceId = personalDevice.getId();
        this.adapter = adapter;
        this.selectedBackground = selectedBackground;
        timeUtils = DateAndTimeUtils.getInstance();
        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
    }

    public void setMessage(Message message) {
        this.message = message;
        setMessageAlignment(message);
        setStyleIfSelectable();
        if (!timeUtils.parseDateToStringCompacted(timeUtils.now()).equals(message.getShortDateString())) {
            time.setText(String.format("%s %s", message.getShortDateString(), message.getShortTimeString()));
        } else {
            time.setText(message.getShortTimeString());
        }
        messageContent.setText(message.getContent());
    }

    public void setMessageAlignment(Message message) {
        if (message.getSender().getId() == personalDeviceId) {
            container.setGravity(Gravity.END);
        } else {
            container.setGravity(Gravity.START);
        }
    }

    public void setStyleIfSelectable() {
        if (adapter.isSelected(message)) {
            container.setBackground(selectedBackground);
        } else {
            container.setBackground(null);
        }
    }

    @Override
    public void onClick(View view) {
        if (adapter.canSelect()) {
            adapter.selectMessage(message);
        }
    }

    @Override
    public boolean onLongClick(View view) {
        if (!adapter.canSelect()) {
            adapter.enableSelection();
            adapter.selectMessage(message);
            return true;
        }
        return false;
    }
}
