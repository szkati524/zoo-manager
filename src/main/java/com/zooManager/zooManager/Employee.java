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
    private String surname;
    private String email;

    private String profession;
    private String imagePath;
    @Transient
    private List<Long> animalIds = new ArrayList<>();
@ManyToMany(mappedBy = "employees")
  private List<Animal> animals = new ArrayList<>();

@OneToMany(mappedBy = "employee")
private List<Document> documents = new ArrayList<>();


    public Employee(){

    }

    public Employee(String name, String surname, String email, String profession, List<Animal> animals,List<Document> documents,String imagePath) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.profession = profession;
        this.animals = animals;
        this.documents = documents;
        this.imagePath = imagePath;
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

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public List<Animal> getAnimals() {
        return animals;
    }

    public void setAnimals(List<Animal> animals) {
        this.animals = animals;
    }

    public List<Long> getAnimalIds() {
        return animalIds;
    }

    public void setAnimalIds(List<Long> animalIds) {
        this.animalIds = animalIds;
    }
    public List<Document> getDocuments() {
        return documents;
    }
    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", email='" + email + '\'' +
                ", profession='" + profession + '\'' +
                ", animals=" + animals +
                '}';
    }
}