package main.client;

import java.io.*;
import java.net.*;

public class ClientApp {

    private static boolean stop = false;

    public static void main(String[] args) throws IOException {

        if (args.length != 1) {
            System.err.println("Usage: java -cp classes main.Client <host>:<port>");
            System.exit(1);
        }

        String[] hostport = args[0].split(":");
        if (hostport.length != 2) {
            System.err.println("Invalid format. Please provide <host>:<port>.");
            System.exit(1);
        }

        System.out.println("Connecting to the server");
        Socket sock = new Socket(hostport[0], Integer.parseInt(hostport[1]));

        System.out.println("Connected");

        Console cons = System.console();

        OutputStream os = sock.getOutputStream();
        Writer writer = new OutputStreamWriter(os);
        BufferedWriter bw = new BufferedWriter(writer);

        InputStream is = sock.getInputStream();
        Reader reader = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(reader);

        while (true) {
            String userCommand = cons.readLine(">>> Input: ").trim();
            if (userCommand.equals("exit")){
                System.out.println("exiting");
                break;
            }
            bw.write(userCommand + "\n");
            bw.flush();

            String fromServer = br.readLine();
            if(fromServer == null){
                System.out.println("server closed the connection");
                break;
            }
            System.out.println(">>> Server: " + fromServer);

        }

    }
}
