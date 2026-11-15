package ru.university.budakov15.repository;

import ru.university.budakov15.model.Abonent;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AbonentInMemoryRepository implements AbonentRepository {
    private static final String FILE_NAME = "abonents.txt";
    private final List<Abonent> abonents = new ArrayList<>();
    private int currentId = 1;

    public AbonentInMemoryRepository() {
        loadFromFile();
    }

    @Override
    public Abonent save(Abonent abonent) {
        abonent.setId(currentId++);
        abonents.add(abonent);
        saveToFile();
        return abonent;
    }

    @Override
    public void update(Abonent updated) {
        for (int i = 0; i < abonents.size(); i++) {
            if (abonents.get(i).getId() == updated.getId()) {
                abonents.set(i, updated);
                saveToFile();
                return;
            }
        }
    }

    @Override
    public boolean deleteById(int id) {
        boolean removed = abonents.removeIf(a -> a.getId() == id);
        if (removed) {
            saveToFile();
        }
        return removed;
    }

    @Override
    public Optional<Abonent> findById(int id) {
        return abonents.stream()
                .filter(a -> a.getId() == id)
                .findFirst();
    }

    @Override
    public List<Abonent> findAll() {
        List<Abonent> sorted = new ArrayList<>(abonents);
        Collections.sort(sorted);
        return sorted;
    }

    @Override
    public List<Abonent> findByLastName(String lastName) {
        String query = lastName.trim().toLowerCase();
        return abonents.stream()
                .filter(a -> a.getLastName().toLowerCase().contains(query))
                .sorted()
                .collect(Collectors.toList());
    }

    @Override
    public List<Abonent> findByPhone(String phone) {
        String query = phone.trim().toLowerCase();
        return abonents.stream()
                .filter(a -> (!a.getPhone1().isEmpty() && a.getPhone1().toLowerCase().contains(query)) ||
                             (!a.getPhone2().isEmpty() && a.getPhone2().toLowerCase().contains(query)) ||
                             (!a.getPhone3().isEmpty() && a.getPhone3().toLowerCase().contains(query)))
                .sorted()
                .collect(Collectors.toList());
    }

    private void loadFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] parts = line.split(";", -1);
                if (parts.length >= 6) {
                    int id = Integer.parseInt(parts[0]);
                    Abonent abonent = new Abonent(id, parts[1], parts[2], parts[3], parts[4], parts[5]);
                    abonents.add(abonent);
                    if (id >= currentId) {
                        currentId = id + 1;
                    }
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Ошибка чтения файла данных: " + e.getMessage());
        }
    }

    private void saveToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, StandardCharsets.UTF_8))) {
            for (Abonent a : abonents) {
                String line = String.join(";",
                        String.valueOf(a.getId()),
                        a.getLastName(),
                        a.getFirstName(),
                        a.getPhone1(),
                        a.getPhone2(),
                        a.getPhone3()
                );
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Ошибка сохранения файла данных: " + e.getMessage());
        }
    }
}
