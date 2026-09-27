package com.tecnomovil;

import java.util.List;
import java.util.Scanner;

public class Main {

    
    public static final String RESET = "\u001B[0m";
    public static final String CYAN = "\u001B[36m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String RED = "\u001B[31m";
    public static final String BOLD = "\u001B[1m";

    public static void main(String[] args) {
        System.out.println(CYAN + "Inicializando sistema y cargando datos simulados..." + RESET);
        List<RegistroTransporte> registros = GeneradorDatos.generarDatosSimulados(150000); // 150 mil registros
        Scanner scanner = new Scanner(System.in);
        boolean salir = false;

        while (!salir) {
            System.out.println(BOLD + CYAN + "\n==================================================");
            System.out.println("   SISTEMA DE ANÁLISIS TECNOMÓVIL DATA v2.0       ");
            System.out.println("==================================================" + RESET);
            System.out.println(GREEN + "Datos en memoria: " + registros.size() + " registros procesados." + RESET);
            
            System.out.println("\nSELECCIONE UNA OPCIÓN:");
            System.out.println("1. Afluencia por Estación (Entradas)");
            System.out.println("2. Top 3 Horas Pico");
            System.out.println("3. Rutas Más Utilizadas");
            System.out.println("4. Patrones de Viaje por Usuario (Muestra de 5)");
            System.out.println("5. Tiempo Promedio entre Estaciones");
            System.out.println("6. Detección de Sobrecarga en Rutas");
            System.out.println(YELLOW + "7. [NUEVO] Top 5 Usuarios Más Activos" + RESET);
            System.out.println(YELLOW + "8. [NUEVO] Volumen Total por Estación" + RESET);
            System.out.println(RED + "0. Salir" + RESET);
            System.out.print(BOLD + "Opción > " + RESET);

            String opcion = scanner.nextLine();

            switch (opcion) {
                case "1":
                    System.out.println(YELLOW + "\n--- AFLUENCIA POR ESTACIÓN ---" + RESET);
                    TecnoMovilDataProcessor.afluenciaPorEstacion(registros)
                            .forEach((est, count) -> System.out.printf("Estación: %-12s | Entradas: %d%n", est, count));
                    break;
                case "2":
                    System.out.println(YELLOW + "\n--- TOP 3 HORAS PICO ---" + RESET);
                    TecnoMovilDataProcessor.horasPico(registros, 3)
                            .forEach(e -> System.out.printf("Hora: %02d:00 | Flujo: %d pasajeros%n", e.getKey(), e.getValue()));
                    break;
                case "3":
                    System.out.println(YELLOW + "\n--- RUTAS MÁS UTILIZADAS ---" + RESET);
                    TecnoMovilDataProcessor.rutasMasUtilizadas(registros)
                            .forEach(e -> System.out.printf("Ruta: %-5s | Registros: %d%n", e.getKey(), e.getValue()));
                    break;
                case "4":
                    System.out.println(YELLOW + "\n--- PATRONES DE VIAJE POR USUARIO ---" + RESET);
                    TecnoMovilDataProcessor.patronesDeViaje(registros).entrySet().stream().limit(5)
                            .forEach(e -> System.out.printf("Usuario %-4s -> Ruta: %s%n", e.getKey(), String.join(" -> ", e.getValue())));
                    break;
                case "5":
                    System.out.println(YELLOW + "\n--- TIEMPO PROMEDIO ENTRE ESTACIONES ---" + RESET);
                    TecnoMovilDataProcessor.tiempoPromedioEntreEstaciones(registros).entrySet().stream().limit(5)
                            .forEach(e -> System.out.printf("Usuario %-4s -> Promedio: %.2f min%n", e.getKey(), e.getValue()));
                    break;
                case "6":
                    System.out.print(CYAN + "\nIngrese el umbral de sobrecarga (ej. 35000): " + RESET);
                    try {
                        long umbral = Long.parseLong(scanner.nextLine());
                        System.out.println(YELLOW + "\n--- ESTADO DE RUTAS ---" + RESET);
                        TecnoMovilDataProcessor.detectarSobrecarga(registros, umbral).forEach(est -> {
                            String colorEstado = est.estado().equals("CRÍTICA") ? RED : GREEN;
                            System.out.printf("Ruta: %-5s | Ocupación: %-6d | Estado: " + colorEstado + "%s" + RESET + "%n", 
                                    est.ruta(), est.ocupacion(), est.estado());
                        });
                    } catch (Exception e) {
                        System.out.println(RED + "Error: Ingrese un valor numérico." + RESET);
                    }
                    break;
                case "7":
                    System.out.println(YELLOW + "\n--- TOP 5 USUARIOS MÁS ACTIVOS ---" + RESET);
                    TecnoMovilDataProcessor.usuariosMasActivos(registros, 5)
                            .forEach(e -> System.out.printf("Usuario: %-4s | Viajes registrados: %d%n", e.getKey(), e.getValue()));
                    break;
                case "8":
                    System.out.println(YELLOW + "\n--- VOLUMEN TOTAL POR ESTACIÓN ---" + RESET);
                    TecnoMovilDataProcessor.volumenTotalPorEstacion(registros)
                            .forEach(e -> System.out.printf("Estación: %-12s | Tráfico Total: %d%n", e.getKey(), e.getValue()));
                    break;
                case "0":
                    salir = true;
                    System.out.println(GREEN + "\nApagando sistema... ¡Hasta pronto!" + RESET);
                    break;
                default:
                    System.out.println(RED + "\nOpción inválida. Intente de nuevo." + RESET);
            }
        }
        scanner.close();
    }
}

    private static void imprimirReporteCompleto(List<RegistroTransporte> registros) {
        System.out.println("\n==================================================");
        System.out.println("       REPORTE COMPLETO DE OPERACIÓN DIARIA       ");
        System.out.println("==================================================");

        System.out.println("\n[1] AFLUENCIA POR ESTACIÓN");
        TecnoMovilDataProcessor.afluenciaPorEstacion(registros)
                .forEach((k, v) -> System.out.println(" - " + k + ": " + v + " entradas"));

        System.out.println("\n[2] TOP 3 HORAS PICO");
        TecnoMovilDataProcessor.horasPico(registros, 3)
                .forEach(e -> System.out.println(" - " + e.getKey() + ":00h: " + e.getValue() + " registros"));

        System.out.println("\n[3] RUTAS MÁS UTILIZADAS");
        TecnoMovilDataProcessor.rutasMasUtilizadas(registros)
                .forEach(e -> System.out.println(" - Ruta " + e.getKey() + ": " + e.getValue() + " uso(s)"));

        System.out.println("\n[4] DIAGNÓSTICO DE SOBRECARGA (UMBRAL = 10)");
        TecnoMovilDataProcessor.detectarSobrecarga(registros, 10)
                .forEach(e -> System.out.println(" - Ruta " + e.ruta() + ": " + e.ocupacion() + " (" + e.estado() + ")"));
    }
}
