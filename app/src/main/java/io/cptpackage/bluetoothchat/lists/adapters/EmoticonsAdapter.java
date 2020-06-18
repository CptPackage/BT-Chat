package io.cptpackage.bluetoothchat.lists.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import io.cptpackage.bluetoothchat.R;
import io.cptpackage.bluetoothchat.lists.viewholders.EmoticonViewHolder;

public class EmoticonsAdapter extends RecyclerView.Adapter<EmoticonViewHolder> {
    String[] emoticons = {"😁", "😂", "😃", "😄", "😅", "😆", "😉", "😊", "😋", "😌",
            "😍", "😏", "😒", "😓", "😜", "😝", "😡", "😥", "😭"};

    private EditText chatField;

    public EmoticonsAdapter(EditText chatField) {
        this.chatField = chatField;
    }

    @NonNull
    @Override
    public EmoticonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_col_emoticon, parent, false);
        return new EmoticonViewHolder(view, chatField);
    }

    @Override
    public void onBindViewHolder(@NonNull EmoticonViewHolder holder, int position) {
        holder.setEmoticon(emoticons[position]);
    }

    @Override
    public int getItemCount() {
        return emoticons.length;
    }

}
