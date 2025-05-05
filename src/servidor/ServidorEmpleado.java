package servidor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

import modelo.Empleado;
import modelo.Reunion;

public class ServidorEmpleado implements Runnable {
    private Empleado empleado;
    private int puerto;

    public ServidorEmpleado(Empleado emp, int puerto) {
        this.empleado = emp;
        this.puerto = puerto;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(puerto)) {
            System.out.println("Servidor de " + empleado.getNombre() + " escuchando en puerto " + puerto);
            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(() -> {
                    try (ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
                        Reunion r = (Reunion) in.readObject();
                        empleado.notificarCambio(r);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}