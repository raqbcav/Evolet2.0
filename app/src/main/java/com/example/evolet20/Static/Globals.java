package com.example.evolet20.Static;

import androidx.viewpager2.widget.ViewPager2;

import com.example.evolet20.Model.Usuario;
import com.example.evolet20.ViewPagerAdapater;
import com.google.android.material.tabs.TabLayout;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

public class Globals {
    public static Usuario usuario;
    public static int semanaActual;
    public static LocalDate fechaActual;
    public static LocalDate fechaSeleccionadaMes;
    public static TabLayout tabLayout;
    public static ViewPager2 viewPager2;
    public static ViewPagerAdapater viewPagerAdapater;

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
}
