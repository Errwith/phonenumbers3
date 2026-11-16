package ru.university.budakov15.service;

import ru.university.budakov15.exception.*;
import ru.university.budakov15.model.Abonent;
import ru.university.budakov15.repository.AbonentRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class AbonentService {
    private final AbonentRepository repository;

    // Разрешены русские и английские буквы, дефис и пробел (от 2 до 50 символов)
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Zа-яА-ЯёЁ\\-]+(\\s[a-zA-Zа-яА-ЯёЁ\\-]+)*$");
    // Разрешены +, цифры, скобки, пробелы и дефисы
    private static final Pattern PHONE_ALLOWED_CHARS = Pattern.compile("^\\+?[0-9\\s()\\-]+$");

    public AbonentService(AbonentRepository repository) {
        this.repository = repository;
    }

    public Abonent add(String lastName, String firstName, String phone1, String phone2, String phone3) {
        List<String> validPhones = validateAndNormalizePhones(phone1, phone2, phone3);
        validateNames(lastName, firstName);

        Abonent abonent = new Abonent(
                lastName,
                firstName,
                validPhones.get(0),
                validPhones.get(1),
                validPhones.get(2)
        );
        return repository.save(abonent);
    }

    public void update(int id, String lastName, String firstName, String phone1, String phone2, String phone3) {
        Abonent existing = repository.findById(id)
                .orElseThrow(() -> new AbonentNotFoundException("Абонент с id=" + id + " не найден"));

        List<String> validPhones = validateAndNormalizePhones(phone1, phone2, phone3);
        validateNames(lastName, firstName);

        existing.setLastName(lastName);
        existing.setFirstName(firstName);
        existing.setPhone1(validPhones.get(0));
        existing.setPhone2(validPhones.get(1));
        existing.setPhone3(validPhones.get(2));

        repository.update(existing);
    }

    public void delete(int id) {
        if (id <= 0) {
            throw new AbonentNotFoundException("ID абонента должен быть положительным числом");
        }
        if (!repository.deleteById(id)) {
            throw new AbonentNotFoundException("Абонент с id=" + id + " не найден");
        }
    }

    public Abonent getById(int id) {
        if (id <= 0) {
            throw new AbonentNotFoundException("ID абонента должен быть положительным числом");
        }
        return repository.findById(id)
                .orElseThrow(() -> new AbonentNotFoundException("Абонент с id=" + id + " не найден"));
    }

    public List<Abonent> getAll() {
        return repository.findAll();
    }

    public List<Abonent> findByLastName(String lastName) {
        if (lastName == null || lastName.isBlank()) {
            throw new EmptyLastNameException("Фамилия для поиска не может быть пустой");
        }
        List<Abonent> result = repository.findByLastName(lastName.trim());
        if (result.isEmpty()) {
            throw new AbonentNotFoundException("Абоненты с фамилией '" + lastName.trim() + "' не найдены");
        }
        return result;
    }

    public List<Abonent> findByPhone(String phone) {
        if (phone == null || phone.isBlank()) {
            throw new EmptyPhoneException("Номер телефона для поиска не может быть пустым");
        }
        String trimmed = phone.trim();
        if (!PHONE_ALLOWED_CHARS.matcher(trimmed).matches()) {
            throw new InvalidPhoneFormatException("Поисковый запрос телефона содержит недопустимые символы: '" + trimmed + "'");
        }
        List<Abonent> result = repository.findByPhone(trimmed);
        if (result.isEmpty()) {
            throw new AbonentNotFoundException("Абоненты с телефоном '" + trimmed + "' не найдены");
        }
        return result;
    }

    private void validateNames(String lastName, String firstName) {
        if (lastName == null || lastName.isBlank()) {
            throw new EmptyLastNameException("Фамилия не может быть пустой");
        }
        if (firstName == null || firstName.isBlank()) {
            throw new EmptyFirstNameException("Имя не может быть пустым");
        }

        String cleanLast = lastName.trim();
        String cleanFirst = firstName.trim();

        if (cleanLast.length() < 2 || cleanLast.length() > 50 || !NAME_PATTERN.matcher(cleanLast).matches()) {
            throw new InvalidNameFormatException("Некорректный формат фамилии '" + cleanLast + "'. Используйте только буквы и дефис (от 2 до 50 символов).");
        }
        if (cleanFirst.length() < 2 || cleanFirst.length() > 50 || !NAME_PATTERN.matcher(cleanFirst).matches()) {
            throw new InvalidNameFormatException("Некорректный формат имени '" + cleanFirst + "'. Используйте только буквы и дефис (от 2 до 50 символов).");
        }
    }

    private List<String> validateAndNormalizePhones(String phone1, String phone2, String phone3) {
        String[] rawPhones = {phone1, phone2, phone3};
        List<String> nonEmptyPhones = new ArrayList<>();
        Set<String> uniqueDigits = new HashSet<>();

        for (int i = 0; i < rawPhones.length; i++) {
            String p = rawPhones[i] != null ? rawPhones[i].trim() : "";
            if (!p.isEmpty()) {
                validateSinglePhoneFormat(p, i + 1);
                String digitsOnly = extractDigitsNormalized(p);
                if (!uniqueDigits.add(digitsOnly)) {
                    throw new DuplicatePhoneException("У абонента указаны дублирующиеся номера телефонов: '" + p + "'");
                }
                nonEmptyPhones.add(p);
            }
        }

        if (nonEmptyPhones.isEmpty()) {
            throw new EmptyPhoneException("У абонента должен быть указан хотя бы один номер телефона");
        }

        // Дополняем пустыми строками до 3 элементов (сдвиг заполненных номеров влево)
        while (nonEmptyPhones.size() < 3) {
            nonEmptyPhones.add("");
        }
        return nonEmptyPhones;
    }

    private void validateSinglePhoneFormat(String phone, int index) {
        if (!PHONE_ALLOWED_CHARS.matcher(phone).matches()) {
            throw new InvalidPhoneFormatException(
                    "Неверный формат Телефона " + index + " ('" + phone + "'). Разрешены только цифры, '+', скобки, пробелы и дефис."
            );
        }
        String digits = phone.replaceAll("\\D", "");
        if (digits.length() < 5 || digits.length() > 15) {
            throw new InvalidPhoneFormatException(
                    "Неверная длина Телефона " + index + " ('" + phone + "'). Номер должен содержать от 5 до 15 цифр."
            );
        }
    }

    private String extractDigitsNormalized(String phone) {
        String digits = phone.replaceAll("\\D", "");
        // Если российский номер из 11 цифр начинается с 8, приводим к 7 для проверки дубликатов
        if (digits.length() == 11 && digits.startsWith("8")) {
            return "7" + digits.substring(1);
        }
        return digits;
    }
}