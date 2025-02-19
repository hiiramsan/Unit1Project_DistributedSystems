/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Scanner;

/**
 *
 * @author carlo
 */
public class Client {
    public static void main(String[] args) throws SocketException, IOException {
        DatagramSocket udpSocket = new DatagramSocket();

        // text to send
        Scanner sc = new Scanner(System.in);
        System.out.println("Type your name: ");
        String nombre = sc.nextLine();

        System.out.println("Type your weight (kg): ");
        Double peso = sc.nextDouble();

        System.out.println("Type your height (m): ");
        Double altura = sc.nextDouble();

        // make object
        Person p = new Person(nombre, peso, altura);

        // object to csv
        String message = p.getName() + "," + p.getWeight() + "," + p.getHeight();
        byte[] buffer = message.getBytes();

        // send
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getLocalHost(), 1001);
        udpSocket.send(packet);

        // Trying to receive the results
        byte[] bufferResponse = new byte[2000];

        DatagramPacket responsePacket = new DatagramPacket(bufferResponse, bufferResponse.length);
        udpSocket.receive(responsePacket);

        String serverResponse = new String(responsePacket.getData(), 0, responsePacket.getLength());

        System.out.println("Server Response: " + serverResponse);
        
        // close socket
        udpSocket.close();
    }
}
