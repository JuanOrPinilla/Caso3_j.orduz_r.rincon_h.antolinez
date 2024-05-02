import java.io.BufferedReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ProtocoloServidor {

    public static PublicKey llavePublica;
    private static PrivateKey llavePrivada;

    public static void enviarLlavePublica(ObjectOutputStream pOut) throws IOException{
        pOut.writeObject(llavePublica);
    }
    
    public static void reto(ObjectInputStream pIn, ObjectOutputStream pOut) throws Exception{
        String reto;
        reto = (String) pIn.readObject();
        System.out.println("Reto recibido: " + reto);

        //Generar el hash del mensaje (H(m))
        byte[] hash = generarHash(reto);
        byte[] retoCifrado = Cifrado.C_kPrivateDirecto(hash, llavePrivada);
        
        pOut.writeObject(retoCifrado);
        System.out.println("salida procesada: ");
        imprimir(retoCifrado);
    }
    
    private static byte[] generarHash(String mensaje) throws Exception {
    // Obtener una instancia del algoritmo de hash SHA-256
    MessageDigest digest = MessageDigest.getInstance("SHA-256");

    // Calcular el hash del mensaje
    byte[] hashBytes = digest.digest(mensaje.getBytes());

    return hashBytes;
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
