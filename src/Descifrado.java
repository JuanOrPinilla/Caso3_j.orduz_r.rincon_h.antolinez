import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

public class Descifrado {

    private final static String AES = "RSA";
    private final static String PADDING = "AES/CBC/PKCS5Padding";

    public static byte[] DescifrarAes(Key llave,byte[] texto){
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

     public static byte[] DescifrarPadding(SecretKey llave, String iv, byte[] texto) throws IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        byte[] textoClaro;
        Cipher cipher = Cipher.getInstance(PADDING);
        SecretKey skeySpec = llave;
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes());
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, ivParameterSpec);
        textoClaro = cipher.doFinal(texto);
        return textoClaro;
    }
    
}
