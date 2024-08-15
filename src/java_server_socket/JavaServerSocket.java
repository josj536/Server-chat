package java_server_socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Clase para crear un ServerSocket con un puerto específico
 * y un número máximo de clientes que puede manejar.
 */
public class JavaServerSocket {
    // Puerto en el que el servidor escuchará las conexiones entrantes.
    private int port;
    // Número máximo de clientes que el servidor puede manejar simultáneamente.
    private int amountClients;

    /**
     * Constructor de JavaServerSocket.
     * Inicializa el puerto y el número máximo de clientes que el servidor puede manejar.
     */
    public JavaServerSocket(int port, int amountClients) {
        this.port = port;
        this.amountClients = amountClients;
    }

    /**
     * Intenta crear y devolver un nuevo ServerSocket.
     * Si ocurre un error durante la creación, se registra una advertencia y se devuelve null.
     */
    public ServerSocket get() {
        try {
            // Crea una nueva instancia de ServerSocket en el puerto especificado
            // con el número máximo de clientes que puede manejar.
            return new ServerSocket(this.port, this.amountClients);
        } catch (IOException e) {
            // Registra un mensaje de advertencia en caso de que ocurra una excepción
            // durante la creación del ServerSocket.
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, e.getMessage(), e);
            // Devuelve null si hubo un error al crear el ServerSocket.
            return null;
        }
    }
}
