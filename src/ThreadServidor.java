import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.PrivateKey;
import java.security.PublicKey;

public class ThreadServidor extends Thread {

    private Socket sktCliente = null;
    private Integer id;
    public static PublicKey llavePublica;
    private static PrivateKey llavePrivada;

    public ThreadServidor(Socket pSocket, Integer id){
        this.sktCliente = pSocket;
        this.id = id;

    }

    public void run(){
        System.out.println("Inicio de un nuevo thread: " + id);

        try{
            ObjectInputStream escritor = new ObjectInputStream(sktCliente.getInputStream());
            ObjectOutputStream lector = new ObjectOutputStream(sktCliente.getOutputStream());
            
            ProtocoloServidor.enviarLlavePublica(lector);
            try {
                ProtocoloServidor.reto(escritor, lector);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            ProtocoloServidor.diffieHelman(escritor, lector);
            System.out.println("\nCONTINUAR (Paso 12)\n");
            ProtocoloServidor.iniciarSesion(escritor, lector);


            escritor.close();
            lector.close();
            sktCliente.close();
        } catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }}

    // Setter para la clave pública
    public static synchronized void setLlavePublica(PublicKey llave) {
        llavePublica = llave;
    }

    // Setter para la clave privada
    public static synchronized void setLlavePrivada(PrivateKey llave) {
        llavePrivada = llave;
    }


    
}
