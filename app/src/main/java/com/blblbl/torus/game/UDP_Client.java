package com.blblbl.torus.game;

import android.os.StrictMode;
import android.util.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class UDP_Client {

    private String Message;

    private DatagramSocket mSocket;
    public UDP_Client() {
   /*     try {
 //           mSocket = new DatagramSocket();
//            mSocket.connect(InetAddress.getByName("http://192.168.0.29/tests/app/index.php"),32000);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }


    public void setMessage(String message) {
        Message = message;
    }

    public void NachrichtSenden() {
        Thread t = new Thread(new Second());
        t.start();
    }

    public class Second implements Runnable {
        Second()
        {
            run();
        }
        public void run() {
            try {

                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
                String serverAddress = "78.229.157.187";
                Socket s = new Socket(serverAddress, 9090);
                BufferedReader input =
                        new BufferedReader(new InputStreamReader(s.getInputStream()));
                String answer = input.readLine();
                System.exit(0);

                /*
                String messageStr = Message;
                int msg_length = messageStr.length();
                byte[] message = messageStr.getBytes();
                DatagramPacket p = new DatagramPacket(message, msg_length);
                mSocket.send(p);
                mSocket.receive(p);*/
                Log.e("bah voilà", answer);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

