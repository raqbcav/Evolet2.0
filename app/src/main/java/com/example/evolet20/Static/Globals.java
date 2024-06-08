package com.example.evolet20.Static;

import androidx.viewpager2.widget.ViewPager2;

import com.example.evolet20.Model.Usuario;
import com.google.android.material.tabs.TabLayout;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Globals {
    public static Usuario usuario;
    public static int semanaActual;
    public static LocalDate fechaActual;

    public static LocalDate textToLocalDate(String fecha){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        try {
            LocalDate date = LocalDate.parse(fecha, formatter);
            return date;
        } catch (DateTimeParseException e) {
            System.out.println("Error al convertir la fecha: " + e.getMessage());
            return LocalDate.now();
        }
    }

    public static String LocalDateToText(LocalDate fecha){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formattedDate = fecha.format(formatter);
        return formattedDate;
    }

    public static LocalDate dateToLocalDate(Date fecha){
        LocalDate localDate = fecha.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        return localDate;
    }

    public static List<LocalDate> getSemanaCompleta(int semana) {
        int anio = LocalDate.now().getYear();

        // Obtener el primer día de la semana (lunes) dada la semana del año y el año
        LocalDate primerDiaSemana = LocalDate.of(anio, 1, 1)
                .with(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear(), semana)
                .with(DayOfWeek.MONDAY);

        // Crear una lista para almacenar los 7 días de la semana
        List<LocalDate> diasSemana = new ArrayList<>();

        // Agregar el primer día de la semana y los siguientes 6 días a la lista
        for (int i = 0; i < 7; i++) {
            diasSemana.add(primerDiaSemana.plusDays(i));
        }

        // Imprimir la lista de días de la semana
        return diasSemana;
    }
}
