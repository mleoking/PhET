package edu.colorado.phet.wickettest;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SimulationSQL {
    private String project;
    private String simulation;
    private int type;
    private String title;
    private String description;
    private String localeString;

    public SimulationSQL( ResultSet ret ) throws SQLException {
        project = ret.getString( "project" );
        simulation = ret.getString( "simulation" );
        type = ret.getInt( "sim_type" );
        title = ret.getString( "title" );
        description = ret.getString( "description" );
        localeString = ret.getString( "locale" );
    }

    public String getProject() {
        return project;
    }

    public String getSimulation() {
        return simulation;
    }

    public int getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getLocaleString() {
        return localeString;
    }
}
