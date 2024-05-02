import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;

public class ProtocoloCliente {
    private static PublicKey llavePublicaServidor;

    public static void recibirllave(ObjectInputStream pIn) throws ClassNotFoundException, IOException{
        llavePublicaServidor =  (PublicKey) pIn.readObject();
        System.out.println("El usuario ha recibido la llave pública del servidor");


    }
    public static void reto(ObjectInputStream pIn, ObjectOutputStream pOut,String numeroAleatorio) throws IOException, ClassNotFoundException {
        pOut.writeObject(numeroAleatorio);
        System.out.println("El usuario ha enviado el reto: " + numeroAleatorio);

        //obtiene del servidor el reto cifrado
        byte[] retoCifrado = (byte[]) pIn.readObject();
        //descifra el reto mandado por el servidor
        byte[] retoDescifrado = Descifrado.Descifrar(llavePublicaServidor, retoCifrado);
        //traduce el reto descrifrado a string
        String descifradoClaro = new String(retoDescifrado, StandardCharsets.UTF_8);

        System.out.println("El usuario ha verificado el reto dando resultado: " + descifradoClaro);

        boolean sonIguales = descifradoClaro.equals(numeroAleatorio);
        //si el reto enviado y la verificación no es correcta el programa acaba
        if(sonIguales == false){
            System.out.println("ERROR");
            System.exit(0); // Terminar el programa
        }
        else{
            System.out.println("OK");
        }

        //verificar el reto
    }

    public static void procesar(BufferedReader stdIn, ObjectInputStream pIn, ObjectOutputStream pOut) throws IOException, ClassNotFoundException {

        //lee del teclado
        System.out.println("Escriba el mensaje para enviar: ");
        String fromUser = (String) pIn.readObject();

        // Verificar si la entrada es un número entero positivo
        try {
            int numeroConsulta = Integer.parseInt(fromUser);
            // Si se llega aquí, la entrada es un número válido
            if (numeroConsulta > 0) {
                System.out.println("El usuario escribió: " + fromUser);

                pOut.writeObject(fromUser);
                String fromServer;

                if ((fromServer = (String) pIn.readObject()) != null) {
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

    public static void imprimir (byte[] contenido){
        int i = 0;
        for (; i< contenido.length -1; i++){
            System.out.print(contenido[i] + " ");
        }
        System.out.println(contenido[i] + " ");
    }
}
