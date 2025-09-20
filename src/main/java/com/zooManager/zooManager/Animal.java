package com.zooManager.zooManager;

import jakarta.persistence.*;

import java.util.*;
@Entity
public class Animal {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String species;

    /* private List<Employee> employeeList = new ArrayList<>(); */


    public Animal() {

    }

    public Animal(String name, String species) {
        this.name = name;
        this.species = species;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }


}




