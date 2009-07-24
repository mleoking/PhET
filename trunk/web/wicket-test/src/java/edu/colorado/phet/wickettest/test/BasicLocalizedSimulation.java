package edu.colorado.phet.wickettest.test;

import java.util.Locale;

public class BasicLocalizedSimulation {
    private int id;
    private Locale locale;
    private String title;
    private String description;
    private BasicSimulation simulation;

    public BasicLocalizedSimulation() {
    }

    public int getId() {
        return id;
    }

    public void setId( int id ) {
        this.id = id;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale( Locale locale ) {
        this.locale = locale;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle( String title ) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription( String description ) {
        this.description = description;
    }

    public BasicSimulation getSimulation() {
        return simulation;
    }

    public void setSimulation( BasicSimulation simulation ) {
        this.simulation = simulation;
    }
}
