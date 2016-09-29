package com.example.nsc1303_pjh.smart2wifi;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private Button buttonScan;
    boolean wifiEnabled=false;
    WifiManager wifiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonScan=(Button)findViewById(R.id.buttonScan);
        wifiManager=(WifiManager) this.getSystemService(Context.WIFI_SERVICE);
    }

    public void onButtonScanClick(View v){
        wifiEnabled=wifiManager.isWifiEnabled();

        if(wifiEnabled==false){
            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), R.string.snack_bar_wifi_on, Snackbar.LENGTH_LONG)
                    .setActionTextColor(Color.parseColor("#FF0000")).setAction("YES", new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            wifiEnabled=wifiManager.setWifiEnabled(true);
                        }
                    });
            TextView textView = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);
            snackbar.show();
        }else{
            Intent intentScan = new Intent(this,ScanActivity.class);
            startActivity(intentScan);
        }
    }
}
