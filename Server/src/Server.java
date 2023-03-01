import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    private ServerSocket serverSocket;
    private ArrayList<ClientHandler> clients = new ArrayList<>();

    public void start() {
        try {
            serverSocket = new ServerSocket(6000);
            System.out.println("server is running on port : 6000");
            while (true) {
                Socket socket = serverSocket.accept();
                ClientHandler handler = new ClientHandler(socket);
                Thread thread = new Thread(handler);
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendAll(Massage massage){
        for (int i=0 ; i<clients.size() ; i++) {
            if(!clients.get(i).name.equals(massage.writer)){
                clients.get(i).send(massage);
            }
        }
    }

    private class ClientHandler implements Runnable{
        public Socket socket;
        public ObjectInputStream in;
        public ObjectOutputStream out;
        public String name = "";

        public ClientHandler(Socket socket) {
            try {
                this.socket = socket;
                out = new ObjectOutputStream(socket.getOutputStream());
                in = new ObjectInputStream(socket.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            clients.add(this);
            try {
                Massage massage = (Massage) in.readObject();
                this.name = massage.writer;
                massage = new Massage(name , "joined to the group");
                sendAll(massage);
                while (true) {
                    massage = (Massage) in.readObject();
                    if(massage.massage.equals("#exit")){
                        massage = new Massage(name , "left the group");
                        System.out.println(name + " : " + massage.massage);
                        sendAll(massage);
                        break;
                    }
                    System.out.println(name + " : " + massage.massage);
                    sendAll(massage);
                }
            } catch (IOException | ClassNotFoundException e) {
                System.out.println(name + " closed the program");
            }
            clients.remove(this);
        }

        public synchronized void send(Massage massage){
            try {
                out.writeObject(massage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
