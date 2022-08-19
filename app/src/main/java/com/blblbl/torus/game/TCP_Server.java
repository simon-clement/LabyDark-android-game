package com.blblbl.torus.game;
import android.os.StrictMode;
import android.util.Log;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

/**
 * A TCP server that runs on port 9090.  When a client connects, it
 * sends the client the current date and time, then closes the
 * connection with that client.  Arguably just about the simplest
 * server you can write.
 */
public class TCP_Server {
    public static void StartServer() {
        Thread t = new Thread(new Second());
        t.start();
    }

    public static class Second implements Runnable {
        Second() {
            run();
        }

        public void run() {
            try {

                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
                ServerSocket listener = new ServerSocket(9090);
                try {
                    while (true) {
                        Socket socket = listener.accept();
                        try {
                            PrintWriter out =
                                    new PrintWriter(socket.getOutputStream(), true);
                            out.println(new Date().toString());
                        } finally {
                            socket.close();
                        }
                        Thread.sleep(500);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    listener.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}