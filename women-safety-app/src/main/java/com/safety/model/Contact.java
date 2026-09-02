package com.safety.model;

import java.util.UUID;

public class Contact {

    private final String id;
    private String name;
    private String phoneNumber;
    private String relation;
    private boolean isPrimary;

    public Contact(String name, String phoneNumber, String relation, boolean isPrimary) {
        this.id = UUID.randomUUID().toString().substring(0, 8);
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.relation = relation;
        this.isPrimary = isPrimary;
    }

    public Contact(String id, String name, String phoneNumber, String relation, boolean isPrimary) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.relation = relation;
        this.isPrimary = isPrimary;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public boolean isPrimary() {
        return isPrimary;
    }

    public void setPrimary(boolean primary) {
        isPrimary = primary;
    }

    @Override
    public String toString() {
        return String.format("%s (%s) - %s %s", name, relation, phoneNumber, isPrimary ? "[PRIMARY]" : "");
    }
}
