package com.project.casaberriel.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

public class Utils {

	  /**
     * Método para obtener una fecha en formato Date.
     *
     * @param fecha a convertir a Date
     * @return String con la fecha en el formato definido
     */
    public static Date obtenerDate(String fecha, String format) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.parse(fecha);
    }
    
    
 // Método para formatear la fecha
    public static String formatDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE d 'de' MMMM 'de' yyyy", new Locale("es", "ES"));
        return date.format(formatter);
    }

    // Método para calcular el precio total
    public static double calculateTotalPrice(LocalDate fechaEntrada, LocalDate fechaSalida, double precioPorDia) {
        long dias = java.time.temporal.ChronoUnit.DAYS.between(fechaEntrada, fechaSalida);
        return dias * precioPorDia;
    }
    
    public static String formatFecha(LocalDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return date.format(formatter);
    }


	public static long calculateTotalDays(LocalDate fechaEntrada, LocalDate fechaSalida) {
		long dias= java.time.temporal.ChronoUnit.DAYS.between(fechaEntrada, fechaSalida);
		return dias;
	}
}
