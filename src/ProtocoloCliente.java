import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ProtocoloCliente {
    public static void procesar(BufferedReader stdIn, BufferedReader pIn, PrintWriter pOut) throws IOException {

        //lee del teclado
        System.out.println("Escriba el mensaje para enviar: ");
        String fromUser = stdIn.readLine();

        // Verificar si la entrada es un número entero positivo
        try {
            int numeroConsulta = Integer.parseInt(fromUser);
            // Si se llega aquí, la entrada es un número válido
            if (numeroConsulta > 0) {
                System.out.println("El usuario escribió: " + fromUser);

                pOut.println(fromUser);
                String fromServer = "";

                if ((fromServer = pIn.readLine()) != null) {
                    //El servidor debe responder con numero 1
                    System.out.println("Respuesta del servidor: " + 1);
                }

            } else {
                System.out.println("Error: El número de consulta debe ser un entero positivo. Por favor, intente de nuevo.");
            }
        } catch (NumberFormatException e) {
            // Si ocurre una excepción al intentar convertir a entero, la entrada no es un número válido
            System.out.println("Error: La entrada no es un número entero. Por favor, intente de nuevo.");
        }
    }
}
