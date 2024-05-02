
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.Cipher;

public class Cifrado {
    private final static String AES = "RSA";

    public static byte[] C_kPrivate(String texto,PrivateKey llave ){
        byte[] retoCifrado;  

        try{
            Cipher cifrador = Cipher.getInstance(AES);
            byte[] textoClaro = texto.getBytes();

            cifrador.init(Cipher.ENCRYPT_MODE, llave);
            retoCifrado = cifrador.doFinal(textoClaro);
            return retoCifrado;
        } catch (Exception e){
            System.out.println("Exception: " + e.getMessage());
            return null;
        }
    }

    public static byte[] C_kPublic(String texto,PublicKey llave ){
        byte[] retoCifrado;  

        try{
            Cipher cifrador = Cipher.getInstance(AES);
            byte[] textoClaro = texto.getBytes();

            cifrador.init(Cipher.ENCRYPT_MODE, llave);
            retoCifrado = cifrador.doFinal(textoClaro);
            return retoCifrado;
        } catch (Exception e){
            System.out.println("Exception: " + e.getMessage());
            return null;
        }
    }
    
}
