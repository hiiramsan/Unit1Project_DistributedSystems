/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package middleware;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 *
 * @author carlo
 */
public class Middleware {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws SocketException, IOException {
        
        DatagramSocket udpSocket = new DatagramSocket(1002);
        while(true) {
            byte[] buffer = new byte[2000];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            System.out.println("Waiting to receive requests...");
            udpSocket.receive(packet);
            
            // receive clietn reqs
            String clientMessage = new String(packet.getData(), 0, packet.getLength());
            System.out.println("Middleware received: " + clientMessage);
            
            // send it to server
            DatagramSocket fwSocket = new DatagramSocket();
            DatagramPacket serverPacket = new DatagramPacket(clientMessage.getBytes(), clientMessage.length(), InetAddress.getLocalHost(), 1001);
            fwSocket.send(serverPacket);
            
            // receive from server
             byte[] serverBuffer = new byte[2000];
            DatagramPacket serverResponse = new DatagramPacket(serverBuffer, serverBuffer.length);
            fwSocket.receive(serverResponse);
            fwSocket.close();
            
            String serverMessage = new String(serverResponse.getData(), 0, serverResponse.getLength());
            
            // send response back to the Client
            InetAddress clientAddress = packet.getAddress();
            int clientPort = packet.getPort();
            DatagramPacket responsePacket = new DatagramPacket(serverMessage.getBytes(), serverMessage.length(), clientAddress, clientPort);
            udpSocket.send(responsePacket);
            
        }
        
        
    }
    
}
