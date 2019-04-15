package com.passgify.bfrisbie.passgify;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

public class ParameterInputActivity extends AppCompatActivity {
    public final static String PREF_NAME = "passgify_prefs";
    public final static String SERVICE_ID = "passgify.SERVICE_ID";
    public final static String KEY = "passgify.KEY";
    public final static String PREFIX = "passgify.PREFIX";
    public final static String LENGTH = "passgify.LENGTH";

    protected Spinner prefix_spinner;
    protected Spinner length_spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        if (prefs.getString(SetupActivity.SALT, null) == null
                || prefs.getString(SetupActivity.DEFAULT_PREFIX, null) == null
                || prefs.getString(SetupActivity.HASH_ALG, null) == null) {
            Intent intent = new Intent(this, SetupActivity.class);
            startActivity(intent);
        }
        prefix_spinner = (Spinner) findViewById(R.id.prefix_spinner);
        length_spinner = (Spinner) findViewById(R.id.length_spinner);
        setContentView(R.layout.activity_parameter_input);
    }
    /**
     * Called when the user clicks the Send button
     */
    public void sendMessage(View view) {
        Intent intent = new Intent(this, OverwriteMessageActivity.class);

        EditText service_id = (EditText) findViewById(R.id.service_id);
        intent.putExtra(SERVICE_ID, service_id.getText().toString());

        EditText key = (EditText) findViewById(R.id.key);
        intent.putExtra(KEY, key.getText().toString());
        key.setText("");

        Spinner prefix = (Spinner) findViewById(R.id.prefix_spinner);
        intent.putExtra(PREFIX, prefix.getSelectedItem().toString());

        Spinner length = (Spinner) findViewById(R.id.length_spinner);
        intent.putExtra(LENGTH, length.getSelectedItem().toString());

        startActivity(intent);
    }
}