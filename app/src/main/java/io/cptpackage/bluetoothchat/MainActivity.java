package io.cptpackage.bluetoothchat;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import io.cptpackage.bluetoothchat.activities.LobbyActivity;
import io.cptpackage.bluetoothchat.activities.PermissionsActivity;
import io.cptpackage.bluetoothchat.connection.BluetoothUtils;
import io.cptpackage.bluetoothchat.security.PermissionsAgent;
import io.cptpackage.bluetoothchat.security.SecurityConstants;


public class MainActivity extends AppCompatActivity {
    private PermissionsAgent permissionsAgent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        permissionsAgent = new PermissionsAgent(this);
        if (permissionsAgent.requiredPermissionsGranted()) {
            BluetoothUtils.enableBluetoothIfDisabled();
            Intent intent = new Intent(this, LobbyActivity.class);
            startActivity(intent);
        } else {
            permissionsAgent.requestRequiredPermissions();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SecurityConstants.FINE_LOCATION_REQUEST_CODE) {
            if (permissionsAgent.checkIfGranted(grantResults)) {
                BluetoothUtils.enableBluetoothIfDisabled();
                Intent lobbyIntent = new Intent(this, LobbyActivity.class);
                startActivity(lobbyIntent);
            } else {
                Intent permissionsIntent = new Intent(this, PermissionsActivity.class);
                startActivity(permissionsIntent);
            }
        }
    }

}