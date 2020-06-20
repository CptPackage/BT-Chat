package io.cptpackage.bluetoothchat.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.cptpackage.bluetoothchat.R;
import io.cptpackage.bluetoothchat.broadcast.callbacks.DeviceConnectionChangeRequester;
import io.cptpackage.bluetoothchat.broadcast.callbacks.MessagesDeliveryRequester;
import io.cptpackage.bluetoothchat.broadcast.receivers.DeviceConnectionChangeReceiver;
import io.cptpackage.bluetoothchat.broadcast.receivers.MessagesDeliveryReceiver;
import io.cptpackage.bluetoothchat.connection.BluetoothConnectionsManager;
import io.cptpackage.bluetoothchat.db.entities.Device;
import io.cptpackage.bluetoothchat.db.entities.Message;
import io.cptpackage.bluetoothchat.db.repositories.implementation.DevicesRepositoryImpl;
import io.cptpackage.bluetoothchat.db.repositories.implementation.MessagesRepositoryImpl;
import io.cptpackage.bluetoothchat.db.repositories.interfaces.DevicesRepository;
import io.cptpackage.bluetoothchat.db.repositories.interfaces.MessagesRepository;
import io.cptpackage.bluetoothchat.dialogs.DialogRequester;
import io.cptpackage.bluetoothchat.dialogs.DialogsFactory;
import io.cptpackage.bluetoothchat.lists.adapters.ConversationsAdapter;

public class LobbyActivity extends AppCompatActivity implements View.OnClickListener,
        MessagesDeliveryRequester, DeviceConnectionChangeRequester, DialogRequester {
    private static final int LAUNCH_CONNECTION_ACTIVITY = 0;
    private MessagesDeliveryReceiver messagesDeliveryReceiver;
    private DeviceConnectionChangeReceiver deviceConnectionChangeReceiver;
    private BluetoothConnectionsManager connectionsManager;
    private DevicesRepository<Device> devicesRepository;
    private MessagesRepository<Message> messagesRepository;
    private ConversationsAdapter conversationsAdapter;
    private List<Device> devicesList;
    private List<Message> lastMessages;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        devicesRepository = DevicesRepositoryImpl.getInstance(this);
        messagesRepository = MessagesRepositoryImpl.getInstance(this);
        connectionsManager = BluetoothConnectionsManager.getInstance(this);
        if (!connectionsManager.isConnected()) {
            connectionsManager.start();
        }
        FloatingActionButton actionButton = (FloatingActionButton) findViewById(R.id.new_conversation_button);
        actionButton.setOnClickListener(this);
        initConversationsList();
    }

    @Override
    protected void onStart() {
        super.onStart();
        messagesDeliveryReceiver = new MessagesDeliveryReceiver(this, connectionsManager);
        registerReceiver(messagesDeliveryReceiver, messagesDeliveryReceiver.getIntentFilters()[0]);
        deviceConnectionChangeReceiver = new DeviceConnectionChangeReceiver(this);
        registerReceiver(deviceConnectionChangeReceiver, deviceConnectionChangeReceiver.getIntentFilters()[0]);
        refreshContactsList();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(messagesDeliveryReceiver);
        unregisterReceiver(deviceConnectionChangeReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_lobby, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.delete_all_messages_global) {
            DialogsFactory dialogsFactory = new DialogsFactory(this, this);
            AlertDialog dialog = dialogsFactory.createDialog(DialogsFactory.DELETE_ALL_MESSAGES_GLOBAL_DIALOG);
            dialog.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == LAUNCH_CONNECTION_ACTIVITY) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                String connectedName = data.getStringExtra(ConnectionActivity.INTENT_CONNECTED_DEVICE_NAME_KEY);
                Intent launchIntent = new Intent(this, ChatActivity.class);
                launchIntent.putExtra(ChatActivity.INTENT_DEVICE_NAME_KEY, connectedName);
                startActivity(launchIntent);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.new_conversation_button) {
            Intent connectionIntent = new Intent(this, ConnectionActivity.class);
            startActivityForResult(connectionIntent, LAUNCH_CONNECTION_ACTIVITY);
        }
    }

    @Override
    public void notifyIncomingMessage(Message message) {
        Toast.makeText(this, String.format("[%s]: %s", message.getSender().getName(), message.getContent()),
                Toast.LENGTH_LONG).show();
       updateLastContactMessage(message);
    }

    @Override
    public void notifyOutgoingMessage(Message message) {
        updateLastContactMessage(message);
    }

    private void updateLastContactMessage(Message message){
        int senderId = message.getSender().getId();
        int receiverId = message.getReceiver().getId();
        for (Device device : devicesList) {
            if (device.getId() == senderId || device.getId() == receiverId) {
                int devicePosition = devicesList.indexOf(device);
                lastMessages.set(devicePosition, message);
                conversationsAdapter.notifyItemChanged(devicePosition);
            }
        }
    }


    private void refreshContactsList() {
        devicesList.clear();
        devicesList.addAll(devicesRepository.getAllDevices());
        lastMessages.clear();
        lastMessages.addAll(messagesRepository.getLatestMessagesByDevices(devicesList));
        conversationsAdapter.notifyDataSetChanged();
    }

    private void initConversationsList() {
        RecyclerView conversationsList = findViewById(R.id.conversations_list);
        devicesList = devicesRepository.getAllDevices();
        lastMessages = messagesRepository.getLatestMessagesByDevices(devicesList);
        conversationsAdapter = new ConversationsAdapter(this, devicesList, lastMessages);
        conversationsList.setAdapter(conversationsAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        conversationsList.setLayoutManager(layoutManager);
    }

    @Override
    public void notifyDeviceDisconnected() {

    }

    @Override
    public void notifyDeviceConnecting() {

    }

    @Override
    public void notifyDeviceConnected(String deviceName, String deviceAddress) {
        Intent launchIntent = new Intent(this, ChatActivity.class);
        launchIntent.putExtra(ChatActivity.INTENT_DEVICE_NAME_KEY, deviceName);
        startActivity(launchIntent);
        refreshContactsList();
    }

    @Override
    public void onDialogSuccess(int dialogId) {
        if (dialogId == DialogsFactory.DELETE_ALL_MESSAGES_GLOBAL_DIALOG) {
            MessagesRepository<Message> messagesRepository = MessagesRepositoryImpl.getInstance(this);
            messagesRepository.deleteAllMessages();
            refreshContactsList();
        }
    }

    @Override
    public void onDialogFailure(int dialogId) {

    }
}
