import java.net.*;
import java.nio.charset.StandardCharsets;

public class RockPaperScissorsServerUDP {

    public static void main(String[] args) throws Exception {

        DatagramSocket server = new DatagramSocket(5000);
        System.out.println("UDP Server running...");

        // ---- Receive Player 1 name ----
        DatagramPacket p1 = receivePacket(server);
        String name1 = packetData(p1);
        InetAddress addr1 = p1.getAddress();
        int port1 = p1.getPort();
        System.out.println("Player 1: " + name1);

        // ---- Receive Player 2 name ----
        DatagramPacket p2 = receivePacket(server);
        String name2 = packetData(p2);
        InetAddress addr2 = p2.getAddress();
        int port2 = p2.getPort();
        System.out.println("Player 2: " + name2);

        int score1 = 0, score2 = 0;

        while (score1 < 3 && score2 < 3) {

            // Tell players to play
            send(server, "let's play", addr1, port1);
            send(server, "let's play", addr2, port2);

            String move1 = null;
            String move2 = null;

            // ---- Wait until VALID moves for BOTH players received ----
            while (move1 == null || move2 == null) {

                DatagramPacket pkt = receivePacket(server);
                String msg = packetData(pkt);

                String[] parts = msg.split(" ");
                if (parts.length != 2) continue;

                String player = parts[0];
                String move = parts[1];

                // Validate move
                if (!move.equals("1") && !move.equals("2") && !move.equals("3")) {
                    send(server, "ERROR Invalid input! Enter 1,2,3", pkt.getAddress(), pkt.getPort());
                    continue;
                }

                // Assign correct move
                if (player.equals(name1)) {
                    move1 = move;
                } else if (player.equals(name2)) {
                    move2 = move;
                }
            }

            // ---- Determine round winner ----
            String roundWinner = determineWinner(
                    Integer.parseInt(move1),
                    Integer.parseInt(move2),
                    name1,
                    name2
            );

            if (roundWinner.equals(name1)) score1++;
            else if (roundWinner.equals(name2)) score2++;

            String result = "Winner: " + roundWinner +
                    " | Score " + score1 + " - " + score2;

            send(server, result, addr1, port1);
            send(server, result, addr2, port2);
        }

        String finalWinner = (score1 == 3 ? name1 : name2);

        send(server, "Game Over! Winner: " + finalWinner, addr1, port1);
        send(server, "Game Over! Winner: " + finalWinner, addr2, port2);

        System.out.println("Game finished. Winner: " + finalWinner);
    }

    // -------- Helper Methods --------

    private static void send(DatagramSocket socket, String msg, InetAddress addr, int port) throws Exception {
        byte[] data = msg.getBytes(StandardCharsets.UTF_8);
        DatagramPacket packet = new DatagramPacket(data, data.length, addr, port);
        socket.send(packet);
    }

    private static DatagramPacket receivePacket(DatagramSocket socket) throws Exception {
        byte[] buffer = new byte[1024];
        DatagramPacket pkt = new DatagramPacket(buffer, buffer.length);
        socket.receive(pkt);
        return pkt;
    }

    private static String packetData(DatagramPacket pkt) {
        return new String(pkt.getData(), 0, pkt.getLength());
    }

    private static String determineWinner(int m1, int m2, String n1, String n2) {
        if (m1 == m2) return "Draw";
        if ((m1 == 1 && m2 == 3) || (m1 == 2 && m2 == 1) || (m1 == 3 && m2 == 2)) {
            return n1;
        } else {
            return n2;
        }
    }
}
