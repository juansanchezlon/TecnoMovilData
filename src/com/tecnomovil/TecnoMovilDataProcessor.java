package com.tecnomovil;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

public class TecnoMovilDataProcessor {


    public static Map<String, Long> afluenciaPorEstacion(List<RegistroTransporte> registros) {
        return registros.parallelStream()
                .filter(r -> "entrada".equalsIgnoreCase(r.accion()))
                .collect(Collectors.groupingBy(
                        RegistroTransporte::estacion,
                        Collectors.counting()
                ));
    }

    
    public static List<Map.Entry<Integer, Long>> horasPico(List<RegistroTransporte> registros, int topN) {
        return registros.parallelStream()
                .collect(Collectors.groupingBy(
                        r -> r.timestamp().getHour(),
                        Collectors.counting()
                ))
                .entrySet().stream()
                .sorted(Map.Entry.<Integer, Long>comparingByValue().reversed())
                .limit(topN)
                .collect(Collectors.toUnmodifiableList());
    }

    
    public static List<Map.Entry<String, Long>> rutasMasUtilizadas(List<RegistroTransporte> registros) {
        return registros.parallelStream()
                .collect(Collectors.groupingBy(
                        RegistroTransporte::ruta, 
                        Collectors.counting()
                ))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .collect(Collectors.toUnmodifiableList());
    }

    
    public static Map<String, List<String>> patronesDeViaje(List<RegistroTransporte> registros) {
        return registros.stream()
                .sorted(Comparator.comparing(RegistroTransporte::timestamp))
                .collect(Collectors.groupingBy(
                        RegistroTransporte::idUsuario,
                        LinkedHashMap::new,
                        Collectors.mapping(RegistroTransporte::estacion, Collectors.toUnmodifiableList())
                ));
    }

    
    public static Map<String, Double> tiempoPromedioEntreEstaciones(List<RegistroTransporte> registros) {
        Map<String, List<RegistroTransporte>> porUsuarioOrdenado = registros.stream()
                .sorted(Comparator.comparing(RegistroTransporte::timestamp))
                .collect(Collectors.groupingBy(
                        RegistroTransporte::idUsuario,
                        LinkedHashMap::new,
                        Collectors.toUnmodifiableList()
                ));

        return porUsuarioOrdenado.entrySet().stream()
                .collect(Collectors.toUnmodifiableMap(
                        Map.Entry::getKey,
                        e -> promedioMinutosEntreConsecutivos(e.getValue())
                ));
    }

    private static double promedioMinutosEntreConsecutivos(List<RegistroTransporte> registrosUsuario) {
        if (registrosUsuario.size() < 2) return 0.0;
        return java.util.stream.IntStream.range(1, registrosUsuario.size())
                .mapToLong(i -> Duration.between(
                        registrosUsuario.get(i - 1).timestamp(),
                        registrosUsuario.get(i).timestamp()
                ).toMinutes())
                .average()
                .orElse(0.0);
    }

    
    public static List<EstadoRuta> detectarSobrecarga(List<RegistroTransporte> registros, long umbral) {
        return registros.parallelStream()
                .collect(Collectors.groupingBy(RegistroTransporte::ruta, Collectors.counting()))
                .entrySet().stream()
                .map(e -> new EstadoRuta(
                        e.getKey(),
                        e.getValue(),
                        e.getValue() > umbral ? "CRÍTICA" : "NORMAL"
                ))
                .sorted(Comparator.comparing(EstadoRuta::ocupacion).reversed())
                .collect(Collectors.toUnmodifiableList());
    }


    public static List<Map.Entry<String, Long>> usuariosMasActivos(List<RegistroTransporte> registros, int topN) {
        return registros.parallelStream()
                .collect(Collectors.groupingBy(RegistroTransporte::idUsuario, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(topN)
                .collect(Collectors.toUnmodifiableList());
    }

    
    public static List<Map.Entry<String, Long>> volumenTotalPorEstacion(List<RegistroTransporte> registros) {
        return registros.parallelStream()
                .collect(Collectors.groupingBy(RegistroTransporte::estacion, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .collect(Collectors.toUnmodifiableList());
    }
}
