package server;

import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Clase que maneja la comunicación con un cliente específico en el servidor.
 * Implementa la interfaz Runnable para permitir la ejecución en un hilo separado.
 */
public class ClientHandler implements Runnable {
    private Socket socket; // Socket para la conexión con el cliente
    private Session session; // Sesión para leer y escribir datos
    private Server server; // Referencia al servidor que maneja esta conexión
    private String clientName; // Nombre del cliente
    private Thread readThread; // Hilo para la lectura de mensajes
    private Thread writeThread; // Hilo para la escritura de mensajes
    private BlockingQueue<String> messageQueue; // Cola para mensajes salientes

    /**
     * Constructor de ClientHandler.
     * Inicializa el socket, el servidor, la sesión y la cola de mensajes.
     */
    public ClientHandler(Socket socket, Server server) {
        this.socket = Objects.requireNonNull(socket, "Socket no puede ser nulo");
        this.server = Objects.requireNonNull(server, "Server no puede ser nulo");
        this.session = new Session(socket); // Inicializar la sesión con el socket
        this.messageQueue = new LinkedBlockingQueue<>(); // Inicializar la cola de mensajes

        System.out.println("Cliente conectado desde: " + socket.getRemoteSocketAddress());
    }

    /**
     * Método ejecutado cuando se inicia el hilo para manejar al cliente.
     * Inicializa y arranca los hilos de lectura y escritura.
     * metodo runnable que se ejecuta para cada cliente en un hilo separado
     */
    @Override
    public void run() {
        try {
            requestClientName(); // Solicita el nombre del cliente

            // Crear y arrancar los hilos para leer y escribir mensajes
            readThread = new Thread(this::readMessages);
            writeThread = new Thread(this::writeMessages);

            readThread.start();
            writeThread.start();

            // Esperar a que ambos hilos terminen
            readThread.join();
            writeThread.join();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Restaurar el estado de interrupción
            System.err.println("Hilo interrumpido: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeConnection(); // Cerrar la conexión al final
        }
    }

    /**
     * Hilo encargado de leer mensajes del cliente.
     * Reacciona a la interrupción y errores de entrada/salida.
     */
    private void readMessages() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                // Leer un mensaje del cliente
                String message = session.read();
                if (message != null) {
                    System.out.println(clientName + ": " + message);
                    // Enviar el mensaje a todos los clientes conectados
                    server.broadcast(clientName + ": " + message);
                }
            }
        } catch (EOFException e) {
            System.out.println("El cliente cerró la conexión.");
        } catch (IOException e) {
            System.err.println("Error al leer el mensaje del cliente: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeConnection(); // Cerrar la conexión en caso de error
        }
    }

    /**
     * Hilo encargado de escribir mensajes al cliente.
     * Reacciona a la interrupción y errores de entrada/salida.
     */
    private void writeMessages() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                // Obtener y enviar el siguiente mensaje de la cola
                String message = messageQueue.take();
                session.write(message);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Restaurar el estado de interrupción
            System.err.println("Hilo de escritura interrumpido: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Error al enviar el mensaje al cliente: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeConnection(); // Cerrar la conexión en caso de error
        }
    }

    /**
     * Solicita el nombre del cliente y lo almacena en clientName.
     */
    private void requestClientName() {
        try {
            // Leer el nombre del cliente
            String nameRequest = session.read();

            if (nameRequest != null) {
                clientName = nameRequest; // Almacenar el nombre del cliente
                System.out.println("Nombre del cliente recibido: " + clientName);
            } else {
                System.err.println("No se recibió nombre del cliente.");
            }
        } catch (Exception e) {
            System.err.println("Error al solicitar el nombre del cliente: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Cierra la conexión con el cliente y elimina al cliente del servidor.
     * Maneja posibles errores durante el cierre.
     */
    public void closeConnection() {
        try {
            if (session.close()) {
                System.out.println("Sesión cerrada para: " + socket.getRemoteSocketAddress());
                server.removeClient(this); // Notificar al servidor que el cliente ha sido desconectado
            } else {
                System.err.println("Error al cerrar la sesión para: " + socket.getRemoteSocketAddress());
            }
        } catch (Exception e) {
            System.err.println("Error al cerrar la conexión: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Devuelve el nombre del cliente.
     * @return Nombre del cliente.
     */
    public String getClientName() {
        return clientName;
    }

    /**
     * Encola un mensaje para enviar al cliente.
     */
    public void enqueueMessage(String message) {
        messageQueue.offer(message); // Añadir el mensaje a la cola de mensajes
    }
}
