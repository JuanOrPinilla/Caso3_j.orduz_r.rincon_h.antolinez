import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import java.security.SecureRandom;
import java.math.BigInteger;


public class Cliente {
    public static final int PUERTO = 3400;
	public static final String SERVIDOR = "localhost";

	// Caracteres válidos para generar el nombre de usuario y la contraseña
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
	
	public static void main(String[] args) throws Exception {
		
		Socket socket = null;
		ObjectOutputStream escritor = null;
		ObjectInputStream lector = null;
		
		System.out.println("Comienza cliente");
		
		try {
			socket = new Socket(SERVIDOR, PUERTO);
			escritor = new ObjectOutputStream(socket.getOutputStream());
			lector = new ObjectInputStream(socket.getInputStream());
		}
		catch (Exception e) {
			e.printStackTrace();
            System.exit(-1);
		}
		
		BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
		
		// Crear una instancia de SecureRandom
        SecureRandom secureRandom = new SecureRandom();
        // Generar un número aleatorio grande
        Integer numeroAleatorio = secureRandom.nextInt();
		String reto = String.valueOf(numeroAleatorio);

		ProtocoloCliente.recibirllave(lector);
		ProtocoloCliente.verificaReto(lector,escritor,reto);
		ProtocoloCliente.diffieHelman(lector, escritor);

		// Longitud deseada para el nombre de usuario y la contraseña
        int usernameLength = 8;
        int passwordLength = 12;

        // Generar nombre de usuario y contraseña
        String username = generateRandomString(usernameLength);
        String password = generateRandomString(passwordLength);

		ProtocoloCliente.iniciarSesion(username,password,lector, escritor);
		ProtocoloCliente.consulta(lector, escritor);
		ProtocoloCliente.verificacionFinal(lector, escritor);
		
		escritor.close();
		lector.close();
		socket.close();
		stdIn.close();
	}

	// Método para generar una cadena aleatoria de longitud dada
    private static String generateRandomString(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            char randomChar = CHARACTERS.charAt(randomIndex);
            sb.append(randomChar);
        }
        return sb.toString();
    }
}

