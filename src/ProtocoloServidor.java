import java.io.BufferedReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ProtocoloServidor {

    public static PublicKey llavePublica;
    private static PrivateKey llavePrivada;

    public static synchronized void enviarLlavePublica(ObjectOutputStream pOut) throws IOException{
        pOut.writeObject(llavePublica);
    }
    
    public static synchronized void reto(ObjectInputStream pIn, ObjectOutputStream pOut) throws IOException, ClassNotFoundException{
        String reto;
        reto = (String) pIn.readObject();
        System.out.println("Reto recibido: " + reto);

        byte[] retoCifrado = Cifrado.C_kPrivate(reto, llavePrivada);
        
        pOut.writeObject(retoCifrado);
        System.out.println("salida procesada: ");
        imprimir(retoCifrado);
    }

    public static synchronized void procesar(ObjectInputStream pIn, ObjectOutputStream pOut) throws IOException, ClassNotFoundException  {
        String inputLine;
        String outputLine;
        inputLine = (String) pIn.readObject();
        System.out.println("Entrada a procesar: " + inputLine);

        outputLine = inputLine;

        pOut.writeObject(outputLine);
        System.out.println("salida procesada: " + outputLine);

    }

    public static void setPublicKey(PublicKey llave){
        llavePublica = llave;
    }

    public static void setPrivateKey(PrivateKey llave){
        llavePrivada = llave;
    }

    public static void imprimir (byte[] contenido){
        int i = 0;
        for (; i< contenido.length -1; i++){
            System.out.print(contenido[i] + " ");
        }
        System.out.println(contenido[i] + " ");
    }
    
}
