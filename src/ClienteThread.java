public class ClienteThread extends Thread {
    @Override
    public void run() {
        try {
            Cliente cliente = new Cliente();
            cliente.main(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}