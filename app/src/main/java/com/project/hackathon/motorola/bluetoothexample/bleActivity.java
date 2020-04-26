package com.project.hackathon.motorola.bluetoothexample;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import java.util.UUID;


public class bleActivity extends AppCompatActivity {

    BluetoothAdapter mBluetoothAdapter;
    BluetoothGatt    mGatt;
    BluetoothGattCharacteristic mBeeChar;
    public static String APS3000data;


    private static final int REQUEST_ENABLE_BT = 1;
    private static final long SCAN_PERIOD = 10000;
    private static final String BLE_TAG = "APS3000_BLE_LOG";
    Context mCtx;

    private boolean mScanning;
    private Handler mHandler;
    private int bleStatus;


    private UUID myUUID;
    private final String APS3000_BLESERVICE_UUID =
            "49535343-fe7d-4ae5-8fa9-9fafd205e455";

    private final String APS3000_BLESERVICE_CHAR =
            "49535343-1e4d-4bd9-ba61-23c647249616";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble);


        mCtx = this;
        mHandler = new Handler();

        // obtaints the ble adapter
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(mCtx.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();


    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        scanLeDevice(true);
    }


    //BLE scan callback
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi,
                                     byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(BLE_TAG, "Device found is:" + device.getAddress());
                            Log.d(BLE_TAG, "Device found is:" + device.getName());

                            if(bleStatus != BluetoothProfile.STATE_CONNECTED) {

                                TextView tv = (TextView) findViewById(R.id.bleScanStatus);


                                // if device is a littleBee gather this device
                                if(new String("EcoTax APS3000").equals(device.getName())) {

                                    tv.setText("APS3000 found! Connecting to it! ");

                                    // gets the device instance and connect to it
                                    mGatt = device.connectGatt(mCtx, true, mGattCallback);
                                }

                            }
                        }
                    });
                }
            };



    private final BluetoothGattCallback mGattCallback =
            new BluetoothGattCallback() {
                @Override
                public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                    super.onConnectionStateChange(gatt, status, newState);

                    bleStatus = newState;

                    if(newState == BluetoothProfile.STATE_DISCONNECTED){
                        Log.d(BLE_TAG, "APS3000 was disconected! restarting the advertisement");
                        scanLeDevice(true);


                    } else if(newState == BluetoothProfile.STATE_CONNECTED) {
                        Log.d(BLE_TAG, "APS3000 is connected! ");
                        TextView tv = (TextView) findViewById(R.id.bleScanStatus);

                        //aftert connection, disable the scanning
                        scanLeDevice(false);

                        // discover services then start the graph activity
                        gatt.discoverServices();

                        Intent it = new Intent(bleActivity.this, MainActivity.class);
                        startActivity(it);

                    }

                }

                @Override
                public void onServicesDiscovered(BluetoothGatt gatt, int status){
                    super.onServicesDiscovered(gatt, status);

                    if (status == BluetoothGatt.GATT_SUCCESS) {

                        Log.d(BLE_TAG, "Discovered database! ");
                        List <BluetoothGattService> services = gatt.getServices();
                        myUUID = UUID.fromString(APS3000_BLESERVICE_UUID);

                        //Iterate through characteristics to find the desired one
                        for(BluetoothGattService service: services) {

                            if(service.getUuid().equals(myUUID)) {
                                myUUID = UUID.fromString(APS3000_BLESERVICE_CHAR);
                                mBeeChar = service.getCharacteristic(myUUID);

                                // Set notification properties:
                                gatt.setCharacteristicNotification(mBeeChar, true);

                                for(BluetoothGattDescriptor desc : mBeeChar.getDescriptors()) {

                                    desc.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                                    gatt.writeDescriptor(desc);
                                }

                                break;
                            }
                        }


                    } else {

                        Log.d(BLE_TAG, "Failed to services discovered, status " +  status);

                    }
                }

                @Override
                public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                    super.onCharacteristicChanged(gatt, characteristic);

                    Log.d(BLE_TAG, "Characteristic update from device " + characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_FLOAT,0));

                    APS3000data = characteristic.getStringValue(0);

                }
            };


    private void scanLeDevice(final boolean enable) {

        TextView tv = (TextView) findViewById(R.id.bleScanStatus);

        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                }
            }, SCAN_PERIOD);

            tv.setText("Finding APS3000 Device...");

            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }

}
