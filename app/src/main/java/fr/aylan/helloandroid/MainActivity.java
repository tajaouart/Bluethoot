package fr.aylan.helloandroid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                1);

        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter == null) {
            // create a 'Toast', representing an ephemeral message to be displayed, and show it
            Toast.makeText(this, "Bluetooth not supported on this device!", Toast.LENGTH_LONG).show();
            System.exit(RESULT_OK);
        }

        if (!adapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        }



    }

    public void startConnectActivity(View view){
        Intent intent = new Intent(this, ConnectActivity.class);
        startActivity(intent);

    }
    public void startServerActivity(View view){
        Intent intent = new Intent(this, ServerActivity.class);
        startActivity(intent);

    }




}
