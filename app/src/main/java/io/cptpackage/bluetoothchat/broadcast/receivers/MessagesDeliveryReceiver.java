package io.cptpackage.bluetoothchat.broadcast.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import io.cptpackage.bluetoothchat.broadcast.callbacks.MessagesDeliveryRequester;
import io.cptpackage.bluetoothchat.connection.BluetoothConnectionsManager;
import io.cptpackage.bluetoothchat.db.entities.Device;
import io.cptpackage.bluetoothchat.db.entities.Message;
import io.cptpackage.bluetoothchat.db.repositories.implementation.DevicesRepositoryImpl;
import io.cptpackage.bluetoothchat.db.repositories.implementation.MessagesRepositoryImpl;
import io.cptpackage.bluetoothchat.db.repositories.interfaces.DevicesRepository;
import io.cptpackage.bluetoothchat.db.repositories.interfaces.MessagesRepository;

import static io.cptpackage.bluetoothchat.broadcast.BroadcastConstants.INCOMING_MESSAGE;
import static io.cptpackage.bluetoothchat.broadcast.BroadcastConstants.KEY_INCOMING_MESSAGE;
import static io.cptpackage.bluetoothchat.broadcast.BroadcastConstants.KEY_MESSAGE_TYPE;
import static io.cptpackage.bluetoothchat.broadcast.BroadcastConstants.KEY_OUTGOING_MESSAGE;
import static io.cptpackage.bluetoothchat.broadcast.BroadcastConstants.MESSAGE_DELIVERY_BROADCAST;
import static io.cptpackage.bluetoothchat.broadcast.BroadcastConstants.OUTGOING_MESSAGE;

public class MessagesDeliveryReceiver extends BroadcastReceiver implements FiltersContainingReceiver {
    private static final String TAG = "MessagesReceiver";
    private MessagesDeliveryRequester messagesDeliveryRequester;
    private Device personalDevice;
    private BluetoothConnectionsManager manager;
    private DevicesRepository<Device> devicesRepository;
    private MessagesRepository<Message> messagesRepository;

    public MessagesDeliveryReceiver() {
    }

    public MessagesDeliveryReceiver(MessagesDeliveryRequester requester,
                                    BluetoothConnectionsManager connectionsManager) {
        messagesDeliveryRequester = requester;
        manager = connectionsManager;
    }

    /* Test the add message if exists as it sometimes adds duplicates, and sometimes doesn't refresh the */
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (MESSAGE_DELIVERY_BROADCAST.equals(action)) {
            initRepos(context);
            int messageType = intent.getIntExtra(KEY_MESSAGE_TYPE, -1);
            switch (messageType) {
                case INCOMING_MESSAGE:
                    Device sender = new Device(manager.getConnectedDevice());
                    if (devicesRepository.exist(sender)) {
                        sender = devicesRepository.getDeviceByName(sender.getName());
                    }
                    String incomingMessageContent = intent.getStringExtra(KEY_INCOMING_MESSAGE);
                    Message incomingMessage = new Message(sender, personalDevice, incomingMessageContent);
                    if (messagesDeliveryRequester == null && !messagesRepository.exist(incomingMessage)) {
                        messagesRepository.addMessage(incomingMessage);
                    }
                    if (messagesDeliveryRequester != null) {
                        messagesDeliveryRequester.notifyIncomingMessage(incomingMessage);
                    }
                    break;
                case OUTGOING_MESSAGE:
                    String outgoingMessageContent = intent.getStringExtra(KEY_OUTGOING_MESSAGE);
                    Device receiver = new Device(manager.getConnectedDevice());
                    receiver = devicesRepository.getDeviceByName(receiver.getName());
                    Message outgoingMessage = new Message(personalDevice, receiver, outgoingMessageContent);
                    if (messagesDeliveryRequester == null && !messagesRepository.exist(outgoingMessage)) {
                        messagesRepository.addMessage(outgoingMessage);
                    }
                    if (messagesDeliveryRequester != null) {
                        messagesDeliveryRequester.notifyOutgoingMessage(outgoingMessage);
                    }
                    break;
            }
        }
    }

    private void initRepos(Context context) {
        if (devicesRepository == null) {
            devicesRepository = DevicesRepositoryImpl.getInstance(context);
        }

        if (messagesRepository == null) {
            messagesRepository = MessagesRepositoryImpl.getInstance(context);
        }

        if (personalDevice == null) {
            personalDevice = devicesRepository.getPersonalDevice();
        }

        if (manager == null) {
            manager = BluetoothConnectionsManager.getInstance(context);
        }
    }

    @Override
    public IntentFilter[] getIntentFilters() {
        return new IntentFilter[]{new IntentFilter(MESSAGE_DELIVERY_BROADCAST)};
    }
}
