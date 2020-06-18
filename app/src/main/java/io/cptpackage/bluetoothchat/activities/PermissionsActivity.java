package io.cptpackage.bluetoothchat.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import io.cptpackage.bluetoothchat.R;
import io.cptpackage.bluetoothchat.connection.BluetoothUtils;
import io.cptpackage.bluetoothchat.security.PermissionsAgent;
import io.cptpackage.bluetoothchat.security.SecurityConstants;

public class PermissionsActivity extends AppCompatActivity implements View.OnClickListener {
    private PermissionsAgent permissionsAgent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permissions);
        Button requestPermissionsBtn = (Button) this.findViewById(R.id.request_permissions_btn);
        requestPermissionsBtn.setOnClickListener(this);
        permissionsAgent = new PermissionsAgent(this);
        if (permissionsAgent.requiredPermissionsGranted()) {
            finish();
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
            }
        }
    }

    @Override
    public void onClick(View view) {
        permissionsAgent.requestRequiredPermissions();
    }
}
