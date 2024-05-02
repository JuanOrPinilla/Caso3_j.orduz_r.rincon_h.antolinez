import javax.crypto.Cipher;

import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;

public class Descifrado {

    private final static String AES = "RSA";

    public static byte[] Descifrar(Key llave,byte[] texto){
        byte[] textoClaro;

        try{
            Cipher cifrador = Cipher.getInstance(AES);
            
            cifrador.init(Cipher.DECRYPT_MODE, llave);
            textoClaro = cifrador.doFinal(texto);
            return textoClaro;
        } catch (Exception e){
            System.out.println("Exception: " + e.getMessage());
            return null;
        }
        

    }
    
}
