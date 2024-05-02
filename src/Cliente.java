import java.io.BufferedReader;
import java.io.IOException;
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
	
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		
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
		ProtocoloCliente.reto(lector,escritor,reto);
		ProtocoloCliente.procesar(stdIn,lector,escritor);
		
		escritor.close();
		lector.close();
		socket.close();
		stdIn.close();
	}
}

