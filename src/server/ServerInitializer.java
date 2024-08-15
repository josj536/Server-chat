package server;

import java_server_socket.JavaServerSocket; // Importa la clase JavaServerSocket, presumiblemente personalizada para manejar la creación de sockets.
import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Clase que inicializa y gestiona el servidor.
 * Se encarga de crear y controlar el ServerSocket y el servidor.
 */
public class ServerInitializer {
    private int port; // Puerto en el que el servidor escuchará conexiones entrantes
    private int maxClients; // Número máximo de clientes que el servidor puede manejar simultáneamente
    private ExecutorService threadPool; // Pool de hilos para manejar la ejecución concurrente
    private Server server; // Instancia del servidor que maneja la lógica de conexión y comunicación
    private volatile boolean running; // Indicador de si el servidor está en funcionamiento

    /**
     * Constructor de ServerInitializer.
     * Inicializa el puerto, el número máximo de clientes y el pool de hilos..
     */
    public ServerInitializer(int port, int maxClients) {
        this.port = port;
        this.maxClients = maxClients;
        this.threadPool = Executors.newFixedThreadPool(maxClients); // Inicializa el pool de hilos con un tamaño fijo
        this.running = false; // El servidor comienza en estado detenido
    }

    /**
     * Inicia el servidor.
     * Crea un JavaServerSocket, inicializa la instancia de Server y comienza a aceptar conexiones.
     * Utiliza el pool de hilos para ejecutar el método bind del servidor.
     * Esto permite que el servidor acepte conexiones y maneje múltiples tareas concurrentemente o clientes
     * sin bloquear el hilo principal de la aplicación.
     */
    public void startServer() {
        if (running) {
            System.out.println("El servidor ya está en funcionamiento.");
            return;
        }

        System.out.println("Iniciando Java Server Socket");

        // Crea un JavaServerSocket personalizado (presumiblemente maneja la configuración del ServerSocket)
        JavaServerSocket javaServerSocket = new JavaServerSocket(port, maxClients);
        ServerSocket serverSocket = javaServerSocket.get();

        if (serverSocket == null) {
            System.err.println("No se pudo crear ServerSocket.");
            return;
        }

        // Inicializa la instancia del servidor con el ServerSocket
        server = new Server(serverSocket);
        running = true;

        // Ejecuta el método bind del servidor en un hilo del pool de hilos.
        // Esto permite que el servidor maneje conexiones concurrentemente y realice tareas
        // sin bloquear el hilo principal de la aplicación.
        threadPool.execute(() -> server.bind());
    }

    /**
     * Detiene el servidor.
     * Cierra el ServerSocket, apaga el pool de hilos y sale de la aplicación.
     */
    public void stopServer() {
        if (!running) {
            System.out.println("El servidor no está en funcionamiento.");
            return;
        }

        running = false;

        if (server != null) {
            server.close(); // Cierra el servidor
        }
        if (threadPool != null) {
            threadPool.shutdown(); // Apaga el pool de hilos
        }

        System.exit(0); // Sale de la aplicación
    }

    /**
     * Verifica si el servidor está en funcionamiento.
     */
    public boolean isRunning() {
        return running;
    }
}
