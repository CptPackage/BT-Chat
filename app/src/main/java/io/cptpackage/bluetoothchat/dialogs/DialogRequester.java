package io.cptpackage.bluetoothchat.dialogs;

public interface DialogRequester {
    void onDialogSuccess(int dialogId);
    void onDialogFailure(int dialogId);
}
