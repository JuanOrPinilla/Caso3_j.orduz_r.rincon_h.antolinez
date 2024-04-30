import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

public class LlavesServidor {

    private final static String ALGORITMO = "RSA";

    public static KeyPair generadorPar() throws NoSuchAlgorithmException{

        KeyPairGenerator generator = KeyPairGenerator.getInstance(LlavesServidor.ALGORITMO);
        generator.initialize(1024);
        KeyPair keyPair = generator.generateKeyPair();
        return keyPair;
    }

    public static PublicKey generadorLlavePublica(KeyPair keyPair){
        PublicKey llavePublica = keyPair.getPublic();
        return llavePublica;
    }

    public static PrivateKey generadorLlavePrivada(KeyPair keyPair){
        PrivateKey llavePrivada = keyPair.getPrivate();
        return llavePrivada;
    }
    
}
