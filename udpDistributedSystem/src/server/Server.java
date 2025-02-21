/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package server;

import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlo
 */
public class Server {

    private static final int PORT = 1001;
    private static final int BUFFER_SIZE = 2000;
    private static final int THREAD_POOL_SIZE = 10;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)  {
        try (
            DatagramSocket udpSocket = new DatagramSocket(Server.PORT);
        ) {
            ExecutorService threadPool = Executors.newFixedThreadPool(Server.THREAD_POOL_SIZE);
            System.out.println("UDP server running on port " + Server.PORT);
            while (true) {
                byte[] buffer = new byte[BUFFER_SIZE];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                udpSocket.receive(packet);
                threadPool.execute(new RequestHandler(packet));
            }
        } catch (SocketException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
