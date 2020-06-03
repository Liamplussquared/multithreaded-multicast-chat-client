import java.io.*;
import java.net.*;
import java.util.Scanner;



public class MulticastChat {
    public static void main(String args []){
        sender();
    }
    
    
    // sender takes input from keyboard and sends to multicast address
    // reciever thread created so messages can be displayed while user retains ability to send messages
    public static void sender() {
        Reciever worker = new Reciever();
        new Thread(worker).start();
        
        DatagramSocket socket = null;
        DatagramPacket outPacket = null;
        byte[] outBuf;
        final int PORT = 8888;
        Scanner scan = new Scanner(System.in);
        try {
            socket = new DatagramSocket();
            long counter = 0;
            String msg;
            while (true) {
                msg = scan.nextLine();
                counter++;
                outBuf = msg.getBytes();
                //Send to multicast IP address and port
                InetAddress address = InetAddress.getByName("224.2.2.3");
                outPacket = new DatagramPacket(outBuf, outBuf.length, address, PORT);
                socket.send(outPacket);
                System.out.println("Sent following message to multicast address: " + msg);
            }
        } catch (IOException ioe) {
            System.out.println(ioe);
        }
    }
    
    
    // receiver sits and waits for incoming messages, sends message to multicast address
    static class Reciever implements Runnable {
        
        public void run() {
            MulticastSocket socket = null;
            DatagramPacket inPacket = null;
            byte[] inBuf = new byte[256];
            try {
                //Prepare to join multicast group
                socket = new MulticastSocket(8888);
                InetAddress address = InetAddress.getByName("224.2.2.3");
                socket.joinGroup(address);
                while (true) {
                    inPacket = new DatagramPacket(inBuf, inBuf.length);
                    socket.receive(inPacket);
                    String msg = new String(inBuf, 0, inPacket.getLength());
                    System.out.println("From a client at address " + inPacket.getAddress()  + " : " + msg);
                }
            } catch (IOException ioe) {
                System.out.println(ioe);
            }
        }
  }
}