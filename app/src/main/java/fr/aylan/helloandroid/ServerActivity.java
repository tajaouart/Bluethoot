package fr.aylan.helloandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.UUID;

public class ServerActivity extends AppCompatActivity {
    private BluetoothAdapter mBluetoothAdapter;
    private boolean waitThreadRunning = false;

    private BluetoothServerSocket mServerSocket;

    private Runnable mListenThread = new Runnable() {
        @Override
        public void run() {
            try {
                updateStatusText("Waiting for a client connection...");

                // wait for a client...
                BluetoothSocket clientSocket = mServerSocket.accept();
                updateStatusText(String.format("Connected to %s (%s)",
                        clientSocket.getRemoteDevice().getName(),
                        clientSocket.getRemoteDevice().getAddress()));

                // send a simple message and wait for a response
                clientSocket.getOutputStream().write("Hello Bluetooth!\n".getBytes());
                String message = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())).readLine();

                updateStatusText(String.format("Received message : %s", message));

                // the client socket should be closed now
                clientSocket.close();
                mServerSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
                updateStatusText("Error when waiting for connection!");
            }
            waitThreadRunning = false;
        }
    };


    public static final String SERVICE_PINGPONG_NAME = "BLUETOOTH PINGPONG M2 DL";
    // we will derivate the UUID from the name of our service
    public static final UUID SERVICE_PINGPONG_UUID =
            UUID.nameUUIDFromBytes(SERVICE_PINGPONG_NAME.getBytes());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
        startActivity(discoverableIntent);



    }



    public void waitForClient(View view) {
        synchronized (this) {
            if (!waitThreadRunning) {
                try {
                    mServerSocket = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(
                            SERVICE_PINGPONG_NAME,
                            SERVICE_PINGPONG_UUID);

                    // the server thread, defined later, will be "mListenThread"
                    new Thread(mListenThread).start();
                    waitThreadRunning = true;

                } catch (IOException e) {
                    e.printStackTrace();
                    ((TextView) findViewById(R.id.label_server_status)).setText("Error!");
                }
            }
        }
    }

    private void updateStatusText(final String status) {
        final TextView label = (TextView) findViewById(R.id.label_server_status);
        label.post(new Runnable() {
            @Override
            public void run() {
                label.setText(status);
            }
        });
    }





}
