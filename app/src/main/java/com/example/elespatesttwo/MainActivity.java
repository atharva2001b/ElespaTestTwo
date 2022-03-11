package com.example.elespatesttwo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity {
    //UI Element
    Button btnUp;
    Button btnDown;
    EditText txtAddress;

    Socket myAppSocket = null;
    public static String wifiModuleIp = "";
    public static int wifiModulePort = 0;
    public static String CMD = "0";
    public static String rData = "noDataRecv";
    public static String message="";

    TextView tvShowStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnUp = (Button) findViewById(R.id.btnUp);
        btnDown = (Button) findViewById(R.id.btnDown);
        tvShowStatus = findViewById(R.id.tv_status);

        txtAddress = (EditText) findViewById(R.id.ipAddress);

        btnUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getIPandPort();
                CMD = "On";
                Socket_AsyncTask cmd_increase_servo = new Socket_AsyncTask();
                cmd_increase_servo.execute();
                tvShowStatus.setText("$" + rData);
            }
        });
        btnDown.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                getIPandPort();
                CMD = "Off";
                Socket_AsyncTask cmd_increase_servo = new Socket_AsyncTask();
                cmd_increase_servo.execute();
                tvShowStatus.setText("$" + rData);
                System.out.println("debug1: in $");
            }
        });

    }


    public void getIPandPort()
    {
        String iPandPort = txtAddress.getText().toString();
        Log.d("MYTEST","IP String: "+ iPandPort);
        String temp[]= iPandPort.split(":");
        wifiModuleIp = temp[0];
        wifiModulePort = Integer.valueOf(temp[1]);
        Log.d("MY TEST","IP:" +wifiModuleIp);
        Log.d("MY TEST","PORT:"+wifiModulePort);
    }
    public class Socket_AsyncTask extends AsyncTask<Void,Void,Void>
    {
        Socket socket;

        @Override
        protected Void doInBackground(Void... params){
            try{

                InetAddress inetAddress = InetAddress.getByName(MainActivity.wifiModuleIp);
                socket = new java.net.Socket(inetAddress, MainActivity.wifiModulePort);
                DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());


                String rawString = CMD;
                byte[] bytes = rawString.getBytes(StandardCharsets.UTF_8);
                String utf8EncodedString = new String(bytes, StandardCharsets.UTF_8);
                CMD = utf8EncodedString;


                //for sending data
                dataOutputStream.writeBytes(CMD);
                dataOutputStream.close();

                // ---- for Receiving data
                // get the inputstream from socket, which will have
                // the message from the clients
                DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                System.out.println("debug1: $");
                System.out.println( "debug1: data received: " + dataInputStream.readLine());
                dataInputStream.close();


                socket.close();
            }catch (UnknownHostException e){e.printStackTrace();}
            catch (IOException e){e.printStackTrace();}
            return null;
        }
    }




}