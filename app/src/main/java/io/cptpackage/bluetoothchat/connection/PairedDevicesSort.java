package io.cptpackage.bluetoothchat.connection;

import android.bluetooth.BluetoothDevice;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.cptpackage.bluetoothchat.db.entities.Device;

public class PairedDevicesSort {

    List<Device> contactsList;
    List<BluetoothDevice> devicesList;

    public PairedDevicesSort(List<BluetoothDevice> toSortList, List<Device> sortBasedList) {
        devicesList = toSortList;
        contactsList = sortBasedList;
    }

    public List<BluetoothDevice> getSortedDevicesList(){
        Collections.sort(devicesList,new DevicesComparator());
        return devicesList;
    }

    class DevicesComparator implements  Comparator<BluetoothDevice>{

        @Override
        public int compare(BluetoothDevice t0, BluetoothDevice t1) {
            for (Device contact: contactsList){
                if(contact.getAddress().equals(t0.getAddress())){
                    return -1;
                }else if(contact.getAddress().equals(t1.getAddress())){
                    return 1;
                }
            }
            return 0;
        }
    }
}
