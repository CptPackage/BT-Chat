package io.cptpackage.bluetoothchat.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.cptpackage.bluetoothchat.R;
import io.cptpackage.bluetoothchat.broadcast.callbacks.MessagesDeliveryRequester;
import io.cptpackage.bluetoothchat.broadcast.receivers.MessagesDeliveryReceiver;
import io.cptpackage.bluetoothchat.connection.BluetoothConnectionsManager;
import io.cptpackage.bluetoothchat.connection.Interceptor;
import io.cptpackage.bluetoothchat.db.entities.Device;
import io.cptpackage.bluetoothchat.db.entities.Message;
import io.cptpackage.bluetoothchat.db.repositories.implementation.DevicesRepositoryImpl;
import io.cptpackage.bluetoothchat.db.repositories.implementation.MessagesRepositoryImpl;
import io.cptpackage.bluetoothchat.db.repositories.interfaces.DevicesRepository;
import io.cptpackage.bluetoothchat.db.repositories.interfaces.MessagesRepository;
import io.cptpackage.bluetoothchat.dialogs.DialogRequester;
import io.cptpackage.bluetoothchat.dialogs.DialogsFactory;
import io.cptpackage.bluetoothchat.lists.adapters.EmoticonsAdapter;
import io.cptpackage.bluetoothchat.lists.adapters.MessagesAdapter;
import io.cptpackage.bluetoothchat.lists.adapters.MessagesSelectionController;
import io.cptpackage.bluetoothchat.security.CryptoAgent;

import static androidx.appcompat.widget.SearchView.OnQueryTextListener;


public class ChatActivity extends LobbyChildActivity implements View.OnClickListener, TextView.OnEditorActionListener,
        MessagesDeliveryRequester, MessagesSelectionController, DialogRequester, OnQueryTextListener {
    public static final String INTENT_DEVICE_NAME_KEY = "DeviceNameKey";
    private RecyclerView emoticonsList;
    private MessagesAdapter messagesAdapter;
    private LinearLayoutManager messagesLayoutManager;
    private EditText chatField;
    private DevicesRepository<Device> devicesRepository;
    private MessagesRepository<Message> messagesRepository;
    private String searchViewContent = "";
    private Device currentDevice;
    private BluetoothConnectionsManager connectionsManager;
    private MessagesDeliveryReceiver messagesDeliveryReceiver;
    private boolean messagesSelectionMode = false;
    private boolean encryptionEnabled = false;

    /*                              Activity Methods                              */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        chatField = findViewById(R.id.chat_input);
        chatField.setOnEditorActionListener(this);
        ImageButton emoticonButton = findViewById(R.id.emoticon_button);
        ImageButton sendButton = findViewById(R.id.send_button);
        emoticonButton.setOnClickListener(this);
        sendButton.setOnClickListener(this);
        connectionsManager = BluetoothConnectionsManager.getInstance(this);
        initData();
        initEmoticonList();
        initMessagesList();
    }

    @Override
    protected void onStart() {
        super.onStart();
        messagesDeliveryReceiver = new MessagesDeliveryReceiver(this, connectionsManager);
        registerReceiver(messagesDeliveryReceiver, messagesDeliveryReceiver.getIntentFilters()[0]);
        refreshMessagesList();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(messagesDeliveryReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_chat, menu);
        MenuItem searchMessagesItem = menu.findItem(R.id.search_messages);
        SearchView searchView = (SearchView) searchMessagesItem.getActionView();
        searchView.setOnQueryTextListener(this);
        searchView.setIconifiedByDefault(true);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (encryptionEnabled) {
            menu.findItem(R.id.toggle_encryption).setIcon(R.drawable.ic_lock_locked);
        } else {
            menu.findItem(R.id.toggle_encryption).setIcon(R.drawable.ic_lock_unlocked);
        }

        SearchView searchView = (SearchView) menu.findItem(R.id.search_messages).getActionView();
        if (!searchView.isIconified() && searchViewContent.length() > 0) {
            searchView.setQuery(searchViewContent, true);
        }

        menu.findItem(R.id.delete_messages).setVisible(messagesSelectionMode);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (messagesSelectionMode) {
                    disableSelectableMode();
                    return true;
                }
                return super.onOptionsItemSelected(item);
            case R.id.toggle_encryption:
                toggleEncryption();
                return true;
            case R.id.delete_all_messages:
                DialogsFactory allMessagesDialogFactory = new DialogsFactory(this, this);
                AlertDialog deleteAllDeviceMessagesConfirmation = allMessagesDialogFactory.
                        createDialog(DialogsFactory.DELETE_ALL_MESSAGES_DEVICE_DIALOG);
                deleteAllDeviceMessagesConfirmation.show();
                return true;
            case R.id.delete_messages:
                if (messagesSelectionMode) {
                    DialogsFactory selectedMessagesDialogFactory = new DialogsFactory(this, this);
                    AlertDialog deleteMessagesConfirmation = selectedMessagesDialogFactory.
                            createDialog(DialogsFactory.DELETE_MESSAGES_DIALOG);
                    deleteMessagesConfirmation.show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*                              Initialization Methods                              */
    private void initData() {
        devicesRepository = DevicesRepositoryImpl.getInstance(this);
        messagesRepository = MessagesRepositoryImpl.getInstance(this);
        Intent intent = getIntent();
        String deviceName = intent.getStringExtra(INTENT_DEVICE_NAME_KEY);
        currentDevice = devicesRepository.getDeviceByName(deviceName);
        encryptionEnabled = currentDevice.getEncrypted();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(deviceName);
        }
    }

    private void initMessagesList() {
        List<Message> messages = messagesRepository.getMessagesByDevice(currentDevice);
        Device personalDevice = devicesRepository.getPersonalDevice();
        RecyclerView messagesList = findViewById(R.id.chat_list);
        messagesList.setHasFixedSize(true);
        Drawable selectedMessageBackground = getResources().getDrawable(R.drawable.shape_selected_message, getTheme());
        messagesAdapter = new MessagesAdapter(messages, personalDevice, this, selectedMessageBackground);
        messagesList.setAdapter(messagesAdapter);
        messagesLayoutManager = new LinearLayoutManager(this);
        messagesLayoutManager.setSmoothScrollbarEnabled(true);
        messagesList.setLayoutManager(messagesLayoutManager);
        scrollToMessagesListBottom();
    }

    private void initEmoticonList() {
        emoticonsList = findViewById(R.id.emoticons_list);
        emoticonsList.setHasFixedSize(true);
        EmoticonsAdapter emoticonsAdapter = new EmoticonsAdapter(chatField);
        emoticonsList.setAdapter(emoticonsAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false);
        emoticonsList.setLayoutManager(layoutManager);
    }

    /*                              Callbacks                              */
    @Override
    public void notifyIncomingMessage(Message message) {
        if (isConnectedDeviceCurrentOne()) {
            messagesAdapter.addMessage(message);
            refreshSearchAndMenu();
            scrollToMessagesListBottom();
        }
    }

    @Override
    public void notifyOutgoingMessage(Message message) {
        if (isConnectedDeviceCurrentOne()) {
            messagesAdapter.addMessage(message);
            refreshSearchAndMenu();
            scrollToMessagesListBottom();
        }
    }

    @Override
    public boolean isSelectionModeOn() {
        return messagesSelectionMode;
    }

    @Override
    public void enableSelectableMode() {
        messagesSelectionMode = true;
        refreshSearchAndMenu();
    }

    @Override
    public void disableSelectableMode() {
        messagesSelectionMode = false;
        messagesAdapter.disableSelection();
        scrollToMessagesListBottom();
        refreshSearchAndMenu();
    }

    @Override
    public void scrollMessagesListToPosition(int position) {
        messagesLayoutManager.scrollToPosition(position);
    }

    @Override
    public void onDialogSuccess(int dialogId) {
        switch (dialogId) {
            case DialogsFactory.DELETE_MESSAGES_DIALOG:
                messagesRepository.deleteMessages(messagesAdapter.getSelectedMessages());
                messagesAdapter.deleteSelectedMessages();
                scrollToMessagesListBottom();
                break;
            case DialogsFactory.DELETE_ALL_MESSAGES_DEVICE_DIALOG:
                messagesRepository.deleteMessagesByDevice(currentDevice);
                messagesAdapter.deleteAllMessages();
                break;
        }
        disableSelectableMode();
    }

    @Override
    public void onDialogFailure(int dialogId) {
    }

    /*                              Listeners                              */

    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
        if (actionId == EditorInfo.IME_ACTION_SEND) {
            sendMessage();
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.emoticon_button:
                TransitionManager.beginDelayedTransition(emoticonsList);
                boolean visible = emoticonsList.getVisibility() == View.VISIBLE;
                emoticonsList.setVisibility(visible ? View.GONE : View.VISIBLE);
                break;
            case R.id.send_button:
                sendMessage();
                break;
        }
    }

    @Override
    public boolean onQueryTextSubmit(String searchKeyword) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String searchKeyword) {
        if (searchKeyword.length() > 0) {
            searchViewContent = searchKeyword;
        }
        messagesAdapter.getFilter().filter(searchKeyword);
        scrollToMessagesListBottom();
        return true;
    }

    /*                              Helper Methods                              */
    private void refreshSearchAndMenu() {
        String tmpContainer = String.valueOf(searchViewContent);
        invalidateOptionsMenu();
        searchViewContent = tmpContainer;
    }

    private void refreshMessagesList() {
        List<Message> freshLoadedMessages = messagesRepository.getMessagesByDevice(currentDevice);
        List<Message> currentMessages = messagesAdapter.getAllMessages();
        if (!currentMessages.containsAll(freshLoadedMessages)) {
            messagesAdapter.setMessages(messagesRepository.getMessagesByDevice(currentDevice));
            scrollToMessagesListBottom();
        }
    }

    private boolean isConnectedDeviceCurrentOne() {
        return currentDevice.getName().equals(connectionsManager.getConnectedDevice().getName());
    }


    private void scrollToMessagesListBottom() {
        scrollMessagesListToPosition(messagesAdapter.getItemCount() - 1);
    }

    private void sendMessage() {
        String text = chatField.getText().toString();
        if (connectionsManager.isConnected()) {
            if (isConnectedDeviceCurrentOne()) {
                if (!text.trim().isEmpty()) {
                    if(encryptionEnabled){
                        CryptoAgent cryptoAgent = new CryptoAgent(text);
                        String encryptedMessage = cryptoAgent.getEncryptedMessage();
                        Interceptor interceptor = new Interceptor(encryptedMessage);
                        text = interceptor.getPayload(true);
                    }
                    connectionsManager.sendData(text);
                    chatField.setText("");
                }
            } else {
                Toast.makeText(this, R.string.toast_not_connected_to_this_device, Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, R.string.toast_not_connected_to_any_device, Toast.LENGTH_LONG).show();
        }

    }

    private void toggleEncryption(){
        encryptionEnabled = !encryptionEnabled;
        refreshSearchAndMenu();
        currentDevice.setEncrypted(encryptionEnabled);
        devicesRepository.updateDevice(currentDevice);
        if(encryptionEnabled){
            Toast.makeText(this,R.string.toast_encryption_enabled,Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this,R.string.toast_encryption_disabled,Toast.LENGTH_LONG).show();
        }
    }

}