package io.cptpackage.bluetoothchat.broadcast.receivers;

import android.content.IntentFilter;

public interface FiltersContainingReceiver {
    IntentFilter[] getIntentFilters();
}
