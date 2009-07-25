package edu.colorado.phet.wickettest.data;

import java.util.Locale;

public class LocalizedSimulation {
    private int id;
    private Locale locale;
    private String title;
    private String description;
    private Simulation simulation;

    public LocalizedSimulation() {
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

    public Simulation getSimulation() {
        return simulation;
    }

    public void setSimulation( Simulation simulation ) {
        this.simulation = simulation;
    }
}
