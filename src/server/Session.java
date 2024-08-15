package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;

public class Session {
    private DataOutputStream dataOutputStream;
    private DataInputStream dataInputStream;
    private Socket socket;

    public Session(Socket socket) {
        this.socket = socket;
        try {
            this.dataOutputStream = new DataOutputStream(this.socket.getOutputStream());
            this.dataInputStream = new DataInputStream(this.socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            close();
        }
    }

    public String read() throws IOException {
        try {
            return this.dataInputStream.readUTF();
        } catch (EOFException e) {
            // Maneja el caso en el que el cliente cierra la conexión inesperadamente
            close(); // Cierra la sesión y libera los recursos
            throw e; // Propaga la excepción para que pueda ser manejada en otro lugar
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public boolean write(String data) throws IOException {
        try {
            this.dataOutputStream.writeUTF(data);
            this.dataOutputStream.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public boolean close() {
        boolean success = true;
        try {
            if (this.dataOutputStream != null) {
                this.dataOutputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            success = false;
        }
        try {
            if (this.dataInputStream != null) {
                this.dataInputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            success = false;
        }
        try {
            if (this.socket != null && !this.socket.isClosed()) {
                this.socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            success = false;
        }
        return success;
    }
}
