package io.cptpackage.bluetoothchat.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import io.cptpackage.bluetoothchat.R;

import static android.app.AlertDialog.Builder;

public class DialogsFactory implements DialogInterface.OnClickListener {
    public static final int DELETE_MESSAGES_DIALOG = 0;
    public static final int DELETE_ALL_MESSAGES_DEVICE_DIALOG = 1;
    public static final int DELETE_ALL_MESSAGES_GLOBAL_DIALOG = 2;
    private static final String TAG = "DialogsFactory";
    private Builder builder;
    private DialogRequester dialogRequester;
    private int dialogId;

    public DialogsFactory(Context context, DialogRequester dialogRequester) {
        this.dialogRequester = dialogRequester;
        builder = new Builder(context, R.style.DialogTheme);
    }

    public AlertDialog createDialog(int dialogId) {
        this.dialogId = dialogId;
        builder.setPositiveButton(R.string.dialog_success, this);
        builder.setNegativeButton(R.string.dialog_failure, this);
        AlertDialog dialog = builder.create();
        dialog.setTitle(getTitleStringId());
        return dialog;
    }

    public int getTitleStringId() {
        switch (dialogId) {
            case DELETE_MESSAGES_DIALOG:
                return R.string.dialog_delete_messages_title;
            case DELETE_ALL_MESSAGES_DEVICE_DIALOG:
                return R.string.dialog_delete_all_messages_device_title;
            case DELETE_ALL_MESSAGES_GLOBAL_DIALOG:
                return R.string.dialog_delete_all_messages_global_title;
            default:
                return 0;
        }
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int id) {
        switch (id) {
            case DialogInterface.BUTTON_POSITIVE:
                dialogRequester.onDialogSuccess(dialogId);
                break;
            case DialogInterface.BUTTON_NEGATIVE:
                dialogRequester.onDialogFailure(dialogId);
                break;
        }
    }

}
