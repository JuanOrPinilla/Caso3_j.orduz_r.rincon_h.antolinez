import java.util.Scanner;

public class multiplesClientes {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Ingrese el número de clientes que desea ejecutar: ");
        int veces = scanner.nextInt();
        scanner.close();
        
        for (int i = 0; i < veces; i++) {
            Cliente.main(args);
        }
    }
}
