package modelo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class Reunion implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID id;
    private String tema;
    private List<String> invitados;
    private String organizador;
    private String lugar;
    private LocalDateTime inicio;
    private LocalDateTime fin;
    private long timestamp;

    public Reunion(String tema, List<String> invitados, String organizador, String lugar,
                   LocalDateTime inicio, LocalDateTime fin) {
        this.id = UUID.randomUUID(); // ID único
        this.tema = tema;
        this.invitados = invitados;
        this.organizador = organizador;
        this.lugar = lugar;
        this.inicio = inicio;
        this.fin = fin;
        this.timestamp = System.currentTimeMillis();
    }

    // Constructor adicional para reconstrucción con ID existente (desde archivo)
    public Reunion(UUID id, String tema, List<String> invitados, String organizador, String lugar,
                   LocalDateTime inicio, LocalDateTime fin, long timestamp) {
        this.id = id;
        this.tema = tema;
        this.invitados = invitados;
        this.organizador = organizador;
        this.lugar = lugar;
        this.inicio = inicio;
        this.fin = fin;
        this.timestamp = timestamp;
    }

    public UUID getId() { return id; }
    public String getTema() { return tema; }
    public List<String> getInvitados() { return invitados; }
    public String getOrganizador() { return organizador; }
    public String getLugar() { return lugar; }
    public LocalDateTime getInicio() { return inicio; }
    public LocalDateTime getFin() { return fin; }
    public long getTimestamp() { return timestamp; }

    public void setTema(String tema) { this.tema = tema; }
    public void setLugar(String lugar) { this.lugar = lugar; }
    public void setInicio(LocalDateTime inicio) { this.inicio = inicio; }
    public void setFin(LocalDateTime fin) { this.fin = fin; }

    @Override
    public String toString() {
        return "ID: " + id + "\nTema: " + tema + "\nOrganizador: " + organizador +
               "\nInvitados: " + invitados + "\nLugar: " + lugar +
               "\nInicio: " + inicio + "\nFin: " + fin + "\n";
    }

}
