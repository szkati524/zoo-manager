package com.zooManager.zooManager;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
@Entity
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String surName;
    private String email;

   /* private List<Animal> animalsToZooKeeper = new ArrayList<>(); */


    public Employee(){

    }

    public Employee(String name, String surName, String email) {
        this.name = name;
        this.surName = surName;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurName() {
        return surName;
    }

    public void setSurName(String surName) {
        this.surName = surName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


}