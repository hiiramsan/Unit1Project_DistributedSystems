/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package client;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;

import javax.swing.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Scanner;
import java.util.logging.Logger;

/**
 *
 * @author carlo
 */
public class Client {
    private static Logger logger = Logger.getLogger(Client.class.getName());

    public static void main(String[] args) throws SocketException, IOException {
        setLookAndFeel();
        JOptionPane.showMessageDialog(null, "Client is running");
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

        // send to middleware
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getLocalHost(), 1002);
        udpSocket.send(packet);

        // receive the results
        byte[] bufferResponse = new byte[2000];

        DatagramPacket responsePacket = new DatagramPacket(bufferResponse, bufferResponse.length);
        udpSocket.receive(responsePacket);

        String serverResponse = new String(responsePacket.getData(), 0, responsePacket.getLength());

        System.out.println("Server Response: " + serverResponse);
        
        // close socket
        udpSocket.close();
    }

    private static void setLookAndFeel() {
        try {
            UIManager.setLookAndFeel(new FlatMacDarkLaf());
        } catch (Exception e) {
            logger.severe("Error setting FlatLaf theme");
        }
    }
}
