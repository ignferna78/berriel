package com.project.casaberriel.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

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
}
