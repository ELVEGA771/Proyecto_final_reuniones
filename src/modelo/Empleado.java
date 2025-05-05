package modelo;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Empleado implements Observer {
    private String nombreCompleto;
    private File archivo;

    public Empleado(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
        this.archivo = new File("recursos/" + nombreCompleto + ".txt");
    }

    public String getNombre() {
        return nombreCompleto;
    }

    @Override
    public void notificarCambio(Reunion r) {
        guardarReunion(r);
    }

    public void guardarReunion(Reunion r) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(archivo, true))) {
            pw.println(r.toString());
            pw.println("--------");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}