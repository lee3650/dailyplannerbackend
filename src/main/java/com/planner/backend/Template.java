package com.planner.backend;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class Template {
    private @Id
    @GeneratedValue
    Long id;

    public Template()
    {

    }

    public Template(String name)
    {
        this.name = name;
    }

    public Template(List<EventData> events, Account owner, String name) {
        this.events = events;
        this.owner = owner;
        this.name = name;
    }

    public List<EventData> getEvents() {
        return events;
    }

    public void setEvents(List<EventData> events) {
        this.events = events;
    }

    @OneToMany(mappedBy = "template", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<EventData> events;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "account_id", nullable = false)
    private Account owner;

    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Account getOwner() {
        return owner;
    }

    public void setOwner(Account owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Template{" +
                "id=" + id +
                ", events=" + events +
                ", owner=" + owner +
                ", name='" + name + '\'' +
                '}';
    }
}
