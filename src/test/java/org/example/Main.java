package org.example;


import org.apache.poi.ss.usermodel.*;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;



import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;


public class Main {

    // Implementiere die zu testende Methode (hier ein Dummy-Beispiel)
    public int berechneVersicherungsbetrag(int alter, boolean vorerkrankung, int risikoKlasse, boolean oeffentlicherDienst) {
        int betrag = risikoKlasse * (vorerkrankung ? 100 : 50);
        if (oeffentlicherDienst) {
            betrag -= 20;
        }
        return betrag;
    }

    // Funktion, um Alterswert aus der Excel-Datei abzuleiten
    private int determineAlter(Row row) {
        if (row.getCell(1).getStringCellValue().equalsIgnoreCase("x")) {
            return 10; // Beispielwert für Alter 0-17
        } else if (row.getCell(2).getStringCellValue().equalsIgnoreCase("x")) {
            return 30; // Beispielwert für Alter 18-67
        } else if (row.getCell(3).getStringCellValue().equalsIgnoreCase("x")) {
            return 70; // Beispielwert für Alter 68-120
        }
        throw new IllegalArgumentException("Kein gültiges Alter definiert im Testcase.");
    }

    // Funktion, um die Risikoklasse aus der Excel-Datei abzuleiten
    private int determineRisikoKlasse(Row row) {
        if (row.getCell(5).getStringCellValue().equalsIgnoreCase("x")) return 1;
        if (row.getCell(6).getStringCellValue().equalsIgnoreCase("x")) return 2;
        if (row.getCell(7).getStringCellValue().equalsIgnoreCase("x")) return 3;
        if (row.getCell(8).getStringCellValue().equalsIgnoreCase("x")) return 4;
        throw new IllegalArgumentException("Keine gültige Risikoklasse definiert im Testcase.");
    }

    @TestFactory
     Collection<DynamicTest> testBerechneVersicherungsbetrag() throws IOException {
        List<DynamicTest> tests = new ArrayList<>();

        // Excel-Datei einlesen
        FileInputStream file = new FileInputStream(new File("src/main/resources/Klassifikationsbaum.xlsx")); // Pfad zur Excel-Datei
        Workbook workbook = WorkbookFactory.create(file);
        Sheet sheet = workbook.getSheetAt(0); // Erste Tabelle in der Excel-Datei

        Iterator<Row> iterator = sheet.iterator();
        iterator.next(); // Überspringe die Header-Zeile
        iterator.next(); // Überspringe die erste Zeile

        // Testfälle aus der Excel-Datei lesen
        while (iterator.hasNext()) {
            Row row = iterator.next();
            String testcaseId = row.getCell(0).getStringCellValue();
            System.out.println(testcaseId);
            int alter = determineAlter(row);
            System.out.println(alter);
            boolean vorerkrankung = row.getCell(4).getStringCellValue().equalsIgnoreCase("x");
            System.out.println(vorerkrankung);
            boolean oeffentlicherDienst = row.getCell(9).getStringCellValue().equalsIgnoreCase("x");
            System.out.println(oeffentlicherDienst);
            int risikoKlasse = determineRisikoKlasse(row);
            System.out.println(risikoKlasse);
            String sollErgebnis = row.getCell(12).getStringCellValue().trim();
            System.out.println(sollErgebnis);
            int expectedResult = sollErgebnis.isEmpty() ? 0 : Integer.parseInt(sollErgebnis);

            // Dynamischen Test hinzufügen
            tests.add(DynamicTest.dynamicTest(testcaseId, () -> {
                int actualResult = berechneVersicherungsbetrag(alter, vorerkrankung, risikoKlasse, oeffentlicherDienst);
                assertEquals(expectedResult, actualResult, "Fehler im Testfall: " + testcaseId);
            }));
        }

        workbook.close();
        file.close();

        return tests;
    }
}