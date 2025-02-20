/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 *
 * @author carlo
 */
public class Server {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws SocketException, IOException {
        // UDP Socket on port 1001
        DatagramSocket udpSocket = new DatagramSocket(1001);

        while (true) {
            // the buffer or basket for the data
            byte buffer[] = new byte[2000];

            // make the packet and receive
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            System.out.println("Waiting to receive...");
            udpSocket.receive(packet);

            // get message
            String decodedMessage = new String(packet.getData(), 0, packet.getLength());

            // parse the decodedmessage
            String[] data = decodedMessage.split(",");
            if (data.length != 3) {
                System.out.println("Invalid format");
                udpSocket.close();
                return;
            }

            String name = data[0];
            double weight = Double.parseDouble(data[1]);
            double height = Double.parseDouble(data[2]);

            // calculate BMI
            double bmi = weight / (height * height);
            String range = "";

            if (bmi < 18.5) {
                range = "thin";
            } else if (bmi >= 18.5 && bmi <= 24.9) {
                range = "healthy";
            } else if (bmi >= 25 && bmi <= 29.9) {
                range = "overweight";
            } else {
                range = "obese";
            }

            String result = name + "," + weight + "," + height + "," + bmi + "," + range;

            // send back the data
            InetAddress address = packet.getAddress();
            int port = packet.getPort();

            byte[] response = result.getBytes();

            DatagramPacket responsePacket = new DatagramPacket(response, response.length, address, port);

            udpSocket.send(responsePacket);
        }
    }

}
