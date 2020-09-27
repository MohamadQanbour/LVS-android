package com.littlevillageschool.lvs.Model;

import java.io.Serializable;

/**
 * Created by Alalaa Center on 11/08/2016.
 */
public class Person implements Serializable{

    private String id;
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() { return name; }

    @Override
    public boolean equals(Object o) {
        return this.name.equalsIgnoreCase(((Person)o).getName());
    }
}
