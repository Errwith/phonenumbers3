package ru.university.budakov15;

import ru.university.budakov15.controller.AbonentController;
import ru.university.budakov15.exception.BusinessException;
import ru.university.budakov15.repository.AbonentPostgresRepository;
import ru.university.budakov15.repository.AbonentRepository;
import ru.university.budakov15.service.AbonentService;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Main {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/phonebook_db";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "98769876";

    public static void main(String[] args) {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        System.setErr(new PrintStream(System.err, true, StandardCharsets.UTF_8));

        try {
            AbonentRepository abonentRepository = new AbonentPostgresRepository(DB_URL, DB_USER, DB_PASSWORD);
            AbonentService abonentService = new AbonentService(abonentRepository);

            Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);
            AbonentController controller = new AbonentController(scanner, abonentService);

            controller.run();
        } catch (BusinessException e) {
            System.err.println("Не удалось подключиться к базе данных: " + e.getMessage());
        }
    }
}