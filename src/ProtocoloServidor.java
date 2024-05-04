
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class ProtocoloServidor {

    public static PublicKey llavePublica;
    private static PrivateKey llavePrivada;
    private static SecretKey llaveAsimetricaCifrar;
    private static SecretKey llaveAsimetricaMac;
    private static String iv;

    public static void enviarLlavePublica(ObjectOutputStream pOut) throws IOException{
        pOut.writeObject(llavePublica);
    }
    
    public static void reto(ObjectInputStream pIn, ObjectOutputStream pOut) throws Exception{
        //Generar firma--------------------------------------------------
        String reto;
        reto = (String) pIn.readObject();
        System.out.println("Reto recibido: " + reto);

        //Generar el hash del mensaje (H(m))
        byte[] hash = generarHash(reto);
        byte[] retoCifrado = Cifrado.C_kPrivateDirecto(hash, llavePrivada);
        
        pOut.writeObject(retoCifrado);
        //-----------------------------------------------------------------
    }

    public static void diffieHelman(ObjectInputStream pIn, ObjectOutputStream pOut) throws Exception{
        // Generar un número primo aleatorio p
        String p = "00c0689e42e90fd7caf07d2e3c20a9ac9e4992b75f4b2033279ced983585fcbcbcc30f93bc57f8f11f9c6e905f016d813b076786e1630fb2902bc264560d9539b475a078f1a02d76c635365a3cadbd75659112a7abf318340fde265c7e0d2a184f223dd997a4f56c866e9a1176c232a826fc4845b4432aec7fe8dbb1ed2c429fa7";
        String g = "2";
        iv = "1234567890123456";

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

        //CALCULO DE LLAVES
        byte[] digestBytes = generarHash(LlaveMaestra.toString());

        // Dividir el digest en dos mitades
        int halfLength = digestBytes.length / 2;

        //GENERAR LLAVE SIMETRICA PARA CIFRAR K_AB1
        byte[] encryptionKeyBytes = Arrays.copyOfRange(digestBytes, 0, halfLength); // Primeros 256 bits

        //GENERAR LLAVE SIMETRICA PARA MAC K_AB2
        byte[] hmacKeyBytes = Arrays.copyOfRange(digestBytes, halfLength, digestBytes.length); // Últimos 256 bits

        // Crear una instancia de la clave secreta utilizando AES
        SecretKey encryptionKey = new SecretKeySpec(encryptionKeyBytes, "AES");
        // Crear una instancia de la clave secreta utilizando SecretKeySpec
        SecretKey hmacKey = new SecretKeySpec(hmacKeyBytes, "HmacSHA256");

        llaveAsimetricaCifrar = encryptionKey;
        llaveAsimetricaMac = hmacKey;
    }

    public static void iniciarSesion(ObjectInputStream pIn, ObjectOutputStream pOut) throws Exception{
        
        //para el login
        String login = (String) pIn.readObject();

        byte[] cKAB1log = (byte[]) pIn.readObject();

        byte[] cKAB1logDescifrado = Descifrado.DescifrarPadding(llaveAsimetricaCifrar,iv,cKAB1log);
        
        byte[] hashLocallog = generarHash(login);

        imprimir(hashLocallog);
        imprimir(cKAB1logDescifrado);
         // Comparar si los dos arrays son iguales
         boolean sonIguales = Arrays.equals(hashLocallog, cKAB1logDescifrado);
         //si el reto enviado y la verificación no es correcta el programa acaba
         if(sonIguales == false){
             System.out.println("ERROR");
             System.exit(0); // Terminar el programa
         }
         System.out.println("OK: Verificar (Paso 16 Usuario)\n");

        //para la contraseña
        String password = (String) pIn.readObject();

        byte[] cKAB1pass = (byte[]) pIn.readObject();

        byte[] cKAB1passDescifrado = Descifrado.DescifrarPadding(llaveAsimetricaCifrar,iv,cKAB1pass);
        
        byte[] hashLocalpas = generarHash(password);

        imprimir(hashLocallog);
        imprimir(cKAB1logDescifrado);

         // Comparar si los dos arrays son iguales
         sonIguales = Arrays.equals(hashLocalpas, cKAB1passDescifrado);
         //si el reto enviado y la verificación no es correcta el programa acaba
         if(sonIguales == false){
             System.out.println("ERROR");
             System.exit(0); // Terminar el programa
         }
         System.out.println("OK: Verificar (Paso 16 Contraseña)");
    }

    public static void consulta(ObjectInputStream pIn, ObjectOutputStream pOut) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchPaddingException, IOException, ClassNotFoundException{
        //Desicfrar la consulta-----------------------------------------------------------------------------
        byte[] cKAB1Consulta = (byte[]) pIn.readObject();
        byte[] cKAB1ConsultaCKAB1 = Cifrado.cifradoSimetrico(llaveAsimetricaCifrar, iv, cKAB1Consulta);
        byte[] cKAB2MAC = (byte[]) pIn.readObject();

        // Crear una instancia de Mac con el algoritmo HMAC-SHA256
        Mac mac = Mac.getInstance("HmacSHA256");

        // Inicializar el objeto Mac con la llave compartida
        mac.init(llaveAsimetricaMac);

        // Calcular el código de autenticación HMAC para el mensaje
        byte[] hmacBytes2 = mac.doFinal(cKAB2MAC);

        pOut.writeObject(cKAB1ConsultaCKAB1);
        pOut.writeObject(hmacBytes2);
        //--------------------------------------------------------------------------------------------------
    }

    public static void verificacionFinal(ObjectInputStream pIn, ObjectOutputStream pOut) throws ClassNotFoundException, IOException{
        String consulta = (String) pIn.readObject();

        // Convertir el string a un entero
        int numero = Integer.parseInt(consulta);

        // Restar 1 al número
        numero--;

        // Imprimir el resultado
        System.out.println("Respuesta del servidor: " + numero);
        pOut.writeObject(numero);
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

    public static byte[] convertirIVStringABytes(String ivString) {
        int longitud = ivString.length();
        byte[] ivBytes = new byte[longitud / 2];
    
        for (int i = 0; i < longitud; i += 2) {
            ivBytes[i / 2] = (byte) ((Character.digit(ivString.charAt(i), 16) << 4)
                                 + Character.digit(ivString.charAt(i+1), 16));
        }
    
        return ivBytes;
    }
}
    
