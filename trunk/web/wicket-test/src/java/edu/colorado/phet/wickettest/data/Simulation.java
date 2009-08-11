package edu.colorado.phet.wickettest.data;

import java.io.Serializable;
import java.util.*;

import edu.colorado.phet.common.phetcommon.util.LocaleUtils;

public class Simulation implements Serializable {
    private int id;
    private String name;
    private int type;
    private Project project;
    private Set localizedSimulations = new HashSet();
    private Set categories = new HashSet();

    //    private List topics = new LinkedList();
    private List keywords = new LinkedList();
//    private List learningGoalKey = new LinkedList();

//    private List designTeam = new LinkedList();
//    private List libraries = new LinkedList();
//    private List thanksTo = new LinkedList();

    public static final int TYPE_JAVA = 0;
    public static final int TYPE_FLASH = 1;

    public Simulation() {
    }

    public LocalizedSimulation getBestLocalizedSimulation( Locale bestLocale ) {
        LocalizedSimulation englishSimulation = null;
        Locale englishLocale = LocaleUtils.stringToLocale( "en" );
        for ( Object localizedSimulation : localizedSimulations ) {
            LocalizedSimulation sim = (LocalizedSimulation) localizedSimulation;
            if ( sim.getLocale().equals( bestLocale ) ) {
                return sim;
            }
            else if ( sim.getLocale().equals( englishLocale ) ) {
                englishSimulation = sim;
            }
        }
        return englishSimulation;
    }

    public String getThumbnailUrl() {
        return "/sims/" + getProject().getName() + "/" + getName() + "-thumbnail.jpg";
    }

    public String getImageUrl() {
        return "/sims/" + getProject().getName() + "/" + getName() + "-screenshot.png";
    }

    public boolean isJava() {
        return getType() == 0;
    }

    public boolean isFlash() {
        return getType() == 1;
    }

    // getters and setters

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

    public Project getProject() {
        return project;
    }

    public void setProject( Project project ) {
        this.project = project;
    }

    public Set getLocalizedSimulations() {
        return localizedSimulations;
    }

    public void setLocalizedSimulations( Set localizedSimulations ) {
        this.localizedSimulations = localizedSimulations;
    }

    public Set getCategories() {
        return categories;
    }

    public void setCategories( Set categories ) {
        this.categories = categories;
    }

    public List getKeywords() {
        return keywords;
    }

    public void setKeywords( List keywords ) {
        this.keywords = keywords;
    }
}
