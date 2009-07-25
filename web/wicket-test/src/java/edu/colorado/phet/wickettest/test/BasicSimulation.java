package edu.colorado.phet.wickettest.test;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import edu.colorado.phet.common.phetcommon.util.LocaleUtils;

public class BasicSimulation {
    private int id;
    private String name;
    private int type;
    private BasicProject project;
    private Set localizedSimulations = new HashSet();

    public static final int TYPE_JAVA = 0;
    public static final int TYPE_FLASH = 1;

    public BasicSimulation() {
    }

    public BasicLocalizedSimulation getBestLocalizedSimulation( Locale bestLocale ) {
        BasicLocalizedSimulation englishSimulation = null;
        Locale englishLocale = LocaleUtils.stringToLocale( "en" );
        for ( Object localizedSimulation : localizedSimulations ) {
            BasicLocalizedSimulation sim = (BasicLocalizedSimulation) localizedSimulation;
            if ( sim.getLocale().equals( bestLocale ) ) {
                return sim;
            }
            else if ( sim.getLocale().equals( englishLocale ) ) {
                englishSimulation = sim;
            }
        }
        return englishSimulation;
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

    public int getType() {
        return type;
    }

    public void setType( int type ) {
        this.type = type;
    }

    public BasicProject getProject() {
        return project;
    }

    public void setProject( BasicProject project ) {
        this.project = project;
    }

    public Set getLocalizedSimulations() {
        return localizedSimulations;
    }

    public void setLocalizedSimulations( Set localizedSimulations ) {
        this.localizedSimulations = localizedSimulations;
    }
}
