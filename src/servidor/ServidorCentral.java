package servidor;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

import modelo.Mediador;
import modelo.Reunion;

public class ServidorCentral implements Mediador {
    private Map<String, Integer> mapaPuertos;

    public ServidorCentral(String archivoProp) throws IOException {
        mapaPuertos = new HashMap<>();
        Properties props = new Properties();
        props.load(new FileInputStream(archivoProp));
        for (String key : props.stringPropertyNames()) {
            mapaPuertos.put(key, Integer.parseInt(props.getProperty(key)));
        }
    }

    @Override
    public void enviarActualizacion(Reunion r) {
        Set<String> destinatarios = new HashSet<>(r.getInvitados());
        destinatarios.add(r.getOrganizador());
        for (String nombre : destinatarios) {
            int puerto = mapaPuertos.getOrDefault(nombre, -1);
            if (puerto != -1) {
                try (Socket socket = new Socket(nombre, puerto);
                     ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {
                    out.writeObject(r);
                } catch (IOException e) {
                    System.out.println("Error enviando a " + nombre);
                }
            }
        }
    }

    public void iniciar() throws IOException {
        ServerSocket serverSocket = new ServerSocket(9000); // puerto fijo del servidor central
        System.out.println("Servidor central esperando...");

        while (true) {
            Socket socket = serverSocket.accept();
            new Thread(() -> {
                try (ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
                    Reunion r = (Reunion) in.readObject();
                    enviarActualizacion(r);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}