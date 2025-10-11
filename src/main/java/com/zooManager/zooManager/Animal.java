package com.zooManager.zooManager;

import jakarta.persistence.*;

import java.util.*;
@Entity
public class Animal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String species;
    private boolean currentVaccination;
    private String imagePath;
@ManyToMany
  private List<Employee> employees = new ArrayList<>();



  public Animal(){

  }

    public Animal(String name, String species, boolean currentVaccination, List<Employee> employees,String imagePath) {
        this.name = name;
        this.species = species;
        this.currentVaccination = currentVaccination;
        this.employees = employees;
        this.imagePath = imagePath;
    }
    public Animal(Long id,String name,String species,boolean currentVaccination){
      this.id = id;
      this.name = name;
      this.species = species;
      this.currentVaccination = currentVaccination;
      this.employees = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public boolean getCurrentVaccination() {
        return currentVaccination;
    }

    public void setCurrentVaccination(boolean currentVaccination) {
        this.currentVaccination = currentVaccination;
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    @Override
    public String toString() {
        return "Animal{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", species='" + species + '\'' +
                ", currentVaccination=" + currentVaccination +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Animal animal = (Animal) o;
        return Objects.equals(id, animal.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}




