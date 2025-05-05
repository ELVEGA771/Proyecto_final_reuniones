package aplicacion;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;

import modelo.Reunion;

public class AppEmpleado {
    private String nombre;
    private int puertoCentral = 9000;
    private Set<String> empleadosValidos;

    public AppEmpleado(String nombre) {
        this.nombre = nombre;
        this.empleadosValidos = cargarEmpleadosValidos();
    }

    private Set<String> cargarEmpleadosValidos() {
        Set<String> empleados = new HashSet<>();
        try (FileInputStream fis = new FileInputStream("recursos/empleados.properties")) {
            Properties props = new Properties();
            props.load(fis);
            empleados.addAll(props.stringPropertyNames());
        } catch (IOException e) {
            System.out.println("Error al cargar empleados.");
        }
        return empleados;
    }

    public void crearReunion() {
        Scanner sc = new Scanner(System.in);
        try {
            System.out.print("Tema: ");
            String tema = sc.nextLine();
            System.out.print("Invitados (coma separados): ");
            String invitadosInput = sc.nextLine();
            List<String> invitados = new ArrayList<>();
            for (String invitado : invitadosInput.split(",")) {
                String invitadoTrim = invitado.trim();
                if (!invitadoTrim.isEmpty()) {
                    invitados.add(invitadoTrim);
                }
            }
            
            for (String invitado : invitados) {
                if (!empleadosValidos.contains(invitado)) {
                    System.out.println("Invitado no válido: " + invitado);
                    return;
                }
            }
            
            System.out.print("Lugar: ");
            String lugar = sc.nextLine();
            
            LocalDateTime ini = null;
            while (ini == null) {
                try {
                    System.out.print("Inicio (YYYY-MM-DDTHH:MM): ");
                    ini = LocalDateTime.parse(sc.nextLine());
                } catch (DateTimeParseException e) {
                    System.out.println("Formato de fecha incorrecto. Use el formato YYYY-MM-DDTHH:MM");
                }
            }
            
            LocalDateTime fin = null;
            while (fin == null) {
                try {
                    System.out.print("Fin (YYYY-MM-DDTHH:MM): ");
                    fin = LocalDateTime.parse(sc.nextLine());
                    if (fin.isBefore(ini)) {
                        System.out.println("La fecha de fin debe ser posterior a la de inicio.");
                        fin = null;
                    }
                } catch (DateTimeParseException e) {
                    System.out.println("Formato de fecha incorrecto. Use el formato YYYY-MM-DDTHH:MM");
                }
            }

            Reunion r = new Reunion(tema, invitados, nombre, lugar, ini, fin);

            // Guardar la reunión para el organizador y los invitados
            Set<String> todosParticipantes = new HashSet<>(invitados);
            todosParticipantes.add(nombre); // Añadir al organizador
            
            for (String participante : todosParticipantes) {
                List<Reunion> reunionesParticipante = cargarReunionesEmpleado(participante);
                
                // Verificar si ya existe una reunión con el mismo ID
                boolean existeReunion = false;
                for (Reunion reunionExistente : reunionesParticipante) {
                    if (reunionExistente.getId().equals(r.getId())) {
                        existeReunion = true;
                        break;
                    }
                }
                
                if (!existeReunion) {
                    reunionesParticipante.add(r);
                    guardarReuniones(participante, reunionesParticipante);
                }
            }

            try (Socket socket = new Socket("servidor-central", puertoCentral);
                 ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {
                out.writeObject(r);
                System.out.println("Reunión enviada.");
            }
        } catch (IOException e) {
            System.out.println("Error al enviar la reunión.");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Error en los datos. Asegúrate del formato correcto.");
            e.printStackTrace();
        }
    }

    public void modificarReunion() {
        Scanner sc = new Scanner(System.in);
        List<Reunion> reuniones = cargarReuniones();
        List<Reunion> reunionesModificables = new ArrayList<>();
        int index = 1;
        
        for (Reunion r : reuniones) {
            if (r.getOrganizador().equals(nombre) || r.getInvitados().contains(nombre)) {
                System.out.println(index + ". " + r.getTema() + " - " + r.getInicio());
                reunionesModificables.add(r);
                index++;
            }
        }
        
        if (reunionesModificables.isEmpty()) {
            System.out.println("No hay reuniones para modificar.");
            return;
        }

        int seleccion = 0;
        boolean seleccionValida = false;
        
        while (!seleccionValida) {
            try {
                System.out.print("Seleccione el número de la reunión a modificar: ");
                seleccion = Integer.parseInt(sc.nextLine());
                
                if (seleccion < 1 || seleccion > reunionesModificables.size()) {
                    System.out.println("Selección inválida. Debe estar entre 1 y " + reunionesModificables.size());
                } else {
                    seleccionValida = true;
                }
            } catch (NumberFormatException e) {
                System.out.println("Por favor, ingrese un número válido.");
            }
        }

        Reunion antigua = reunionesModificables.get(seleccion - 1);

        // Crear una copia para modificar
        Reunion modificada = new Reunion(
                antigua.getId(), // Mantener el mismo ID
                antigua.getTema(), 
                new ArrayList<>(antigua.getInvitados()), // Copia profunda
                antigua.getOrganizador(),
                antigua.getLugar(), 
                antigua.getInicio(), 
                antigua.getFin(),
                System.currentTimeMillis() // Actualizar timestamp
        );

        System.out.print("Nuevo tema (actual: " + modificada.getTema() + ") [Enter para mantener]: ");
        String tema = sc.nextLine();
        if (!tema.isEmpty()) modificada.setTema(tema);
        
        System.out.print("Nuevo lugar (actual: " + modificada.getLugar() + ") [Enter para mantener]: ");
        String lugar = sc.nextLine();
        if (!lugar.isEmpty()) modificada.setLugar(lugar);
        
        boolean fechaInicioValida = false;
        while (!fechaInicioValida) {
            System.out.print("Nuevo inicio (YYYY-MM-DDTHH:MM) (actual: " + modificada.getInicio() + ") [Enter para mantener]: ");
            String inicioStr = sc.nextLine();
            
            if (inicioStr.isEmpty()) {
                fechaInicioValida = true; // Mantener el valor actual
            } else {
                try {
                    LocalDateTime nuevoInicio = LocalDateTime.parse(inicioStr);
                    modificada.setInicio(nuevoInicio);
                    fechaInicioValida = true;
                } catch (DateTimeParseException e) {
                    System.out.println("Formato de fecha incorrecto. Use el formato YYYY-MM-DDTHH:MM");
                }
            }
        }
        
        boolean fechaFinValida = false;
        while (!fechaFinValida) {
            System.out.print("Nuevo fin (YYYY-MM-DDTHH:MM) (actual: " + modificada.getFin() + ") [Enter para mantener]: ");
            String finStr = sc.nextLine();
            
            if (finStr.isEmpty()) {
                fechaFinValida = true; // Mantener el valor actual
            } else {
                try {
                    LocalDateTime nuevoFin = LocalDateTime.parse(finStr);
                    if (nuevoFin.isBefore(modificada.getInicio())) {
                        System.out.println("La fecha de fin debe ser posterior a la de inicio.");
                    } else {
                        modificada.setFin(nuevoFin);
                        fechaFinValida = true;
                    }
                } catch (DateTimeParseException e) {
                    System.out.println("Formato de fecha incorrecto. Use el formato YYYY-MM-DDTHH:MM");
                }
            }
        }

        // Identificar todos los participantes (antiguos y nuevos)
        Set<String> participantes = new HashSet<>();
        participantes.addAll(antigua.getInvitados());
        participantes.add(antigua.getOrganizador());
        participantes.addAll(modificada.getInvitados());
        participantes.add(modificada.getOrganizador());

        // Para cada participante, actualizar su archivo de reuniones
        for (String participante : participantes) {
            List<Reunion> reunionesParticipante = cargarReunionesEmpleado(participante);
            
            // Eliminar la reunión antigua si existe
            reunionesParticipante.removeIf(r -> r.getId().equals(antigua.getId()));
            
            // Añadir la reunión modificada si el participante está involucrado
            if (modificada.getOrganizador().equals(participante) || 
                modificada.getInvitados().contains(participante)) {
                reunionesParticipante.add(modificada);
                guardarReuniones(participante, reunionesParticipante);
            }
        }
        
        // Notificar al servidor central
        try (Socket socket = new Socket("servidor-central"
                , puertoCentral);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {
            out.writeObject(modificada);
            System.out.println("Reunión modificada y actualizada correctamente.");
        } catch (IOException e) {
            System.out.println("Error al enviar la reunión modificada.");
            e.printStackTrace();
        }
    }

    private List<Reunion> cargarReuniones() {
        return cargarReunionesEmpleado(nombre);
    }

    private List<Reunion> cargarReunionesEmpleado(String empleado) {
        List<Reunion> reuniones = new ArrayList<>();
        File archivo = new File("recursos/" + empleado + ".txt");
        if (!archivo.exists()) return reuniones;
        
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            StringBuilder sb = new StringBuilder();
            while ((linea = br.readLine()) != null) {
                if (linea.equals("--------")) {
                    if (sb.length() > 0) {
                        Reunion r = parsearReunion(sb.toString());
                        if (r != null) reuniones.add(r);
                        sb.setLength(0);
                    }
                } else {
                    sb.append(linea).append("\n");
                }
            }
            // Verificar si hay datos después del último separador
            if (sb.length() > 0) {
                Reunion r = parsearReunion(sb.toString());
                if (r != null) reuniones.add(r);
            }
        } catch (IOException e) {
            System.out.println("Error al leer las reuniones para " + empleado);
            e.printStackTrace();
        }
        
        return reuniones;
    }

    private Reunion parsearReunion(String texto) {
        try {

            String[] lineas = texto.split("\n");
            UUID id = UUID.fromString(lineas[0].split(": ")[1]);
            String tema = lineas[1].split(": ")[1];
            String organizador = lineas[2].split(": ")[1];
            String invitadosStr = lineas[3].split(": ")[1];

            // Extraer la lista de invitados correctamente
            String contenidoLista = invitadosStr.substring(1, invitadosStr.length() - 1);
            List<String> invitados = new ArrayList<>();
            if (!contenidoLista.isEmpty()) {
                invitados = Arrays.asList(contenidoLista.split(", "));
            }

            String lugar = lineas[4].split(": ")[1];
            LocalDateTime inicio = LocalDateTime.parse(lineas[5].split(": ")[1]);
            LocalDateTime fin = LocalDateTime.parse(lineas[6].split(": ")[1]);

            return new Reunion(id, tema, invitados, organizador, lugar, inicio, fin, System.currentTimeMillis());
        } catch (Exception e) {
            System.out.println("Error al parsear reunión: " + e.getMessage());
            return null;
        }
    }

    private void guardarReuniones(String empleado, List<Reunion> reuniones) {
        File archivo = new File("recursos/" + empleado + ".txt");
        
        // Eliminar duplicados usando un mapa por ID
        Map<UUID, Reunion> reunionesPorId = new HashMap<>();
        for (Reunion r : reuniones) {
            reunionesPorId.put(r.getId(), r);
        }
        
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivo))) {
            boolean primeraReunion = true;
            for (Reunion r : reunionesPorId.values()) {
                if (!primeraReunion) {
                    bw.write("--------\n");
                }
                bw.write(r.toString());
                primeraReunion = false;
            }
        } catch (IOException e) {
            System.out.println("Error al guardar reuniones para " + empleado);
            e.printStackTrace();
        }
    }
}