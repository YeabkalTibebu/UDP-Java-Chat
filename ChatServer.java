import java.net.*;
import java.util.*;

public class ChatServer {
    private static final int PORT = 12345;
       private static Set<String> clientRegistry = new HashSet<>();

    public static void main(String[] args) {
        try (DatagramSocket serverSocket = new DatagramSocket(PORT)) {
            System.out.println("UDP Chat Server started on port " + PORT);
            byte[] buffer = new byte[1024];

            while (true) {
                DatagramPacket incomingPacket = new DatagramPacket(buffer, buffer.length);
                serverSocket.receive(incomingPacket); 

                String message = new String(incomingPacket.getData(), 0, incomingPacket.getLength());
                InetAddress clientAddress = incomingPacket.getAddress();
                int clientPort = incomingPacket.getPort();

                String clientID = clientAddress.getHostAddress() + ":" + clientPort;
                clientRegistry.add(clientID);

                System.out.println("Message from " + clientID + ": " + message);

                
                broadcast(serverSocket, message, clientAddress, clientPort);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void broadcast(DatagramSocket socket, String msg, InetAddress senderAddr, int senderPort) throws Exception {
        byte[] data = msg.getBytes();
        for (String client : clientRegistry) {
            String[] parts = client.split(":");
            InetAddress destAddr = InetAddress.getByName(parts[0]);
            int destPort = Integer.parseInt(parts[1]);

            
            if (!(destAddr.equals(senderAddr) && destPort == senderPort)) {
                DatagramPacket packet = new DatagramPacket(data, data.length, destAddr, destPort);
                socket.send(packet);
            }
        }
    }
}
