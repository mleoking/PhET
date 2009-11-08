package edu.colorado.phet.website.data;

import java.io.Serializable;
import java.util.*;

import edu.colorado.phet.common.phetcommon.util.LocaleUtils;

public class Simulation implements Serializable {
    private int id;
    private String name;
    private int type;
    private Project project;

    // TODO: put kilobytes into database
    private int kilobytes;
    private Set localizedSimulations = new HashSet();
    private Set categories = new HashSet();

    private List topics = new LinkedList();
    private List keywords = new LinkedList();

    private String designTeam;
    private String libraries;
    private String thanksTo;

    private boolean underConstruction;
    private boolean guidanceRecommended;
    private boolean classroomTested;

    public static final int TYPE_JAVA = 0;
    public static final int TYPE_FLASH = 1;

    public Simulation() {
    }

    public String getDescriptionKey() {
        return "simulation." + name + ".description";
    }

    public String getLearningGoalsKey() {
        return "simulation." + name + ".learningGoals";
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

    public int getKilobytes() {
        return kilobytes;
    }

    public void setKilobytes( int kilobytes ) {
        this.kilobytes = kilobytes;
    }

    public List getTopics() {
        return topics;
    }

    public void setTopics( List topics ) {
        this.topics = topics;
    }

    public String getDesignTeam() {
        return designTeam;
    }

    public void setDesignTeam( String designTeam ) {
        this.designTeam = designTeam;
    }

    public String getLibraries() {
        return libraries;
    }

    public void setLibraries( String libraries ) {
        this.libraries = libraries;
    }

    public String getThanksTo() {
        return thanksTo;
    }

    public void setThanksTo( String thanksTo ) {
        this.thanksTo = thanksTo;
    }

    public boolean isUnderConstruction() {
        return underConstruction;
    }

    public void setUnderConstruction( boolean underConstruction ) {
        this.underConstruction = underConstruction;
    }

    public boolean isGuidanceRecommended() {
        return guidanceRecommended;
    }

    public void setGuidanceRecommended( boolean guidanceRecommended ) {
        this.guidanceRecommended = guidanceRecommended;
    }

    public boolean isClassroomTested() {
        return classroomTested;
    }

    public void setClassroomTested( boolean classroomTested ) {
        this.classroomTested = classroomTested;
    }
}
