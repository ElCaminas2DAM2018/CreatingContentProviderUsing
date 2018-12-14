package org.ieselcaminas.pmdm.creatingcontentproviderusing;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_CALL_LOG = 1;
    TextView textResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textResult = (TextView) findViewById(R.id.textResult);

        Button buttonSelect = (Button) findViewById(R.id.buttonSelect);
        buttonSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] projection = new String[] {"_id", "user", "password", "email"};
                String uriStr = "content://net.victoralonso.unit7.creatingcontentprovider/Users";
                Uri usersUri = Uri.parse(uriStr);
                ContentResolver cr = getContentResolver();
                Cursor cur = cr.query(usersUri, projection, null, null, null);
                if (cur.moveToFirst()) {
                    String user, password, email;
                    int colUser = cur.getColumnIndex("user");
                    int colPassword = cur.getColumnIndex("password");
                    int colEmail = cur.getColumnIndex("email");
                    textResult.setText("");
                    do {
                        user = cur.getString(colUser);
                        password = cur.getString(colPassword);
                        email = cur.getString(colEmail);

                        textResult.append(user + " - " + password + " - " + email + "\n");
                    } while (cur.moveToNext());
                }
            }
        });
        Button buttonInsert = (Button) findViewById(R.id.buttonInsert);
        buttonInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentValues values = new ContentValues();
                values.put("user","userN");
                values.put("password","passwordN");
                values.put("email", "new@ieselcaminas.org");
                String uriStr = "content://net.victoralonso.unit7.creatingcontentprovider/Users";
                Uri usersUri = Uri.parse(uriStr);
                ContentResolver cr = getContentResolver();
                cr.insert(usersUri, values);
            }
        });
        Button buttonDelete = (Button) findViewById(R.id.buttonDelete);
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uriStr = "content://net.victoralonso.unit7.creatingcontentprovider/Users";
                Uri usersUri = Uri.parse(uriStr);
                ContentResolver cr = getContentResolver();
                cr.delete(usersUri, "user = 'userN'", null);
            }
        });
        Button buttonCalls = (Button) findViewById(R.id.buttonCalls);
        buttonCalls.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.READ_CALL_LOG)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.READ_CALL_LOG},
                            MY_PERMISSIONS_REQUEST_CALL_LOG);
                } else {
                    listCallLog();
                }
            }
        });
    }

    private void listCallLog() {
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_CALL_LOG)
                == PackageManager.PERMISSION_GRANTED) {
            String[] projection = new String[]{CallLog.Calls.TYPE, CallLog.Calls.NUMBER};
            Uri callsUri = CallLog.Calls.CONTENT_URI;
            ContentResolver cr = getContentResolver();
            Cursor cur = cr.query(callsUri, projection, null, null, null);
            if (cur.moveToFirst()) {
                int type;
                String callType = "";
                String phone = "";

                int colCallType = cur.getColumnIndex(CallLog.Calls.TYPE);
                int colPhone = cur.getColumnIndex(CallLog.Calls.NUMBER);

                textResult.setText("");
                do {
                    type = cur.getInt(colCallType);
                    phone = cur.getString(colPhone);

                    if (type == CallLog.Calls.INCOMING_TYPE)
                        callType = "INCOMING";
                    else if (type == CallLog.Calls.OUTGOING_TYPE)
                        callType = "OUTGOING";
                    else if (type == CallLog.Calls.MISSED_TYPE)
                        callType = "MISSED";

                    textResult.append(callType + " - " + phone + "\n");
                } while (cur.moveToNext());
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CALL_LOG: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    listCallLog();
                } else {
                    // permission denied
                    Dialog d = new AlertDialog.Builder(MainActivity.this).setTitle("Error").
                            setMessage("I need permission to access Call logs").create();
                    d.show();
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}