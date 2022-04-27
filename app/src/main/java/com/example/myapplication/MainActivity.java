package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    Button sendButton;
    Button connectButton;

    EditText ipText;
    EditText portText;
    EditText messageText;
    EditText receivedText;

    Socket socket;

    DataInputStream in;
    DataOutputStream out;

    Thread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        sendButton = findViewById(R.id.sendButton);
        connectButton = findViewById(R.id.connectButton);

        ipText = findViewById(R.id.ipText);
        portText = findViewById(R.id.portText);

        messageText = findViewById(R.id.messageText);
        receivedText = findViewById(R.id.receivedText);

        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (thread != null) {
                        thread.interrupt();
                    }

                    if (socket != null) {
                        socket.close();
                    }

                    socket = new Socket(ipText.getText().toString(), Integer.parseInt(portText.getText().toString()));
                    out = new DataOutputStream(socket.getOutputStream());
                    in = new DataInputStream(socket.getInputStream());
                    thread = new Thread() {
                        @Override
                        public void run() {
                            try {
                                while (true) {
                                    if (in != null) {
                                        receivedText.setText(in.readUTF());
                                    }
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    };

                    thread.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    out.writeUTF(messageText.getText().toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}