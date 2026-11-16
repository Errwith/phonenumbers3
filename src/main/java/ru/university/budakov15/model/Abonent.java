package ru.university.budakov15.model;

import java.util.Objects;

public class Abonent implements Comparable<Abonent> {
    private int id;
    private String lastName;
    private String firstName;
    private String phone1;
    private String phone2;
    private String phone3;

    public Abonent(int id, String lastName, String firstName, String phone1, String phone2, String phone3) {
        this.id = id;
        setLastName(lastName);
        setFirstName(firstName);
        setPhone1(phone1);
        setPhone2(phone2);
        setPhone3(phone3);
    }

    public Abonent(String lastName, String firstName, String phone1, String phone2, String phone3) {
        this(0, lastName, firstName, phone1, phone2, phone3);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName != null ? lastName.trim() : ""; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName != null ? firstName.trim() : ""; }

    public String getPhone1() { return phone1; }
    public void setPhone1(String phone1) { this.phone1 = phone1 != null ? phone1.trim() : ""; }

    public String getPhone2() { return phone2; }
    public void setPhone2(String phone2) { this.phone2 = phone2 != null ? phone2.trim() : ""; }

    public String getPhone3() { return phone3; }
    public void setPhone3(String phone3) { this.phone3 = phone3 != null ? phone3.trim() : ""; }

    @Override
    public int compareTo(Abonent o) {
        int res = this.lastName.compareToIgnoreCase(o.lastName);
        if (res != 0) return res;

        res = this.firstName.compareToIgnoreCase(o.firstName);
        if (res != 0) return res;

        res = comparePhones(this.phone1, o.phone1);
        if (res != 0) return res;

        res = comparePhones(this.phone2, o.phone2);
        if (res != 0) return res;

        return comparePhones(this.phone3, o.phone3);
    }

    private int comparePhones(String p1, String p2) {
        if (p1.isEmpty() && p2.isEmpty()) return 0;
        if (p1.isEmpty()) return 1;  // Пустые номера уходят в конец при сортировке
        if (p2.isEmpty()) return -1;
        return p1.compareToIgnoreCase(p2);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Abonent abonent = (Abonent) o;
        return id == abonent.id;
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return "Абонент {" +
                "id=" + id +
                ", Фамилия='" + lastName + '\'' +
                ", Имя='" + firstName + '\'' +
                ", Телефон 1='" + (phone1.isEmpty() ? "-" : phone1) + '\'' +
                ", Телефон 2='" + (phone2.isEmpty() ? "-" : phone2) + '\'' +
                ", Телефон 3='" + (phone3.isEmpty() ? "-" : phone3) + '\'' +
                '}';
    }
}