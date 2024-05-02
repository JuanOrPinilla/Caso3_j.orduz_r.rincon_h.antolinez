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

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Random;

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

        //PARA LA VERIFICACiON
        String concatenado = p + g;
        //Generar el hash del mensaje (H(m))
        byte[] hash = generarHash(concatenado);
        byte[] CifradoDiffie = Cifrado.C_kPrivateDirecto(hash, llavePrivada);
        pOut.writeObject(CifradoDiffie);
        

        BigInteger yServidor = gxnumBigInt.mod(PdecimalValue);

        //envia al cliente
        pOut.writeObject(yServidor);
        //recibe del cliente
        BigInteger yCliente = (BigInteger) pIn.readObject();

        BigInteger gyx = yCliente.pow(x);
        BigInteger LlaveMaestra = gyx.mod(PdecimalValue);
        System.out.println("Shared secret:" + LlaveMaestra);



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

    // Generar un número primo aleatorio entre 2^10 y 2^20
    private static int generarNumeroPrimo() {
        Random random = new Random();
        int min = 1 << 10; // 2^10
        int max = 1 << 20; // 2^20
        int p;
        do {
            p = random.nextInt(max - min + 1) + min;
        } while (!esPrimo(p));
        return p;
    }

    // Verificar si un número es primo
    private static boolean esPrimo(int n) {
        if (n <= 1) {
            return false;
        }
        for (int i = 2; i * i <= n; i++) {
            if (n % i == 0) {
                return false;
            }
        }
        return true;
    }

    // Generar un número aleatorio menor que p y mayor que 1
    private static int generarBase(int p) {
        Random random = new Random();
        return random.nextInt(p - 1) + 1;
    }
}
    
