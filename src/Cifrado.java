
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class Cifrado {
    private final static String AES = "RSA";
    private final static String PADDING = "AES/CBC/PKCS5Padding";

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

    public static byte[] C_kPrivateDirecto( byte[] textoClaro,PrivateKey llave ){
        byte[] retoCifrado;  

        try{
            Cipher cifrador = Cipher.getInstance(AES);

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

    public static byte[] cifradoSimetrico(SecretKey llave, String iv,byte[] texto) throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchPaddingException{
        byte[] datoCifrado;
        Cipher cipher = Cipher.getInstance(PADDING);
        SecretKey skeySpec = llave;
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes());
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivParameterSpec);
        datoCifrado = cipher.doFinal(texto);
        return datoCifrado;
    }
    
}
