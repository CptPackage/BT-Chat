package io.cptpackage.bluetoothchat.lists.adapters;
/**
 *The implementer will have the logic about the currently selected messages
 *
 * */
public interface MessagesSelectionController {
    boolean isSelectionModeOn();
    void enableSelectableMode();
    void disableSelectableMode();
    void scrollMessagesListToPosition(int position);
}
