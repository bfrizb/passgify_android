package com.passgify.bfrisbie.passgify;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.widget.TextView;

public class OverwriteMessageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the message from the intent
        Intent intent = getIntent();
        String service_id = intent.getStringExtra(ParameterInputActivity.SERVICE_ID);
        String key = intent.getStringExtra(ParameterInputActivity.KEY);
        String prefix = intent.getStringExtra(ParameterInputActivity.PREFIX);
        int length = Integer.parseInt(intent.getStringExtra(ParameterInputActivity.LENGTH));
        SharedPreferences prefs = getSharedPreferences(ParameterInputActivity.PREF_NAME, MODE_PRIVATE);

        // Get Salt
        String salt = prefs.getString(SetupActivity.SALT, null);
        // Get Prefix
        if (prefix.equals("<Default>")) {
            prefix = prefs.getString(SetupActivity.DEFAULT_PREFIX, null);
            if (prefix == null) {
                try {
                    throw new Exception();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        } else if (prefix.equals("<No Prefix>")) {
            prefix = "";
        } else {
            try {
                throw new Exception();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        // Get Hashing Algorithm
        MessageDigest digester = null;
        try {
            digester = MessageDigest.getInstance(prefs.getString(SetupActivity.HASH_ALG, null));
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // Generate Hash Digest
        String to_digest = service_id + salt + key;
        digester.update(to_digest.getBytes(), 0, to_digest.length());
        String pb64_hash = computePb64String(digester.digest());
        String full_password = prefix + pb64_hash;

        if (full_password.length() < length) {
            throw new StringIndexOutOfBoundsException("The max password length for your " +
                    "chosen prefix is " + full_password.length() + " characters");
        }
        String shortened_password = full_password.substring(0, length);
        final String overwrite_string = new String(new char[2 * length]).replace("\0", "z");

        int sdk = android.os.Build.VERSION.SDK_INT;
        if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
            final android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(shortened_password);

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    clipboard.setText(overwrite_string);
                }
            }, 10000);

        } else {
            final android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("password label", shortened_password);
            clipboard.setPrimaryClip(clip);

            final android.content.ClipData clip2 = android.content.ClipData.newPlainText("overwrite label",
                    overwrite_string);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    clipboard.setPrimaryClip(clip2);
                }
            }, 10000);
        }
        // Create the text view
        TextView textView = new TextView(this);
        // textView.setTextSize(40);
        textView.setText("10 seconds until overwrite occurs");
        // TODO - delete
        if(salt == null)
            textView.setText("NULL salt");

        // Set the text view as the activity layout
        setContentView(textView);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public static String computePb64String(byte[] ra) {
        char[] DEFAULT_PB64_MAP = create_pb64_map();
        //for(int i = 0; i < 64; i++) System.out.println(i + " | " + DEFAULT_PB64_MAP[i]);
        //   System.out.println();

        String pb_digest = "";
        for(int i = 3; i < ra.length + 1; i += 3) {
            byte byteA = ra[i-3];
            byte byteB = ra[i-2];
            byte byteC = ra[i-1];
            byte bits0 = (byte) ((byteA >> 2) & 0x3F);    // 0x3F = (00111111)
            byte bits1 = (byte) ((((byteA & 0x03) << 4) | (byteB >> 4)) & 0x3F);
            byte bits2 = (byte) ((((byteB & 0x3F) << 2) | (byteC >> 6)) & 0x3F);
            byte bits3 = (byte) (byteC & 0x3F);
            pb_digest += "" + DEFAULT_PB64_MAP[bits0] + DEFAULT_PB64_MAP[bits1] + DEFAULT_PB64_MAP[bits2] + DEFAULT_PB64_MAP[bits3];
            /* OLD pb64 algorithm
            byte bits2 = (byte) ((ra[i-3] & 0xFC) >> 2);                                 // 0xFC = (11111100)
            byte bits1 = (byte) (((ra[i-3] & 0x03) << 4) | ((ra[i-2] & 0xF0) >> 4));
            byte bits4 = (byte) (((ra[i-2] & 0x0F) << 2) | ((ra[i-1] & 0xC0) >> 6));
            byte bits3 = (byte) (ra[i-1] & 0x3F);                                        // 0x3F = (00111111)
            pb_digest += "" + DEFAULT_PB64_MAP[bits1] + DEFAULT_PB64_MAP[bits2] + DEFAULT_PB64_MAP[bits3] + DEFAULT_PB64_MAP[bits4];
            */
        }
        return pb_digest;
    }
    public static char[] create_pb64_map() {
        char[] array = new char[64];
        int[][] ranges = {{65, 73}, {74, 79}, {80, 91}, {97, 108}, {109, 123}};
        int index = 0;
        for (int[] ra: ranges) {
            for (int n = ra[0]; n < ra[1]; n++) {
                array[index] = (char)n;
                index++;
            }
        }
        for (int i = 0; i < 10; i++) {
            array[i + 49] = (char)(i + 48);
        }
        for (int i = 0; i < 5; i++) {
            array[i + 59] = (char)(i + 48);
        }
        return array;
    }
}
