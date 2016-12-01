package com.example.nsc1303_pjh.smart2wifi;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import static com.example.nsc1303_pjh.smart2wifi.Const.SERVER_IP;

/**
 * Created by nsc1303-PJH on 2016-11-13.
 */

public class FlightActivity extends AppCompatActivity {
    private Button buttonEmergency;

    private Button buttonLandTakeOff;

    //roll
    private Button buttonForward;
    private Button buttonBack;
    private Button buttonRollLeft;
    private Button buttonRollRight;

    //yaw
    private Button buttonUp;
    private Button buttonDown;
    private Button buttonRight;
    private Button buttonLeft;

    private Socket client;
    private PrintWriter printWriter;
    private BufferedReader bufferedReader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flight);
        buttonEmergency=(Button)findViewById(R.id.buttonEmergency);

        buttonLandTakeOff=(Button)findViewById(R.id.buttonLandTakeOff);

        buttonForward=(Button)findViewById(R.id.buttonForward);
        buttonBack=(Button)findViewById(R.id.buttonBack);
        buttonRollLeft=(Button)findViewById(R.id.buttonRollLeft);
        buttonRollRight=(Button)findViewById(R.id.buttonRollRight);

        buttonUp=(Button)findViewById(R.id.buttonUp);
        buttonDown=(Button)findViewById(R.id.buttonDown);
        buttonRight=(Button)findViewById(R.id.buttonRight);
        buttonLeft=(Button)findViewById(R.id.buttonLeft);
        buttonLandTakeOff.setText("Take Off");

        ChatOperator chatOperator = new ChatOperator();
        chatOperator.execute();
    }

    private class ChatOperator extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... params) {
            try{
                client = new Socket(SERVER_IP,3001);

                if(client!=null){
                    printWriter = new PrintWriter(client.getOutputStream(),true);
                    InputStreamReader inputStreamReader = new InputStreamReader((client.getInputStream()));
                    bufferedReader = new BufferedReader(inputStreamReader);
                }else{
                    Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),R.string.snack_bar_server_port, Snackbar.LENGTH_LONG);
                    View v = snackbar.getView();
                    TextView textView = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(Color.WHITE);
                    snackbar.show();
                }
            } catch (IOException e) {
                Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),R.string.snack_bar_server_connect, Snackbar.LENGTH_LONG);
                View v = snackbar.getView();
                TextView textView = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.WHITE);
                snackbar.show();
                e.printStackTrace();
            }
            return null;
        }

        private void MessageSend(String text){
            final Sender messageSender=new Sender();
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB){
                messageSender.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,text);
            }else{
                messageSender.execute(text);
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            buttonEmergency.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    String text="EM";
                    MessageSend(text);
                }
            });

            buttonLandTakeOff.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    String text=null;
                    if(buttonLandTakeOff.getText()=="Take Off"){
                        text="116";
                        buttonLandTakeOff.setText("Landing");
                    }else{
                        text="32";
                        buttonLandTakeOff.setText("Take Off");
                    }
                    MessageSend(text);
                }
            });

            buttonForward.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    String text="114";
                    MessageSend(text);
                }
            });

            buttonBack.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    String text="102";
                    MessageSend(text);
                }
            });

            buttonRollLeft.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    String text="100";
                    MessageSend(text);
                }
            });

            buttonRollRight.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    String text="103";
                    MessageSend(text);
                }
            });

            buttonUp.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    String text="65";
                    MessageSend(text);
                }
            });

            buttonDown.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    String text="66";
                    MessageSend(text);
                }
            });

            buttonRight.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    String text="67";
                    MessageSend(text);
                }
            });

            buttonLeft.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    String text="68";
                    MessageSend(text);
                }
            });

            if(client!=null){
                Receiver receiver = new Receiver();
                receiver.execute();
            }
        }
    }

    private class Sender extends AsyncTask<String,String,Void>{
        private String message;

        @Override
        protected Void doInBackground(String... params) {
            message=params[0];
            printWriter.write(message+"\n");
            printWriter.flush();
            return null;
        }
    }

    private class Receiver extends AsyncTask<Void,Void,Void>{
        private String message;

        @Override
        protected Void doInBackground(Void... params) {
            while(true) {
                try {
                    if (bufferedReader.ready()) {
                        message = bufferedReader.readLine();
                        publishProgress(null);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),message, Snackbar.LENGTH_LONG);
            View v = snackbar.getView();
            TextView textView = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);
            snackbar.show();
        }
    }
}
