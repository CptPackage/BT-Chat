package io.cptpackage.bluetoothchat.lists.viewholders;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import io.cptpackage.bluetoothchat.R;

public class EmoticonViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private EditText chatField;
    private TextView emoticonView;
    private String emoticon;

    public EmoticonViewHolder(@NonNull View itemView, EditText chatField) {
        super(itemView);
        this.chatField = chatField;
        this.emoticonView = itemView.findViewById(R.id.emoticon);
        itemView.setOnClickListener(this);
    }

    public void setEmoticon(String emoticon) {
        emoticonView.setText(emoticon);
        this.emoticon = emoticon;
    }

    @Override
    public void onClick(View view) {
        String updatedText = chatField.getText().toString() + emoticon;
        chatField.setText(updatedText);
        chatField.setSelection(updatedText.length());
    }
}
