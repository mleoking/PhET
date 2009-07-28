package edu.colorado.phet.wickettest.data;

import java.util.LinkedList;
import java.util.List;

public class Category {
    private int id;
    private String name;
    private boolean auto;
    private boolean root;
    private List subcategories = new LinkedList();
    private List simulations = new LinkedList();
    private Category parent;

    public Category() {
    }

    public Category( Category parent, String name ) {
        this.parent = parent;
        this.name = name;
        if ( parent == null ) {
            auto = true;
            root = true;
        }
        else if ( parent.isRoot() ) {
            auto = true;
            root = false;
        }
        else {
            auto = false;
            root = false;
        }

        if ( !root ) {
            parent.subcategories.add( this );
        }
    }

    public String getCategoryPath() {
        if ( isRoot() ) {
            return "";
        }
        else if ( getParent().isRoot() ) {
            return getName();
        }
        else {
            return getParent().getCategoryPath() + "/" + getName();
        }
    }

    public int getId() {
        return id;
    }

    public void setId( int id ) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public boolean isAuto() {
        return auto;
    }

    public void setAuto( boolean auto ) {
        this.auto = auto;
    }

    public boolean isRoot() {
        return root;
    }

    public void setRoot( boolean root ) {
        this.root = root;
    }

    public List getSubcategories() {
        return subcategories;
    }

    public void setSubcategories( List subcategories ) {
        this.subcategories = subcategories;
    }

    public List getSimulations() {
        return simulations;
    }

    public void setSimulations( List simulations ) {
        this.simulations = simulations;
    }

    public Category getParent() {
        return parent;
    }

    public void setParent( Category parent ) {
        this.parent = parent;
    }
}
