package ru.university.budakov15.service;

import ru.university.budakov15.exception.*;
import ru.university.budakov15.model.Abonent;
import ru.university.budakov15.repository.AbonentRepository;

import java.util.List;

public class AbonentService {
    private final AbonentRepository repository;

    public AbonentService(AbonentRepository repository) {
        this.repository = repository;
    }

    public Abonent add(String lastName, String firstName, String phone1, String phone2, String phone3) {
        validate(lastName, firstName, phone1, phone2, phone3);
        Abonent abonent = new Abonent(lastName, firstName, phone1, phone2, phone3);
        return repository.save(abonent);
    }

    public void update(int id, String lastName, String firstName, String phone1, String phone2, String phone3) {
        Abonent existing = repository.findById(id)
                .orElseThrow(() -> new AbonentNotFoundException("Абонент с id=" + id + " не найден"));

        validate(lastName, firstName, phone1, phone2, phone3);

        existing.setLastName(lastName);
        existing.setFirstName(firstName);
        existing.setPhone1(phone1);
        existing.setPhone2(phone2);
        existing.setPhone3(phone3);

        repository.update(existing);
    }

    public void delete(int id) {
        if (!repository.deleteById(id)) {
            throw new AbonentNotFoundException("Абонент с id=" + id + " не найден");
        }
    }

    public Abonent getById(int id) {
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
        List<Abonent> result = repository.findByLastName(lastName);
        if (result.isEmpty()) {
            throw new AbonentNotFoundException("Абоненты с фамилией '" + lastName + "' не найдены");
        }
        return result;
    }

    public List<Abonent> findByPhone(String phone) {
        if (phone == null || phone.isBlank()) {
            throw new EmptyPhoneException("Номер телефона для поиска не может быть пустым");
        }
        List<Abonent> result = repository.findByPhone(phone);
        if (result.isEmpty()) {
            throw new AbonentNotFoundException("Абоненты с телефоном '" + phone + "' не найдены");
        }
        return result;
    }

    private void validate(String lastName, String firstName, String phone1, String phone2, String phone3) {
        if (lastName == null || lastName.isBlank()) {
            throw new EmptyLastNameException("Фамилия не может быть пустой");
        }
        if (firstName == null || firstName.isBlank()) {
            throw new EmptyFirstNameException("Имя не может быть пустым");
        }
        boolean hasAtLeastOnePhone = (phone1 != null && !phone1.isBlank()) ||
                                     (phone2 != null && !phone2.isBlank()) ||
                                     (phone3 != null && !phone3.isBlank());
        if (!hasAtLeastOnePhone) {
            throw new EmptyPhoneException("У абонента должен быть указан хотя бы один номер телефона");
        }
    }
}
