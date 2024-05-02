public class MultiplesClientes {
    public static void main(String[] args) {
        final int NUM_CLIENTES = 50;

        for (int i = 0; i < NUM_CLIENTES; i++) {
            Thread clienteThread = new ClienteThread();
            clienteThread.start();
        }
    }
}