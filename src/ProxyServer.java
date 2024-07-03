import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ProxyServer {

    public static void main(String[] args) {
        int localPort = 8800; // Port on which the proxy server listens

        try (ServerSocket serverSocket = new ServerSocket(localPort)) {
            System.out.println("Proxy server running on port " + localPort);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());

                // Connect to the target server
                Socket targetSocket = new Socket("localhost", 8888); // Replace "example.com" with the target server's hostname/IP and port

                // Start separate threads to handle client-to-server and server-to-client communication
                Thread clientToServer = new Thread(new ProxyThread(clientSocket.getInputStream(), targetSocket.getOutputStream()));
                Thread serverToClient = new Thread(new ProxyThread(targetSocket.getInputStream(), clientSocket.getOutputStream()));

                clientToServer.start();
                serverToClient.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class ProxyThread implements Runnable {
        private final InputStream inputStream;
        private final OutputStream outputStream;

        ProxyThread(InputStream inputStream, OutputStream outputStream) {
            this.inputStream = inputStream;
            this.outputStream = outputStream;
        }

        @Override
        public void run() {
            try {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                    outputStream.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}