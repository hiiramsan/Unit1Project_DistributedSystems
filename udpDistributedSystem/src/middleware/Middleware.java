/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package middleware;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Middleware {

    public static void main(String[] args) {
        try (DatagramSocket udpSocket = new DatagramSocket(1002)) {
            while (true) {
                
                DatagramPacket packet = receiveClientRequest(udpSocket);
                new Thread(new RequestHandler(packet, udpSocket)).start();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    
    private static DatagramPacket receiveClientRequest(DatagramSocket udpSocket) throws IOException {
        byte[] buffer = new byte[2000];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        System.out.println("Waiting to receive requests...");
        udpSocket.receive(packet);
        return packet;
    }

    
    static class RequestHandler implements Runnable {
        private DatagramPacket packet;
        private DatagramSocket udpSocket;

        public RequestHandler(DatagramPacket packet, DatagramSocket udpSocket) {
            this.packet = packet;
            this.udpSocket = udpSocket;
        }

        @Override
        public void run() {
            try {
                
                String clientMessage = processClientRequest();
                System.out.println("Middleware received: " + clientMessage);
                
               
                String serverMessage = forwardToServer(clientMessage);
                
                
                sendResponseToClient(serverMessage);
                
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        
        private String processClientRequest() {
            return new String(packet.getData(), 0, packet.getLength());
        }

        
        private String forwardToServer(String message) throws IOException {
            DatagramPacket serverResponse;
            try (DatagramSocket fwSocket = new DatagramSocket()) {
                byte[] messageBytes = message.getBytes();
                DatagramPacket serverPacket = new DatagramPacket(messageBytes, messageBytes.length, InetAddress.getLocalHost(), 1001);
                fwSocket.send(serverPacket);
                byte[] serverBuffer = new byte[2000];
                serverResponse = new DatagramPacket(serverBuffer, serverBuffer.length);
                fwSocket.receive(serverResponse);
            }
            return new String(serverResponse.getData(), 0, serverResponse.getLength());
        }

        
        private void sendResponseToClient(String serverMessage) throws IOException {
            InetAddress clientAddress = packet.getAddress();
            int clientPort = packet.getPort();
            DatagramPacket responsePacket = new DatagramPacket(serverMessage.getBytes(), serverMessage.length(), clientAddress, clientPort);
            udpSocket.send(responsePacket);
        }
    }
}
