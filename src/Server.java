import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class Server implements Runnable{    // runnable to make multiple threads
                                            // as we want multiple users to have access to server port

    Socket connection;
    public static Vector<BufferedWriter> client = new Vector<>();   // declaring vector collection framework
                                                                    // to store multiple clients

    // Constructor
    public Server(Socket connection){
        this.connection = connection;
    }

    // implementing abstract method run()
    @Override
    public void run() {
        try {
            // reading streamed message from socket
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream())  // getting streamed message from socket
            );
            // sending or streaming message using socket
            BufferedWriter bufferedWriter = new BufferedWriter(
                    new OutputStreamWriter(connection.getOutputStream())
            );

            client.add(bufferedWriter); // adding BufferedWriter object into Vector.
                                        // add all clients into a collection
            // infinite looping to receive multiple message
            while (true){
                String message = bufferedReader.readLine().trim();  // getting message into String
                                                                    // using BufferedReader object
                System.out.println("Received: \t"+ message);    // just for checking on console
                                                                // if message received (optional)
                // broadcasting streamed message all client stored in vector
                for (int i=0; i<client.size(); i++){
                    BufferedWriter bWriter = client.get(i); // getting all clients into BufferedWriter.
// NOTE: here client is BufferedWriter type vector as declared so we can store it into
//       BufferedWriter object otherwise we have to type cast client as BufferedWrite

                    if (bWriter != bufferedWriter){ // to remove sender from broadcast list.
                        bWriter.write(message+"\n");    // sending or broadcasting message
                        bWriter.flush();    // flushing message as characters into stream to reach destination(s).
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // main
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(6666); // port where user will connect
        while (true){   // infinite loop
            Socket allConnection = serverSocket.accept();   // to connect user to this socket
            Server server = new Server(allConnection);  // passing socket as argument into constructor
            // calling Thread class explicitly
            Thread thread = new Thread (server);    // passing constructor object to create thread(s)
            thread.start(); // starting threads(s)  // start() method will call run() method
        }
    }
}
