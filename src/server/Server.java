package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Clase que representa el servidor que acepta conexiones de clientes y maneja la comunicación con ellos.
 * Implementa la interfaz SocketProcess para definir los métodos de conexión y respuesta.
 */
public class Server implements SocketProcess {
    private ServerSocket serverSocket; // Socket del servidor para aceptar conexiones
    private ScheduledExecutorService scheduler; // Servicio para ejecutar tareas periódicas
    private List<ClientHandler> clientHandlers; // Lista de manejadores de clientes
    private volatile boolean running;

    /**
     * Constructor de Server.
     * Inicializa el ServerSocket, el programador y la lista de manejadores de clientes.
     */
    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
        this.scheduler = Executors.newScheduledThreadPool(1); // Inicializa el servicio de programación
        this.clientHandlers = new ArrayList<>(); // Inicializa la lista de manejadores de clientes
        this.running = true; // El servidor comienza en estado de true significa que esta prendido  o encendido
    }

    /**
     * Método para aceptar conexiones de clientes y manejar los hilos de los clientes.
     * Ejecuta tareas periódicas para enviar actualizaciones de la lista de clientes.
     */
    @Override
    public boolean bind() {
        System.out.println("Esperando conexiones...");

        while (running) {
            try {
                // Verifica si el servidor ha sido cerrado
                if (serverSocket.isClosed()) {
                    break;
                }

                // Acepta una nueva conexión del cliente
                Socket socket = serverSocket.accept();
                System.out.println("Conexión aceptada: " + socket.getRemoteSocketAddress());

                // Programa la tarea de enviar la lista de clientes cada 5 segundos
                scheduler.scheduleAtFixedRate(this::sendClientList, 0, 5, TimeUnit.SECONDS);

                if (socket != null) {
                    // Crea un nuevo manejador de cliente y lo inicia en un nuevo hilo
                    ClientHandler clientHandler = new ClientHandler(socket, this);
                    clientHandlers.add(clientHandler); // Añade el manejador a la lista
                    new Thread(clientHandler).start(); // Inicia el hilo del manejador
                }
            } catch (IOException e) {
                if (!running) {
                    System.out.println("ServerSocket cerrado.");
                    break;
                }
                e.printStackTrace();
            }
        }
        return true;
    }

    @Override
    public List<Object> listen() {
        return List.of(); // Método no utilizado en esta implementación
    }

    @Override
    public boolean response(List<Object> data) {
        return false; // Método no utilizado en esta implementación
    }

    /**
     * Cierra el servidor y libera los recursos.
     * @return Verdadero si se cerró exitosamente.
     */
    @Override
    public boolean close() {
        running = false; // Detiene la aceptación de nuevas conexiones
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                System.out.println("Cerrando ServerSocket...");
                serverSocket.close(); // Cierra el ServerSocket
            }
            scheduler.shutdown(); // Apaga el servicio de programación
            scheduler.awaitTermination(5, TimeUnit.SECONDS); // Espera a que todas las tareas se completen
            return true;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Envia un mensaje a todos los clientes conectados.
     * Maneja errores y elimina clientes con problemas.
     */
    public synchronized void broadcast(String message) {
        for (ClientHandler handler : clientHandlers) {
            try {
                handler.enqueueMessage(message); // Encola el mensaje para el cliente
            } catch (Exception e) {
                System.err.println("Error al encolar el mensaje para el cliente: " + handler.getClientName());
                e.printStackTrace();
                // Aquí puedes optar por registrar el error, notificar al cliente o realizar otras acciones si es necesario
            }
        }
    }


    /**
     * Elimina un cliente de la lista de clientes conectados.
     */
    public synchronized void removeClient(ClientHandler clientHandler) {
        clientHandlers.remove(clientHandler); // Elimina al cliente de la lista
        System.out.println("Cliente removido: " + clientHandler.getClientName());
    }

    /**
     * Envía la lista de todos los clientes conectados a todos los clientes.
     * Se ejecuta periódicamente para mantener actualizada la lista de clientes.
     */
    private synchronized void sendClientList() {
        if (clientHandlers.size() > 0) {
            StringBuilder clientList = new StringBuilder("Lista_clientes:"); // Inicializa la lista de clientes
            for (ClientHandler handler : clientHandlers) {
                clientList.append(handler.getClientName()).append("\n"); // Agrega el nombre del cliente a la lista
            }
            System.out.println(clientList);
            broadcast(clientList.toString()); // Envía la lista de clientes a todos los clientes con prefijo
        }
    }
}
