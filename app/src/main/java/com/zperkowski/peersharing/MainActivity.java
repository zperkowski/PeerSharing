package com.zperkowski.peersharing;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private RecyclerView recyclerView;
    private static ArrayList<Phone> phones = new ArrayList<>();
    private Intent intent;

    private String manualAddress;

    public static void addPhoneToList(Phone phone) {
        boolean exists = false;
        for (Phone p:
             phones) {
            if (p.getAddress().equals(phone.getAddress())) {
                exists = true;
                break;
            }
        }
        if (!exists) {
            Log.d(TAG, "New phone: " + phone.getAddress());
            phones.add(phone);
        }
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
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        recyclerView.getAdapter().notifyDataSetChanged();
                        Toast.makeText(getApplicationContext(), R.string.refreshed, Toast.LENGTH_SHORT).show();
                    }
                }, 500);

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
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        } else if (id == R.id.manual_address) {
            manualAddressDialog();
        }

        return super.onOptionsItemSelected(item);
    }

    private void manualAddressDialog() {
        Log.d(TAG, "manualAddressDialog()");
        LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
        View promptView = layoutInflater.inflate(R.layout.dialog_ip, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setView(promptView);

        final EditText input = (EditText) promptView.findViewById(R.id.texedit_manual_ip);
        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        manualAddress = input.getText().toString();
                        Log.d(TAG, "manualAddressDialog got: " + manualAddress);
                        if (Pattern.matches("(([2]([0-4][0-9]|[5][0-5])|[0-1]?[0-9]?[0-9])[.]){3}(([2]([0-4][0-9]|[5][0-5])|[0-1]?[0-9]?[0-9]))", manualAddress)) {
                            intent.setAction(NetworkService.ACTION_GETFILES);
                            intent.putExtra(NetworkService.EXTRA_IP, manualAddress);
                            startService(intent);
                        } else
                            Toast.makeText(MainActivity.this, R.string.wrong_address, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }
}
