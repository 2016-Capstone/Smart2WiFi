package com.example.nsc1303_pjh.smart2wifi;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import static com.example.nsc1303_pjh.smart2wifi.Const.SERVER_IP;

/**
 * Created by nsc1303-PJH on 2016-10-31.
 */

public class SocketActivity extends AppCompatActivity {
    private EditText textField;
    private Button button;
    private TextView textView;
    private Socket client;
    private PrintWriter printwriter;
    private BufferedReader bufferedReader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_socket);

        textField = (EditText) findViewById(R.id.editText);
        button = (Button) findViewById(R.id.button);
        textView = (TextView) findViewById(R.id.textView);


        ChatOperator chatOperator = new ChatOperator();
        chatOperator.execute();
    }


    private class ChatOperator extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                client = new Socket(SERVER_IP, 3001);

                if (client != null) {
                    //자동 flushing 기능이 있는 PrintWriter 객체를 생성한다.
                    //client.getOutputStream() 서버에 출력하기 위한 스트림을 얻는다.
                    printwriter = new PrintWriter(client.getOutputStream(), true);
                    InputStreamReader inputStreamReader = new InputStreamReader(client.getInputStream());

                    //입력 스트림 inputStreamReader에 대해 기본 크기의 버퍼를 갖는 객체를 생성한다.
                    bufferedReader = new BufferedReader(inputStreamReader);
                } else {
                    System.out.println("Server has not bean started on port 9999.");
                }
            } catch (UnknownHostException e) {
                System.out.println("Faild to connect server " + SERVER_IP);
                e.printStackTrace();
            } catch (IOException e) {
                System.out.println("Faild to connect server " + SERVER_IP);
                e.printStackTrace();
            }
            return null;
        }

        /**
         * Following method is executed at the end of doInBackground method.
         */
        @Override
        protected void onPostExecute(Void result) {
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    String text = textField.getText().toString();

                    final Sender messageSender = new Sender(); // Initialize chat sender AsyncTask.
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        messageSender.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, text);
                    } else {
                        messageSender.execute(text);
                    }
                }
            });

            if ( client != null) {
                Receiver receiver = new Receiver(); // Initialize chat receiver AsyncTask.
                receiver.execute();
            }

        }

    }

    /**
     * This AsyncTask continuously reads the input buffer and show the chat
     * message if a message is availble.
     */
    private class Receiver extends AsyncTask<Void, Void, Void> {

        private String message;

        @Override
        protected Void doInBackground(Void... params) {
            while (true) {
                try {
                    //스트림으로부터 읽어올수 있으면 true를 반환한다.
                    if (bufferedReader.ready()) {
                        //'\n', '\r'을 만날 때까지 읽어온다.(한줄을 읽어온다.)
                        message = bufferedReader.readLine();
                        publishProgress(null);
                    }
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    Thread.sleep(500);
                } catch (InterruptedException ie) {
                }
            }
        }

        //publishProgress(null)에 의해서 호출된다. '
        //서버에서 전달받은 문자열을 읽어서 화면에 출력해준다.
        @Override
        protected void onProgressUpdate(Void... values) {
            textView.append("Server: " + message + "\n");
        }

    }

    /**
     * This AsyncTask sends the chat message through the output stream.
     */
    private class Sender extends AsyncTask<String, String, Void> {

        private String message;

        @Override
        protected Void doInBackground(String... params) {
            message = params[0];

            //문자열을 스트림에 기록한다.
            printwriter.write(message + "\n");

            //스트림을 플러쉬한다.
            printwriter.flush();
            return null;
        }

        //클라이언트에서 입력한 문자열을 화면에 출력한다.
        @Override
        protected void onPostExecute(Void result) {
            textField.setText(""); // Clear the chat box
            textView.append("Client: " + message + "\n");
        }
    }
}