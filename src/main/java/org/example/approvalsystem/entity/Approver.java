package org.example.approvalsystem.entity;

import lombok.ToString;

@ToString
public class Approver implements Person {

    private String id;
    private String name;

    public Approver(String id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
