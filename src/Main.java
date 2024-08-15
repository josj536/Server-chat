import interfaz.ServerControlGUI;
import server.ServerInitializer;

/**
 * Clase principal del programa.
 */
public class Main {
    public static void main(String[] args) {
        int port = 2000;
        int maxClients = 100;

        ServerInitializer serverInitializer = new ServerInitializer(port, maxClients);
        ServerControlGUI serverControlGUI = new ServerControlGUI(serverInitializer);
        serverControlGUI.setVisible(true);
    }
}