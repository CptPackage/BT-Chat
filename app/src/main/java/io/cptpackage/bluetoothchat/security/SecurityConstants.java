package io.cptpackage.bluetoothchat.security;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.BLUETOOTH;
import static android.Manifest.permission.BLUETOOTH_ADMIN;

public class SecurityConstants {
    public static final String ENCRYPTION_SECRET_KEY = "BT-Chat-Encrypt-Me";
    public static final String ENCRYPTION_ALGORITHM = "AES";
    public static final String ENCRYPTION_PREFIX = "[BT-ENC]";
    public static final String[] REQUIRED_DANGEROUS_PERMISSIONS_LIST = {ACCESS_FINE_LOCATION};
    public static final int FINE_LOCATION_REQUEST_CODE = 11;

    private SecurityConstants(){}
}
