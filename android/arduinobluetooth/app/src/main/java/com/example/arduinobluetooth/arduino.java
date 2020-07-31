package com.example.arduinobluetooth;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class arduino extends AppCompatActivity {
    String intro = "Connecting";
    Button btn1, btn2, btnDis, btn3;
    TextView tv;
    InputStream tmpIn = null;
    String address = null;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    private static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent newint = getIntent();
        address = newint.getStringExtra(MainActivity.EXTRA_ADDRESS);

        setContentView(R.layout.activity_arduino);
        tv = findViewById(R.id.textView2);
        btn1 = findViewById(R.id.button2);
        btn2 = findViewById(R.id.button3);
        btnDis = findViewById(R.id.button4);
        btn3 = findViewById(R.id.button5);

        btnDisable();
        txt();

        new ConnectBT().execute();

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                readSignal("a");
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                readSignal("b");
            }
        });
        btnDis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                Disconnect();
            }
        });
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readSignal("c");
            }
        });

    }
    private void txt(){
        tv.setText(intro);
        tv.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.conProcessing));
    }

    private void readSignal(String x){
        byte[] buffer = new byte[256];  // buffer store for the stream
        int bytes;
        if ( btSocket != null ) {
            try {
                sendSignal(x);
                tmpIn = btSocket.getInputStream();
                DataInputStream mmIn = new DataInputStream(tmpIn);
                bytes = mmIn.read(buffer);
                String readM = new String(buffer, 0, bytes);
                tv.setText(readM);
                tv.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorTextPrimary));
            } catch (IOException e) {
                msg("Error");
            }

        }
    }

    private void sendSignal ( String cmd ) {
        if ( btSocket != null ) {
            try {
                btSocket.getOutputStream().write(cmd.getBytes());
                SystemClock.sleep(500);
            } catch (IOException e) {
                msg("Error");
            }
        }
    }

    private void Disconnect () {
        if ( btSocket!=null ) {
            try {
                btnDisable();
                btSocket.close();
            } catch(IOException e) {
                msg("Error");
            }
        }
        finish();
    }
    private void btnDisable(){
        btn1.setEnabled(false);
        btn2.setEnabled(false);
        btnDis.setEnabled(false);
        btn3.setEnabled(false);
    }
    private void btnEnable(){
        btn1.setEnabled(true);
        btn2.setEnabled(true);
        btnDis.setEnabled(true);
        btn3.setEnabled(true);
    }

    private void msg (String s) {
        Toast.makeText(arduino.this, s, Toast.LENGTH_LONG).show();
    }

    @SuppressLint("StaticFieldLeak")
    private class ConnectBT extends AsyncTask<Void, Void, Void> {
        private boolean ConnectSuccess = true;


        @Override
        protected  void onPreExecute () {
        }

        @Override
        protected Void doInBackground (Void... devices) {
            try {
                if ( btSocket==null || !isBtConnected ) {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();

                }
            } catch (IOException e) {
                ConnectSuccess = false;
            }

            return null;
        }

        @Override
        protected void onPostExecute (Void result) {
            super.onPostExecute(result);

            if (!ConnectSuccess) {
                msg("Connection Failed. Is it a SPP Bluetooth? Try again.");
                finish();
            } else {
                isBtConnected = true;
                btnEnable();
                tv.setText("Connected");
                tv.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.conSuccess));
            }

        }
    }

}
