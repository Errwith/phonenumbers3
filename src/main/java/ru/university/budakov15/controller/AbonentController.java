package ru.university.budakov15.controller;

import ru.university.budakov15.exception.BusinessException;
import ru.university.budakov15.model.Abonent;
import ru.university.budakov15.service.AbonentService;

import java.util.List;
import java.util.Scanner;

public class AbonentController {
    private final Scanner scanner;
    private final AbonentService service;

    public AbonentController(Scanner scanner, AbonentService service) {
        this.scanner = scanner;
        this.service = service;
    }

    public void run() {
        while (true) {
            printMenu();
            if (!scanner.hasNextLine()) {
                System.out.println("\nВвод завершен. Выход из программы.");
                return;
            }
            String cmd = scanner.nextLine().trim();
            try {
                switch (cmd) {
                    case "1" -> showAll();
                    case "2" -> addAbonent();
                    case "3" -> editAbonent();
                    case "4" -> deleteAbonent();
                    case "5" -> searchByLastName();
                    case "6" -> searchByPhone();
                    case "0" -> {
                        System.out.println("Выход из программы.");
                        return;
                    }
                    default -> System.out.println("Неизвестная команда. Попробуйте снова.");
                }
            } catch (BusinessException e) {
                System.out.println("Ошибка: " + e.getMessage());
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите корректное целое число (ID).");
            }
        }
    }

    private void printMenu() {
        System.out.println("\n--- ТЕЛЕФОННЫЙ СПРАВОЧНИК ---");
        System.out.println("1. Вывести всех абонентов (по алфавиту)");
        System.out.println("2. Добавить абонента");
        System.out.println("3. Редактировать абонента");
        System.out.println("4. Удалить абонента");
        System.out.println("5. Поиск по фамилии");
        System.out.println("6. Поиск по номеру телефона");
        System.out.println("0. Выход");
        System.out.print("Выберите пункт: ");
    }

    private void showAll() {
        List<Abonent> list = service.getAll();
        if (list.isEmpty()) {
            System.out.println("Справочник пуст.");
            return;
        }
        list.forEach(System.out::println);
    }

    private void addAbonent() {
        System.out.print("Фамилия: ");
        String lastName = scanner.nextLine();
        System.out.print("Имя: ");
        String firstName = scanner.nextLine();
        System.out.print("Телефон 1: ");
        String phone1 = scanner.nextLine();
        System.out.print("Телефон 2 (Enter, если нет): ");
        String phone2 = scanner.nextLine();
        System.out.print("Телефон 3 (Enter, если нет): ");
        String phone3 = scanner.nextLine();

        Abonent saved = service.add(lastName, firstName, phone1, phone2, phone3);
        System.out.println("Добавлен: " + saved);
    }

    private void editAbonent() {
        System.out.print("Введите ID абонента для редактирования: ");
        int id = Integer.parseInt(scanner.nextLine().trim());
        Abonent existing = service.getById(id);

        System.out.println("Текущие данные: " + existing);
        System.out.println("(Оставьте поле пустым, чтобы сохранить текущее значение; введите '-', чтобы удалить номер)");

        System.out.print("Новая фамилия [" + existing.getLastName() + "]: ");
        String lastName = scanner.nextLine();
        if (lastName.isBlank()) lastName = existing.getLastName();

        System.out.print("Новое имя [" + existing.getFirstName() + "]: ");
        String firstName = scanner.nextLine();
        if (firstName.isBlank()) firstName = existing.getFirstName();

        System.out.print("Новый телефон 1 [" + existing.getPhone1() + "]: ");
        String phone1 = readUpdatedPhone(scanner.nextLine(), existing.getPhone1());

        System.out.print("Новый телефон 2 [" + existing.getPhone2() + "]: ");
        String phone2 = readUpdatedPhone(scanner.nextLine(), existing.getPhone2());

        System.out.print("Новый телефон 3 [" + existing.getPhone3() + "]: ");
        String phone3 = readUpdatedPhone(scanner.nextLine(), existing.getPhone3());

        service.update(id, lastName, firstName, phone1, phone2, phone3);
        System.out.println("Абонент успешно обновлен.");
    }

    private String readUpdatedPhone(String input, String currentPhone) {
        if (input.equals("-")) return "";
        if (input.isBlank()) return currentPhone;
        return input;
    }

    private void deleteAbonent() {
        System.out.print("Введите ID абонента для удаления: ");
        int id = Integer.parseInt(scanner.nextLine().trim());
        service.delete(id);
        System.out.println("Абонент удален.");
    }

    private void searchByLastName() {
        System.out.print("Введите фамилию для поиска: ");
        String lastName = scanner.nextLine();
        List<Abonent> found = service.findByLastName(lastName);
        found.forEach(System.out::println);
    }

    private void searchByPhone() {
        System.out.print("Введите номер телефона для поиска: ");
        String phone = scanner.nextLine();
        List<Abonent> found = service.findByPhone(phone);
        found.forEach(System.out::println);
    }
}
