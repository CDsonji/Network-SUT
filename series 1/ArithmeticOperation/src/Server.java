import java.io.*;
import java.net.*;

public class Server {
    public static void main(String[] args) throws IOException {
        int port = 12345;
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Server started on port " + port);

        Socket clientSocket = serverSocket.accept();
        System.out.println("Client connected");

        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

        boolean started = false;
        String message;
        Double first_number = null;
        Double second_number = null;
        int stage = 1;
        while ((message = in.readLine()) != null) {
            if (message.equalsIgnoreCase("exit")) {
                out.println("server shutting down");
                in.close();
                out.close();
                clientSocket.close();
                serverSocket.close();
                System.out.println("Server stopped.");
                break;
            }
            System.out.println("Client: " + message);
            if (!started) {
                if (message.equalsIgnoreCase("start")) {
                    started = true;
                    out.println("ok");
                } else {
                    out.println("enter the command \"start\"");
                }
            } else {
                if (stage == 1) {
                    try {
                        first_number = Double.parseDouble(message);
                        stage++;
                        out.println("first number received");
                    } catch (Exception e) {
                        e.printStackTrace();
                        out.println("enter a valid number");
                    }
                } else if (stage == 2) {
                    try {
                        second_number = Double.parseDouble(message);
                        stage++;
                        out.println("second number received");
                    } catch (Exception e) {
                        e.printStackTrace();
                        out.println("enter a valid number");
                    }
                } else if (stage == 3) {
                    switch (message) {
                        case "*" -> {
                            out.println("arithmetic operation received. calculation: " + first_number * second_number);
                            stage = 1;
                        }
                        case "/" -> {
                            try {
                                double calculation = first_number / second_number;
                                out.println("arithmetic operation received. calculation: " + calculation);
                                stage = 1;
                            } catch (ArithmeticException e) {
                                e.printStackTrace();
                                out.println(e.getMessage());
                            }
                        }
                        case "+" -> {
                            out.println("arithmetic operation received. calculation: " + first_number + second_number);
                            stage = 1;
                        }
                        case "-" -> {
                            out.println("arithmetic operation received. calculation: " + (first_number - second_number));
                            stage = 1;
                        }
                        default -> out.println("enter a valid arithmetic operation");
                    }
                }
            }
        }
    }
}
