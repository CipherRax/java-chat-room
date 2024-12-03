import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ChatClient {
    private static final int PORT = 5555;
    private static final int BUFFER_SIZE = 1024;

    public static void main(String[] args) throws IOException {
        Socket clientSocket = new Socket("localhost", PORT);
        System.out.println("Connected to server: " + clientSocket.getInetAddress());

        new Thread(new ReceiveMessages(clientSocket)).start();

        try (PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
             Scanner scanner = new Scanner(System.in)) {

            while (true) {
                String message = scanner.nextLine();
                if (message.equals("exit")) {
                    break;
                }
                out.println(message);
            }
        }
    }

    private static class ReceiveMessages implements Runnable {
        private Socket clientSocket;

        public ReceiveMessages(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
                String message;
                while ((message = in.readLine()) != null) {
                    System.out.println(message);
                }
            } catch (IOException e) {
                System.out.println("Disconnected from server.");
            }
        }
    }
}