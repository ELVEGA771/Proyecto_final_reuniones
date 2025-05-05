import java.io.FileInputStream;
import java.util.*;
import aplicacion.AppEmpleado;
import modelo.Empleado;
import servidor.ServidorCentral;
import servidor.ServidorEmpleado;

public class Main {
    public static void main(String[] args) throws Exception {
        Map<String, String> params = parseArgs(args);
        String mode = params.getOrDefault("mode", "");
        // Lista de empleados válidos
        Set<String> empleadosValidos = Set.of(
                "Alice_White", "Bob_Smith", "Carol_Simpson", "David_Black", "Eva_Brown"
        );

        switch (mode) {
            case "central":
                // Iniciar ServidorCentral
                String propPath = params.getOrDefault("empleadosConfig", "recursos/empleados.properties");
                new ServidorCentral(propPath).iniciar();
                break;

            case "empleado":
                // Iniciar ServidorEmpleado para un empleado
                String nombreEmp = params.get("nombre");
                if (nombreEmp == null) {
                    System.err.println("Error: Falta --nombre para modo empleado");
                    System.exit(1);
                }
                int puerto = Integer.parseInt(params.getOrDefault("port", "6000"));
                new Thread(new ServidorEmpleado(new Empleado(nombreEmp), puerto)).start();
                break;

            case "app":
                // Iniciar AppEmpleado con selección dinámica de empleado
                Scanner sc = new Scanner(System.in);
                String nombreApp = params.get("nombre");
                if (nombreApp == null) {
                    System.err.println("Error: falta --nombre para modo app");
                    System.exit(1);
                }
                AppEmpleado app = new AppEmpleado(nombreApp);
                while (true) {
                    System.out.println("\n--- Menú de " + nombreApp + " ---");
                    System.out.println("1. Crear reunión");
                    System.out.println("2. Modificar reunión");
                    System.out.println("3. Salir");
                    System.out.print("Opción: ");
                    String line = sc.nextLine();
                    int opc;
                    try {
                        opc = Integer.parseInt(line);
                    } catch (NumberFormatException e) {
                        System.out.println("Opción no válida, ingresa un número.");
                        continue;
                    }

                    switch (opc) {
                        case 1:
                            app.crearReunion();
                            break;
                        case 2:
                            app.modificarReunion();
                            break;
                        case 3:
                            System.out.println("Saliendo...");
                            System.exit(0);
                        default:
                            System.out.println("Opción fuera de rango.");
                    }
                }

            default:
                System.out.println("Uso: java Main --mode={central|empleado|app} [--nombre=<NombreEmpleado>] [--port=<Puerto>] [--empleadosConfig=<ruta>]");
                System.exit(0);
        }
    }

    private static Map<String, String> parseArgs(String[] args) {
        Map<String, String> map = new HashMap<>();
        for (String arg : args) {
            if (arg.startsWith("--") && arg.contains("=")) {
                String[] parts = arg.substring(2).split("=", 2);
                map.put(parts[0], parts[1]);
            }
        }
        return map;
    }
}
