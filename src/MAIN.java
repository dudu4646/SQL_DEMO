import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MAIN {

    public static void main(String[] args) {
        ServerSocket s =null;

        try {
            s=new ServerSocket(10000);
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (true){
            System.out.println("wayting");
            Socket incomig = null;

            try {
                incomig=s.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println(incomig);
            new SocketHandler(incomig).start();

        }

    }


}
