import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class Servidor {

    private static final int PUERTO = 3400;

    public static void main(String[] args) throws IOException {
        ServerSocket ss = null;
        boolean continuar = true;

        System.out.println("Main server");

        try{
            ss = new ServerSocket(PUERTO);
        } catch (IOException e){
            System.err.println("no se pudo crear el socket con el puerto: " + PUERTO);
            System.exit(-1);
        }

        int numeroThread = 0;

        while(continuar){
            //creación de servidores delegados
            Socket socket  = ss.accept();
            ThreadServidor thread = new ThreadServidor(socket,numeroThread);
            numeroThread++;
            thread.start();
        }
        ss.close();
    } 
}
