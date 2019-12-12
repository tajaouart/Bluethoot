package fr.aylan.helloandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import static fr.aylan.helloandroid.ServerActivity.SERVICE_PINGPONG_UUID;
import static java.security.AccessController.getContext;


public class ConnectActivity extends AppCompatActivity  implements  AdapterView.OnItemClickListener{

    private ImageView image_view ;
    private Bitmap imageBitmap;
    private byte[] blob ;

    private static final int RESULT_LOAD_IMG = 0;
    private BluetoothAdapter mBluetoothAdapter;
    private ArrayList<BluetoothDevice> mVisibleDevices = new ArrayList<>();
    BluetoothSocket socket;


    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        // this method is called each time there is a new event on which we subscribed
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {

                // get the BluetoothDevice object and its info from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                // add it if not a duplicate
                if (!mVisibleDevices.contains(device)) {
                    mVisibleDevices.add(device);

                    // we will define this method later, create a stub for now
                    updateVisibleDevices();
                }
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);

// get the adapter and keep its reference
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        ((ListView) findViewById(R.id.bluetooth_devices)).setOnItemClickListener(this);

        image_view = findViewById(R.id.imageView);
    }


    private void updateVisibleDevices() {
        ListView view = (ListView) findViewById(R.id.bluetooth_devices);

        // set our adapter with the list of the found devices
        view.setAdapter(new BluetoothArrayAdapter(this, mVisibleDevices));
    }

    public void scanForDevices(View view) {
        mVisibleDevices.clear();

        // start discovering the surrounding bluetooth devices
        mBluetoothAdapter.startDiscovery();
        updateVisibleDevices();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
        BluetoothDevice device = (BluetoothDevice) parent.getItemAtPosition(i);

        try {
             socket = device.createRfcommSocketToServiceRecord(SERVICE_PINGPONG_UUID);
            socket.connect();

            String message = new BufferedReader(new InputStreamReader(socket.getInputStream())).readLine();
            ((TextView) findViewById(R.id.label_client_status)).setText(
                    String.format("Message reçu : %s", message));

            socket.getOutputStream().write(" Hello, Moi  c'est le Client".getBytes());
            //Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            //photoPickerIntent.setType("image/*");
            //startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class BluetoothArrayAdapter extends ArrayAdapter<BluetoothDevice> {
        public BluetoothArrayAdapter(Context context, ArrayList<BluetoothDevice> devices) {
            super(context, 0, devices);
        }



        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView label;
            TextView secondLabel;

            if(convertView == null) {
                // create a new view by inflating the Android's simple_list_item_2 layout
                convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
            }

            // primary and secondary labels in the item view
            label = (TextView) convertView.findViewById(android.R.id.text1);
            secondLabel = (TextView) convertView.findViewById(android.R.id.text2);

            BluetoothDevice device = getItem(position);

            label.setText(device.getName());
            secondLabel.setText(device.getAddress());

            return convertView;
        }
    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);


        if (resultCode == RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                imageBitmap = BitmapFactory.decodeStream(imageStream);
                image_view.setImageBitmap(imageBitmap);
                //blob = getBlob();
                socket.getOutputStream().write(" Hello, Moi  c'est le Client".getBytes());
            } catch (Exception e) {
                e.printStackTrace();
            }

        }else {
        }
    }

    private byte[] getBlob() {

        ByteArrayOutputStream boas = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 10, boas);

        byte[] byteArray = boas.toByteArray();

        return byteArray;
    }

}

