/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package client;

import com.formdev.flatlaf.themes.FlatMacDarkLaf;

import javax.swing.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.logging.Logger;

/**
 * @author carlo
 */
public class Client {
    private static final Logger logger = Logger.getLogger(Client.class.getName());

    public static void main(String[] args) throws IOException {
        setLookAndFeel();

        DatagramSocket udpSocket = new DatagramSocket();
        // make object
        Person person = getUserInput();

        // object to csv
        String message = personToCsv(person);

        byte[] buffer = message.getBytes();

        // send to middleware
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getLocalHost(), 1002);
        udpSocket.send(packet);

        // receive the results
        byte[] bufferResponse = new byte[2000];

        DatagramPacket responsePacket = new DatagramPacket(bufferResponse, bufferResponse.length);
        udpSocket.receive(responsePacket);

        String serverResponse = new String(responsePacket.getData(), 0, responsePacket.getLength());

        showServerResponse(serverResponse);

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

    private static Person getUserInput() {
        String name = getInput("Enter your name");
        double weight = getValidDoubleInput("Enter your weight (kg)");
        double height = getValidDoubleInput("Enter your height (m)");
        return new Person(name, weight, height);
    }

    private static String personToCsv(Person person) {
        return person.getName() + "," + person.getWeight() + "," + person.getHeight();
    }

    private static String getInput(String message) {
        String input = JOptionPane.showInputDialog(null, message, "IMC Calculator", JOptionPane.QUESTION_MESSAGE);
        return input != null ? input.trim() : "";
    }

    private static double getValidDoubleInput(String message) {
        double value = -1;
        while (value < 0) {
            try {
                String input = getInput(message);
                value = Double.parseDouble(input);
                if (value <= 0) {
                    JOptionPane.showMessageDialog(null, "Please enter a positive number.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    value = -1;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Please enter a valid number.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            }
        }
        return value;
    }

    private static void showServerResponse(String serverResponse) {
        String[] responseParts = serverResponse.split(",");

        String name = responseParts[0];
        double weight = Double.parseDouble(responseParts[1]);
        double height = Double.parseDouble(responseParts[2]);
        double imc = Double.parseDouble(responseParts[3]);
        String status = responseParts[4];

        String message = String.format("<html><font size='4'><b>IMC: %.2f</b><br>Name: %s<br>Weight: %.2f kg<br>Height: %.2f m<br>Status: %s</font></html>",
                imc, name, weight, height, status);

        JOptionPane.showMessageDialog(null, message, "Server Response", JOptionPane.INFORMATION_MESSAGE);
    }
}
