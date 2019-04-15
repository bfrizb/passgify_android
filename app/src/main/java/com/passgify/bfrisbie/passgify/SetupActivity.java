package com.passgify.bfrisbie.passgify;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

public class SetupActivity extends AppCompatActivity {
    public final static String SALT = "passgify.SALT";
    public final static String DEFAULT_PREFIX = "passgify.DEFAULT_PREFIX";
    public final static String HASH_ALG = "passgify.HASH_ALG";

    protected Spinner hash_spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        hash_spinner = (Spinner) findViewById(R.id.hash_alg_spinner);
        setContentView(R.layout.activity_setup);
    }
    public void sendMessage(View view) {
        SharedPreferences.Editor editor = getSharedPreferences(
                ParameterInputActivity.PREF_NAME, MODE_PRIVATE).edit();
        EditText salt = (EditText) findViewById(R.id.salt);
        editor.putString(SALT, salt.getText().toString()).commit();
        EditText default_prefix = (EditText) findViewById(R.id.default_prefix);
        editor.putString(DEFAULT_PREFIX, default_prefix.getText().toString()).commit();
        Spinner hash_alg = (Spinner) findViewById(R.id.hash_alg_spinner);
        editor.putString(HASH_ALG, hash_alg.getSelectedItem().toString()).commit();

        Intent intent = new Intent(this, ParameterInputActivity.class);
        startActivity(intent);
    }
}
