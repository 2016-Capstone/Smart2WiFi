package com.example.nsc1303_pjh.smart2wifi;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.DhcpInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import static com.example.nsc1303_pjh.smart2wifi.Const.wifiManager;

/**
 * Created by nsc1303-PJH on 2016-09-27.
 */

public class ScanActivity extends AppCompatActivity{

    final private int MY_PERMISSIONS_REQUEST_READ_CONTACTS=1;
    final private String TAG="ScanActivity";

    WifiReceiver wifiReceiver;
    List<ScanResult> wifiList;
    ListView listView;
    ArrayAdapter<String> adapter;
    ArrayList<String> wifiResults;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        listView=(ListView)findViewById(R.id.listView);
        wifiResults=new ArrayList<String>();
        adapter=new ArrayAdapter<String>(this, R.layout.list_item,R.id.txt,wifiResults);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(listener);

        wifiManager = (WifiManager)this.getSystemService(Context.WIFI_SERVICE);
        wifiReceiver = new WifiReceiver();
        Log.d(TAG,"onCreate()");
        scanWifi();
    }

    AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            WifiConfiguration wifiConfiguration = new WifiConfiguration();

            wifiConfiguration.SSID = "\"".concat((String)parent.getAdapter().getItem(position)).concat("\"");
            wifiConfiguration.status = WifiConfiguration.Status.DISABLED;
            wifiConfiguration.priority = 40;

            wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            wifiConfiguration.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            wifiConfiguration.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            wifiConfiguration.allowedAuthAlgorithms.clear();
            wifiConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);

            wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);

            int networkId = wifiManager.addNetwork(wifiConfiguration);

            if (networkId != -1) {
                wifiManager.enableNetwork(networkId, true);
                DhcpInfo dhcpInfo = wifiManager.getDhcpInfo() ;
                int serverIP = dhcpInfo.gateway;

                Const.SERVER_IP = String.format("%d.%d.%d.%d", (serverIP & 0xff),(serverIP >> 8 & 0xff),(serverIP >> 16 & 0xff),(serverIP >> 24 & 0xff));
            }
//            Intent intentSocket = new Intent(getApplicationContext(), SocketActivity.class);
//            startActivity(intentSocket);

            Intent intentFlight = new Intent(getApplicationContext(), FlightActivity.class);
            intentFlight.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intentFlight);
            Log.d(TAG,"onItemClick()");
        }
    };

    public void scanWifi(){
        onResume();
        int permissionCheck = ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION);

        if(permissionCheck==-1){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        }

        wifiManager.startScan();
        Log.d(TAG,"scanWifi()");
    }

    protected void onResume(){
        super.onResume();
        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        Log.d(TAG,"onResume()");
    }

    protected void onPause(){
        super.onPause();
        unregisterReceiver(wifiReceiver);
        Log.d(TAG,"onPause()");
    }

    class WifiReceiver extends BroadcastReceiver {
        public void onReceive(Context c, Intent intent) {
            wifiResults.clear();
            wifiList = wifiManager.getScanResults();
            for(int i = 0; i < wifiList.size(); i++){
                if(wifiList.get(i).SSID.toString().equals("MyRP3")){
                    wifiResults.add(wifiList.get(i).SSID.toString());
                }
            }
            adapter.notifyDataSetChanged();
            Log.d(TAG,"onReceive() - BroadcastReceiver");
        }
    }

    public void onButtonStopClick(View v){
        wifiManager.setWifiEnabled(false); //와이파이 설정 자체를 OFF
//        onPause();                        // broadcast 명령을 해제
        Log.d(TAG,"onButtonStopClick()");
    }
}