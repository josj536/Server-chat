// esta es la interfaz que  define un conjunto de métodos relacionados con el manejo de conexiones
// de sockets. Las clases que implementan esta interfaz se encargarán de implementar la lógica
// para enlazar (bind), escuchar (listen), responder (response), y cerrar (close) un socket de red.

package server;

import java.util.List;

public interface SocketProcess {
    // Este método probablemente se utilizará para enlazar un ServerSocket a un puerto, preparándolo para aceptar conexiones entrantes.
    public boolean bind();
    //esperará a que lleguen conexiones entrantes y devolverá una lista de objetos, que pueden representar mensajes recibidos o conexiones.
    public List<Object> listen();
    //enviará una respuesta a un cliente utilizando los datos proporcionados.
    public boolean response(List<Object> data);
    //cerrará el socket, liberando cualquier recurso asociado.
    public boolean close();
}

