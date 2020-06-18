package io.cptpackage.bluetoothchat.security;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.BLUETOOTH;
import static android.Manifest.permission.BLUETOOTH_ADMIN;

public class SecurityConstants {
    public static final String ENCRYPTION_KEY = "r4u7x!A%D*G-KaPdSgVkYp3s5v8y/B?E";
    public static final String[] REQUIRED_NORMAL_PERMISSIONS_LIST = {BLUETOOTH, BLUETOOTH_ADMIN};
    public static final String[] REQUIRED_DANGEROUS_PERMISSIONS_LIST = {ACCESS_FINE_LOCATION};
    public static final int FINE_LOCATION_REQUEST_CODE = 11;

    private SecurityConstants(){}
}
