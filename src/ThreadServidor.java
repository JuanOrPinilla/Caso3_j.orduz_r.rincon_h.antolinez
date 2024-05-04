import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

public class ThreadServidor extends Thread {

    private Socket sktCliente = null;
    private Integer id;
    public static PublicKey llavePublica;
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static Map<String, Long> times = new HashMap();

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
                long tiempoInicial = System.currentTimeMillis();
                ProtocoloServidor.reto(escritor, lector);
                long tiempoFinal = System.currentTimeMillis();
                long tiempoEjecucion = tiempoFinal - tiempoInicial;
                times.put("Generar firma", tiempoEjecucion);  
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            ProtocoloServidor.diffieHelman(escritor, lector);
            System.out.println("\nCONTINUAR (Paso 12)\n");
            ProtocoloServidor.iniciarSesion(escritor, lector);
            long tiempoInicial = System.currentTimeMillis();
            ProtocoloServidor.consulta(escritor, lector);
            long tiempoFinal = System.currentTimeMillis();
            long tiempoEjecucion = tiempoFinal - tiempoInicial;
            times.put("Desicfrar la consulta", tiempoEjecucion);  
            ProtocoloServidor.verificacionFinal(escritor, lector);


            escritor.close();
            lector.close();
            sktCliente.close();
        } catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("----------------------------------------------------------------");
        for (Map.Entry<String, Long> entry : times.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue()+"ms");
        }
        System.out.println("----------------------------------------------------------------");
        System.out.println("----------------------------------------------------------------");
        System.out.println("----------------------------------------------------------------");
    }

    // Setter para la clave pública
    public static synchronized void setLlavePublica(PublicKey llave) {
        llavePublica = llave;
    }
}
