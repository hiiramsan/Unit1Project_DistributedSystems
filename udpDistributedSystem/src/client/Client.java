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
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author carlo
 */
public class Client {
    private static final Logger logger = Logger.getLogger(Client.class.getName());

    public static void main(String[] args)  {
        try {
            setLookAndFeel();
            
            DatagramSocket udpSocket = new DatagramSocket();
            
            Person person = getUserInput();
            
            String message = personToCsv(person);
            
            sendMessage(message, udpSocket);
            
            String serverResponse = getServerResponse(udpSocket);
            
            showServerResponse(serverResponse);
            
            udpSocket.close();
        } catch (SocketException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static String getServerResponse(DatagramSocket udpSocket) throws IOException {
        byte[] bufferResponse = new byte[2000];

        DatagramPacket responsePacket = new DatagramPacket(bufferResponse, bufferResponse.length);
        try{
        udpSocket.receive(responsePacket);

        return new String(responsePacket.getData(), 0, responsePacket.getLength());
        }catch (IOException e){
        logger.log(Level.SEVERE,"Error receiving response from server.");
        throw new IOException("Error processing request. Invalid data.");
        }
    }

    private static void sendMessage(String message, DatagramSocket udpSocket) throws IOException {
        try{
        byte[] buffer = message.getBytes();
        // send to middleware
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getLocalHost(), 1002);
        udpSocket.send(packet);
        }catch (UnknownHostException e){
            logger.log(Level.SEVERE, "The server address could not be determined");
            throw new IOException("Could not connect to the server");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error sending message to server.", e);
            throw new IOException("Could not contact server.");
        }
    }
    

    private static void setLookAndFeel() {
        try {
            UIManager.setLookAndFeel(new FlatMacDarkLaf());
        } catch (UnsupportedLookAndFeelException e) {
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
