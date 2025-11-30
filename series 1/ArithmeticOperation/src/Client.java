import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws IOException {
        String host = "localhost";
        int port = 12345;

        Socket socket = new Socket(host, port);
        System.out.println("Connected to server");

        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("Enter message: ");
            String msg = scanner.nextLine();
            out.println(msg); // send message to server

            String response = in.readLine(); // read server response
            System.out.println("Server: " + response);

            if (msg.equalsIgnoreCase("exit")) {
                System.out.println("Exiting...");
                break;
            }
        }

        scanner.close();
        in.close();
        out.close();
        socket.close();
    }
}
