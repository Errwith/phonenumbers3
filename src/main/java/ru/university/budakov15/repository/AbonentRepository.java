package ru.university.budakov15.repository;

import ru.university.budakov15.model.Abonent;

import java.util.List;
import java.util.Optional;

public interface AbonentRepository {
    Abonent save(Abonent abonent);
    void update(Abonent abonent);
    boolean deleteById(int id);
    Optional<Abonent> findById(int id);
    List<Abonent> findAll();
    List<Abonent> findByLastName(String lastName);
    List<Abonent> findByPhone(String phone);
}
