import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class RockPaperScissorsClientUDP {

    public static void main(String[] args) throws Exception {

        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter your player name: ");
        String playerName = scanner.nextLine().trim();

        DatagramSocket socket = new DatagramSocket();
        InetAddress serverAddress = InetAddress.getByName("localhost");

        int serverPort = 5000;

        System.out.println("Client started. Sending name: " + playerName);

        send(socket, playerName, serverAddress, serverPort);

        while (true) {

            String msg = receive(socket);

            if (msg.equals("let's play")) {

                System.out.println("Server: let's play");

                System.out.print("Your move (1=Rock, 2=Paper, 3=Scissors): ");
                String move = scanner.nextLine().trim();

                send(socket, playerName + " " + move, serverAddress, serverPort);
            }

            else if (msg.startsWith("ERROR")) {

                System.out.println("Server: " + msg);

                System.out.print("Re-enter your move: ");
                String move = scanner.nextLine().trim();

                send(socket, playerName + " " + move, serverAddress, serverPort);
            }

            else if (msg.startsWith("Winner:")) {
                System.out.println(msg);
            }
            else if (msg.startsWith("Game Over!")) {
                System.out.println(msg);
                break;
            }
            else {
                System.out.println("Server: " + msg);
            }
        }

        socket.close();
    }

    private static void send(DatagramSocket socket, String msg, InetAddress addr, int port) throws Exception {
        byte[] data = msg.getBytes(StandardCharsets.UTF_8);
        DatagramPacket packet = new DatagramPacket(data, data.length, addr, port);
        socket.send(packet);
    }

    private static String receive(DatagramSocket socket) throws Exception {
        byte[] buffer = new byte[1024];
        DatagramPacket pkt = new DatagramPacket(buffer, buffer.length);
        socket.receive(pkt);
        return new String(pkt.getData(), 0, pkt.getLength());
    }
}
