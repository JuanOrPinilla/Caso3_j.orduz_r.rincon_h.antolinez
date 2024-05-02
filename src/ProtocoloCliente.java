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
        System.out.println("\nEl usuario ha enviado el reto: " + numeroAleatorio);

        //obtiene del servidor el h(m) cifrado
        byte[] hashretoCifrado = (byte[]) pIn.readObject();
        //obtener el hash local
        byte[] hashLocal = generarHash(numeroAleatorio);
        //descifra el h(m) mandado por el servidor
        byte[] hashretoDescifrado = Descifrado.Descifrar(llavePublicaServidor, hashretoCifrado);
        //traduce el reto descrifrado a string

        //imprimir(hashretoDescifrado);
        //imprimir(hashLocal);

        // Comparar si los dos arrays son iguales
        boolean sonIguales = Arrays.equals(hashLocal, hashretoDescifrado);
        //si el reto enviado y la verificación no es correcta el programa acaba
        if(sonIguales == false){
            System.out.println("ERROR");
            System.exit(0); // Terminar el programa
        }
        System.out.println("\nOK: Verificar (Paso 4)");
    }


    public static void diffieHelman(ObjectInputStream pIn, ObjectOutputStream pOut) throws Exception{
        // Generar un número primo aleatorio p
        String p = "00c0689e42e90fd7caf07d2e3c20a9ac9e4992b75f4b2033279ced983585fcbcbcc30f93bc57f8f11f9c6e905f016d813b076786e1630fb2902bc264560d9539b475a078f1a02d76c635365a3cadbd75659112a7abf318340fde265c7e0d2a184f223dd997a4f56c866e9a1176c232a826fc4845b4432aec7fe8dbb1ed2c429fa7";
        String g = "2";

        // Parse hexadecimal string to long
        BigInteger PdecimalValue = new BigInteger(p, 16);

        // Crear una instancia de la clase Random
        Random random = new Random();

        // Generar un número aleatorio entre 0 y 20 (individuo)
        int x = random.nextInt(20);

        double gxnum = Math.pow(Integer.parseInt(g),x);
        BigInteger gxnumBigInt = BigInteger.valueOf((long) gxnum);

        BigInteger yCliente = gxnumBigInt.mod(PdecimalValue);

        //Para verificacion
        String concatenado = p + g;
        //obtiene del servidor el h(m) cifrado
        byte[] hashretoCifrado = (byte[]) pIn.readObject();
        //obtener el hash local
        byte[] hashLocal = generarHash(concatenado);
        //descifra el h(m) mandado por el servidor
        byte[] hashDescifrado = Descifrado.Descifrar(llavePublicaServidor, hashretoCifrado);
        // Comparar si los dos arrays son iguales
        boolean sonIguales = Arrays.equals(hashLocal, hashDescifrado);
        //si el reto enviado y la verificación no es correcta el programa acaba
        if(sonIguales == false){
            System.out.println("ERROR");
            System.exit(0); // Terminar el programa
        }
        System.out.println("\nOK: Verificar (Paso 8)\n");
        //envia al servidor
        pOut.writeObject(yCliente);
        
        //recibe del servidor
        BigInteger yServidor = (BigInteger) pIn.readObject();

        BigInteger gxy = yServidor.pow(x);
        BigInteger LlaveMaestra = gxy.mod(PdecimalValue);
        System.out.println("Shared secret:" + LlaveMaestra);

        //TODO: GENERAR LLAVE SIMETRICA PARA CIFRAR K_AB1
        //TODO: GENERAR LLAVE SIMETRICA PARA MAC K_AB2
    }

    private static byte[] generarHash(String mensaje) throws Exception {
        // Obtener una instancia del algoritmo de hash SHA-256
        MessageDigest digest = MessageDigest.getInstance("SHA-256");

        // Calcular el hash del mensaje
        byte[] hashBytes = digest.digest(mensaje.getBytes());

        return hashBytes;
    }

    public static void imprimir (byte[] contenido){
        int i = 0;
        for (; i< contenido.length -1; i++){
            System.out.print(contenido[i] + " ");
        }
        System.out.println(contenido[i] + " ");
    }

}
