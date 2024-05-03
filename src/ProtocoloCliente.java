import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.Random;
import javax.crypto.Mac;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class ProtocoloCliente {
    private static PublicKey llavePublicaServidor;
    private static SecretKey K_AB1;
    private static SecretKey K_AB2;

    private static String iv;

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
        byte[] hashretoDescifrado = Descifrado.DescifrarAes(llavePublicaServidor, hashretoCifrado);
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

        iv = "1234567890123456";

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
        byte[] hashDescifrado = Descifrado.DescifrarAes(llavePublicaServidor, hashretoCifrado);
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

        K_AB1 = encryptionKey;
        K_AB2 = hmacKey;
    }
    public static void iniciarSesion(String login, String password, ObjectInputStream pIn, ObjectOutputStream pOut) throws Exception{

        byte[] hashlogin = generarHash(login);
        byte[] cKAB1log = Cifrado.cifradoSimetrico(K_AB1, iv, hashlogin);
        byte[] hashpassword = generarHash(password);
        byte[] cKAB1pas = Cifrado.cifradoSimetrico(K_AB1, iv, hashpassword);

        pOut.writeObject(login);

        pOut.writeObject(cKAB1log);

        pOut.writeObject(password);

        pOut.writeObject(cKAB1pas);
    }

    public static void consulta(ObjectInputStream pIn, ObjectOutputStream pOut) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchPaddingException, IOException{
        // Crear un objeto de la clase Random
        Random random = new Random();
        
        // Generar un número aleatorio entre 1 y 100 (inclusive)
        int numeroConsulta = random.nextInt(100) + 1;
        String numeroComoString = String.valueOf(numeroConsulta);

        System.out.println("\nEl usuario escribio: " + numeroComoString);
        byte[] textoClaro = numeroComoString.getBytes();
        byte[] numeroConsultaCifrado = Cifrado.cifradoSimetrico(K_AB1, iv, textoClaro);

        pOut.writeObject(numeroConsultaCifrado);

         // Crear una instancia de Mac con el algoritmo HMAC-SHA256
         Mac mac = Mac.getInstance("HmacSHA256");

          // Inicializar el objeto Mac con la llave compartida
          mac.init(K_AB2);

          // Calcular el código de autenticación HMAC para el mensaje
          byte[] hmacBytes = mac.doFinal(numeroComoString.getBytes());

          pOut.writeObject(hmacBytes);
    }
    public static void verificacionFinal(ObjectInputStream pIn, ObjectOutputStream pOut) throws ClassNotFoundException, IOException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException{
        byte[] consulta = (byte[]) pIn.readObject();
        byte[] consultaDescifrada = Descifrado.DescifrarPadding(K_AB1,iv,consulta);
        byte[] consultaDescifrada2 = Descifrado.DescifrarPadding(K_AB1,iv,consultaDescifrada);
        String texto = new String(consultaDescifrada2, StandardCharsets.UTF_8);

        // Crear una instancia de Mac con el algoritmo HMAC-SHA256
         Mac mac = Mac.getInstance("HmacSHA256");
        // Inicializar el objeto Mac con la llave compartida
        mac.init(K_AB2);
        // Calcular el HMAC
        byte[] hmacVerificacion = mac.doFinal(texto.getBytes());
        // Calcular el HMAC del HMAC
        byte[] hmacFinalLocal = mac.doFinal(hmacVerificacion);
        byte[] HMACRecibido = (byte[]) pIn.readObject();

         // Comparar los dos HMAC
         if (MessageDigest.isEqual(hmacFinalLocal, HMACRecibido)) {
            System.out.println("\nEl HMAC final coincide con el HMAC inicial. El mensaje es auténtico.");
            pOut.writeObject(texto);
        } else {
            System.out.println("\nEl HMAC final NO coincide con el HMAC inicial. El mensaje podría haber sido alterado.");
        }
        Integer respuesta = (Integer) pIn.readObject();
        System.out.println("\nRespuesta del servidor: " + respuesta);

        System.out.println("\nSe da por finalizada la conexion");


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
