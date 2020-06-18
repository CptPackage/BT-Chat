package io.cptpackage.bluetoothchat.lists.adapters;

public interface MessagesSelectionController {
    boolean isSelectionModeOn();
    void enableSelectableMode();
    void disableSelectableMode();
    void scrollMessagesListToPosition(int position);
}
