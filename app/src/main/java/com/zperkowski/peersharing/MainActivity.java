package com.zperkowski.peersharing;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private RecyclerView recyclerView;
    private static ArrayList<Phone> phones = new ArrayList<>();
    private Intent intent;

    private String manualAddress;

    public static void addPhoneToList(Phone phone) {
        Log.d(TAG, "New phone: " + phone.getAddress());
        phones.add(phone);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        intent = new Intent(getApplicationContext(), NetworkService.class);

        recyclerView = (RecyclerView) findViewById(R.id.phone_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new SimpleCardAdapter(phones, getApplicationContext()));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.setAction(NetworkService.ACTION_REFRESH);
                startService(intent);
                recyclerView.getAdapter().notifyDataSetChanged();
            }
        });


        intent.setAction(NetworkService.ACTION_STARTSERVER);
        startService(intent);
    }

    @Override
    protected void onDestroy() {
        Intent intent = new Intent(getApplicationContext(), NetworkService.class);
        intent.setAction(NetworkService.ACTION_STOPSERVER);
        startService(intent);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.manual_address) {
            manualAddressDialog();
        }

        return super.onOptionsItemSelected(item);
    }

    private void manualAddressDialog() {
        Log.d(TAG, "manualAddressDialog()");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Input IP address");

        final EditText input = new EditText(this);

        input.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
        builder.setView(input);


        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                manualAddress = input.getText().toString();
                Log.d(TAG, "manualAddressDialog got: " + manualAddress);
                intent.setAction(NetworkService.ACTION_GETFILES);
                intent.putExtra(NetworkService.EXTRA_IP, manualAddress);
                startService(intent);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
}
