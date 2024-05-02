import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class ProtocoloCliente {
    private static PublicKey llavePublicaServidor;

    public static void recibirllave(ObjectInputStream pIn) throws ClassNotFoundException, IOException{
        //obtener llave pública del servidor
        llavePublicaServidor =  (PublicKey) pIn.readObject();
    }

    public static void verificaReto(ObjectInputStream pIn, ObjectOutputStream pOut,String numeroAleatorio) throws Exception {
        pOut.writeObject(numeroAleatorio);
        System.out.println("\nEl usuario ha enviado el reto: " + numeroAleatorio + "\n");

        //obtiene del servidor el h(m) cifrado
        byte[] hashretoCifrado = (byte[]) pIn.readObject();
        //obtener el hash local
        byte[] hashLocal = generarHash(numeroAleatorio);
        //descifra el h(m) mandado por el servidor
        byte[] hashretoDescifrado = Descifrado.Descifrar(llavePublicaServidor, hashretoCifrado);
        //traduce el reto descrifrado a string
        imprimir(hashretoDescifrado);
        imprimir(hashLocal);
        // Comparar si los dos arrays son iguales
        boolean sonIguales = Arrays.equals(hashLocal, hashretoDescifrado);
        //si el reto enviado y la verificación no es correcta el programa acaba
        if(sonIguales == false){
            System.out.println("ERROR");
            System.exit(0); // Terminar el programa
        }
        System.out.println("\nOK: El usuario ha verificado el servidor correctamente ");
    }

    private static byte[] generarHash(String mensaje) throws Exception {
    // Obtener una instancia del algoritmo de hash SHA-256
    MessageDigest digest = MessageDigest.getInstance("SHA-256");

    // Calcular el hash del mensaje
    byte[] hashBytes = digest.digest(mensaje.getBytes());

    return hashBytes;
}

public static void diffieHelman(ObjectInputStream pIn, ObjectOutputStream pOut) throws IOException, ClassNotFoundException{
    // Crear una instancia de la clase Random
    Random random = new Random();
    // Generar un número aleatorio entre 0 y 20 (individuo)
    int x = random.nextInt(20);

    int yServidor = (int) pIn.readObject();

}

    public static void imprimir (byte[] contenido){
        int i = 0;
        for (; i< contenido.length -1; i++){
            System.out.print(contenido[i] + " ");
        }
        System.out.println(contenido[i] + " ");
    }
}
