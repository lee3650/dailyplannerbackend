package com.planner.backend;

import jakarta.persistence.*;

@Entity
public class EventData {
    @GeneratedValue
    @Id
    Long id;

    private int startTime;
    private int endTime;

    @ManyToOne
    @JoinColumn(name = "template_id")
    private Template template;

    public EventData(int startTime, int endTime, Template template) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.template = template;
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
