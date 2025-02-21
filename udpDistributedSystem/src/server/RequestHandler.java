package server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class RequestHandler implements Runnable {

    private final DatagramPacket packet;

    public RequestHandler(DatagramPacket packet) {
        this.packet = packet;
    }

    
    @Override
    public void run() {
        try {
            String decodedMessage = new String(this.packet.getData(), 0, this.packet.getLength());
            String[] data = decodedMessage.split(",");
            if (data.length != 3) {
                System.out.println("Invalid format...");
                return;
            }
            String name = data[0];
            double weight = Double.parseDouble(data[1]);
            double height = Double.parseDouble(data[2]);
            double bmi = weight / (height * height);
            String range = determineBMItring(bmi);
            String result = name + "," + weight + "," + height + "," + bmi + "," + range;
            InetAddress address = packet.getAddress();
            int port = packet.getPort();
            byte[] response = result.getBytes();
            DatagramSocket responseSocket = new DatagramSocket();
            DatagramPacket responsePacket = new DatagramPacket(response, response.length, address, port);
            responseSocket.send(responsePacket);
            responseSocket.close();
            System.out.println("Request: " + name + " with BMI: " + bmi);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String determineBMItring(double bmi) {
        if (bmi < 18.5) return "thin";
        if (bmi >= 18.5 && bmi <= 24.9) return "healthy";
        if (bmi >= 25 && bmi <= 29.9) return "overweight";
        return "obese";
    }
}