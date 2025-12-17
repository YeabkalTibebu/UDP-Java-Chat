import java.net.*;
import java.util.Scanner;

public class ChatClient {
    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) {
       
        try (DatagramSocket socket = new DatagramSocket()) {
            InetAddress serverAddr = InetAddress.getByName(SERVER_IP);
            Scanner scanner = new Scanner(System.in);

            System.out.println("Connected to Chat Server. Type your message below:");

            Thread receiveThread = new Thread(() -> {
                try {
                    byte[] buffer = new byte[1024];
                    while (true) {
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                        socket.receive(packet);
                        String msg = new String(packet.getData(), 0, packet.getLength());
                        System.out.println("\n[Others]: " + msg);
                        System.out.print("> "); 
                    }
                } catch (Exception e) {
                    System.out.println("Connection closed.");
                }
            });
            receiveThread.start();

            while (true) {
                System.out.print("> ");
                String msg = scanner.nextLine();
                if (msg.equalsIgnoreCase("exit")) break;

                byte[] data = msg.getBytes();
                DatagramPacket packet = new DatagramPacket(data, data.length, serverAddr, SERVER_PORT);
                socket.send(packet);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
