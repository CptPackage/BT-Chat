package io.cptpackage.bluetoothchat.lists.filters;

import android.widget.Filter;

import java.util.ArrayList;
import java.util.List;

import io.cptpackage.bluetoothchat.db.entities.Message;

public class MessagesFilter extends Filter {

    private List<Message> fullMessageList;
    private MessagesFilteringRequester filteringRequester;

    public MessagesFilter(List<Message> messagesList, MessagesFilteringRequester messagesFilteringRequester) {
        fullMessageList = messagesList;
        filteringRequester = messagesFilteringRequester;
    }

    @Override
    protected FilterResults performFiltering(CharSequence filterKeyword) {
        List<Message> filteredMessages = new ArrayList<>();
        if(filterKeyword != null && filterKeyword.length() > 0){
            String filterPattern = filterKeyword.toString().trim().toLowerCase();
            for(Message message: fullMessageList){
                if(message.getContent().toLowerCase().contains(filterPattern)){
                    filteredMessages.add(message);
                }
            }
        }else{
            filteredMessages.addAll(fullMessageList);
        }
        FilterResults filteringResults = new FilterResults();
        filteringResults.values = filteredMessages;
        return filteringResults;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
        List<Message> filteredMessages = (List<Message>) filterResults.values;
        filteringRequester.postFiltering(filteredMessages);
    }
}
