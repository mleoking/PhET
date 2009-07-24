package edu.colorado.phet.wickettest.test;

public class Honey {
    private Integer id;
    private String name;
    private String taste;

    public Honey() {

    }

    public Integer getId() {
        return id;
    }

    public void setId( Integer id ) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public String getTaste() {
        return taste;
    }

    public void setTaste( String taste ) {
        this.taste = taste;
    }

    public String toString() {
        return "Honey: " + getId() + " Name: " + getName() + " Taste: " + getTaste();
    }
}