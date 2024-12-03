import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    private static final int PORT = 5555;
    private static final int BUFFER_SIZE = 1024;

    private static List<Socket> clients = new ArrayList<>();
    private static Object lock = new Object();

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Server is listening on port " + PORT + "...");

        while (true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("New client connected: " + clientSocket.getInetAddress());

            synchronized (lock) {
                clients.add(clientSocket);
            }

            new Thread(new ClientHandler(clientSocket)).start();
        }
    }

    private static void broadcast(String message, Socket sender) throws IOException {
        synchronized (lock) {
            for (Socket client : clients) {
                if (client != sender) {
                    PrintWriter out = new PrintWriter(client.getOutputStream(), true);
                    out.println(message);
                }
            }
        }
    }

    private static class ClientHandler implements Runnable {
        private Socket clientSocket;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                out.println("Welcome to the chat room!");

                String message;
                while ((message = in.readLine()) != null) {
                    System.out.println("Received: " + message);
                    broadcast(message, clientSocket);
                }
            } catch (IOException e) {
                System.out.println("Client disconnected: " + clientSocket.getInetAddress());
            } finally {
                synchronized (lock) {
                    clients.remove(clientSocket);
                }
            }
        }
    }
}