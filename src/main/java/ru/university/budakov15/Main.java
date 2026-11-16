package ru.university.budakov15;

import ru.university.budakov15.controller.AbonentController;
import ru.university.budakov15.repository.AbonentInMemoryRepository;
import ru.university.budakov15.repository.AbonentRepository;
import ru.university.budakov15.service.AbonentService;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        System.setErr(new PrintStream(System.err, true, StandardCharsets.UTF_8));

        AbonentRepository abonentRepository = new AbonentInMemoryRepository();
        AbonentService abonentService = new AbonentService(abonentRepository);

        Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);
        AbonentController controller = new AbonentController(scanner, abonentService);

        controller.run();
    }
}
