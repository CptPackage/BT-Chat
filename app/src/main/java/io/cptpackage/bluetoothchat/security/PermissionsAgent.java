package io.cptpackage.bluetoothchat.security;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionsAgent {
    private Context context;

    public PermissionsAgent(Context context) {
        this.context = context;
    }

    public boolean requiredPermissionsGranted() {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            return true;
        }

        if (ContextCompat.checkSelfPermission(context, SecurityConstants.REQUIRED_DANGEROUS_PERMISSIONS_LIST[0])
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    public void requestRequiredPermissions() {
        ActivityCompat.requestPermissions((Activity) context, SecurityConstants.REQUIRED_DANGEROUS_PERMISSIONS_LIST
                , SecurityConstants.FINE_LOCATION_REQUEST_CODE);
    }

    public boolean checkIfGranted(int[] grantedResults){
        return grantedResults.length > 0
                && grantedResults[0] == PackageManager.PERMISSION_GRANTED;
    }


}
