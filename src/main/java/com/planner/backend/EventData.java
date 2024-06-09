package com.planner.backend;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
public class EventData {
    @GeneratedValue
    @Id
    Long id;

    public EventData clone(Template newParent)
    {
        return new EventData(startTime, endTime, name, newParent);
    }

    private int startTime;
    private int endTime;
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }

    @ManyToOne
    @JoinColumn(name = "template_id")
    @JsonBackReference
    private Template template;

    public EventData(int startTime, int endTime, String name, Template template) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.template = template;
        this.name = name;
    }

    public EventData()
    {

    }

    public Template getTemplate() {
        return template;
    }

    public void setTemplate(Template template) {
        this.template = template;
    }

    @Override
    public String toString() {
        return "EventData{" +
                "id=" + id +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", template_id=" + template.getId() +
                '}';
    }
}
