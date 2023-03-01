import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Main {
    static Scanner input = new Scanner(System.in);
    static Socket socket;
    static ObjectInputStream in;
    static ObjectOutputStream out;
    static String name;

    public static void main(String[] args) {
        try {
            System.out.println("please enter your name:");
            name = input.nextLine();
            socket = new Socket("127.0.0.1", 6000);

            System.out.println("successfully connected.");

            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            out.writeObject(new Massage(name, ""));

            Read read = new Read();
            Thread readThread = new Thread(read);
            readThread.start();

            while (true) {
                String massage = input.nextLine();
                out.writeObject(new Massage(name, massage));
                if (massage.equals("#exit")) {
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("\u001B[31m" + "server ran out." + "\u001B[0m");
        }
        System.exit(0);
    }

    public static class Read implements Runnable{

        @Override
        public void run() {
            while (true) {
                try {
                     Massage massage = (Massage) in.readObject();
                    if(massage.massage.equals("joined to the group")){
                        System.out.println("\u001B[36m" + massage.writer.toString() + " : " + massage.massage.toString() + "\u001B[0m");
                    }
                    else if(massage.massage.equals("left the group")){
                        System.out.println("\u001B[31m" + massage.writer.toString() + " : " + massage.massage.toString() + "\u001B[0m");
                    }
                    else {
                        System.out.println(massage.writer.toString() + " : " + massage.massage.toString());
                    }
                } catch (IOException | ClassNotFoundException e) {
                    System.out.println("\u001B[31m" + "server ran out." + "\u001B[0m");
                    break;
                }
            }
            System.exit(0);
        }
    }
}
