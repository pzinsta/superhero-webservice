package com.pzinsta.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class Superhero {

    @Id
    @GeneratedValue(generator = "ID_GENERATOR")
    private Long id;
    private String name;
    private String pseudonym;
    private String publisher;
    private List<String> skills = new ArrayList<>();
    private List<String> allies = new ArrayList<>();
    private LocalDate firstAppearance;

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

    public String getPseudonym() {
        return pseudonym;
    }

    public void setPseudonym(String pseudonym) {
        this.pseudonym = pseudonym;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }

    public List<String> getAllies() {
        return allies;
    }

    public void setAllies(List<String> allies) {
        this.allies = allies;
    }

    public LocalDate getFirstAppearance() {
        return firstAppearance;
    }

    public void setFirstAppearance(LocalDate firstAppearance) {
        this.firstAppearance = firstAppearance;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null || !(that instanceof Superhero)) {
            return false;
        }
        Superhero superhero = (Superhero) that;
        return Objects.equals(getName(), superhero.getName()) &&
                Objects.equals(getPseudonym(), superhero.getPseudonym()) &&
                Objects.equals(getPublisher(), superhero.getPublisher()) &&
                Objects.equals(getSkills(), superhero.getSkills()) &&
                Objects.equals(getAllies(), superhero.getAllies()) &&
                Objects.equals(getFirstAppearance(), superhero.getFirstAppearance());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getPseudonym(), getPublisher(), getSkills(), getAllies(), getFirstAppearance());
    }
}
