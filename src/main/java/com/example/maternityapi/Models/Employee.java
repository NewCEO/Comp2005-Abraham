package com.example.maternityapi.Models;

public class Employee {
    private int id;
    private String surname;
    private String forename;

    public Employee() {}

    public Employee(int id, String surname, String forename) {
        this.id = id;
        this.surname = surname;
        this.forename = forename;
    }

    public int getId() {
        return id;
    }

    public String getSurname() {
        return surname;
    }

    public String getForename() {
        return forename;
    }
}
