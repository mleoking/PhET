package edu.colorado.phet.website.data;

import java.io.File;
import java.io.Serializable;
import java.util.*;

import edu.colorado.phet.common.phetcommon.util.LocaleUtils;
import edu.colorado.phet.website.data.util.IntId;

public class Simulation implements Serializable, IntId {
    private int id;
    private String name;
    private Project project;

    private int kilobytes;
    private Set localizedSimulations = new HashSet();
    private Set categories = new HashSet();
    private Set contributions = new HashSet();

    private List topics = new LinkedList();
    private List keywords = new LinkedList();

    private String designTeam;
    private String libraries;
    private String thanksTo;

    private boolean underConstruction;
    private boolean guidanceRecommended;
    private boolean classroomTested;
    private boolean simulationVisible;

    /**
     * What the simulation ID was on the old PHP site. Kept for redirections, etc.
     */
    private int oldId;

    public Simulation() {
    }

    public String getDescriptionKey() {
        return "simulation." + name + ".description";
    }

    public String getLearningGoalsKey() {
        return "simulation." + name + ".learningGoals";
    }

    /**
     * Returns the best matching localized simulation.
     * <p/>
     * Note: should be in a Hibernate session, so localizedSimulations is instantiated
     *
     * @param bestLocale
     * @return
     */
    public LocalizedSimulation getBestLocalizedSimulation( Locale bestLocale ) {
        LocalizedSimulation englishSimulation = null;
        LocalizedSimulation languageSimulation = null;
        Locale englishLocale = LocaleUtils.stringToLocale( "en" );
        Locale languageLocale = LocaleUtils.stringToLocale( bestLocale.getLanguage() );
        for ( Object localizedSimulation : localizedSimulations ) {
            LocalizedSimulation sim = (LocalizedSimulation) localizedSimulation;
            if ( sim.getLocale().equals( bestLocale ) ) {
                return sim;
            }
            else if ( sim.getLocale().equals( englishLocale ) ) {
                englishSimulation = sim;
            }
            else if ( sim.getLocale().equals( languageLocale ) ) {
                languageSimulation = sim;
            }
        }
        return languageSimulation != null ? languageSimulation : englishSimulation;
    }

    public LocalizedSimulation getEnglishSimulation() {
        return getBestLocalizedSimulation( LocaleUtils.stringToLocale( "en" ) );
    }

    public String getThumbnailUrl() {
        return "/sims/" + getProject().getName() + "/" + getName() + "-thumbnail.jpg";
    }

    public String getImageUrl() {
        return "/sims/" + getProject().getName() + "/" + getName() + "-screenshot.png";
    }

    public int detectSimKilobytes( File docRoot ) {
        File projectRoot = project.getProjectRoot( docRoot );
        switch( getType() ) {
            case Project.TYPE_JAVA:
                return (int) ( new File( projectRoot, project.getName() + "_all.jar" ) ).length() / 1000;
            case Project.TYPE_FLASH:
                return (int) ( new File( projectRoot, name + "_en.jar" ) ).length() / 1024;
            default:
                throw new RuntimeException( "Simulation type not handled? type = " + getType() );
        }
    }

    public static File getAllJar( File docRoot, String project ) {
        return new File( docRoot, "sims/" + project + "/" + project + "_all.jar" );
    }

    public static File getLocalizedJar( File docRoot, String project, String sim, Locale locale ) {
        return new File( docRoot, "sims/" + project + "/" + sim + "_" + LocaleUtils.localeToString( locale ) + ".jar" );
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
        return project.getType();
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

    public boolean isSimulationVisible() {
        return simulationVisible;
    }

    public void setSimulationVisible( boolean simulationVisible ) {
        this.simulationVisible = simulationVisible;
    }

    public boolean isVisible() {
        return isSimulationVisible() && project.isVisible();
    }

    public Set getContributions() {
        return contributions;
    }

    public void setContributions( Set contributions ) {
        this.contributions = contributions;
    }

    public int getOldId() {
        return oldId;
    }

    public void setOldId( int oldId ) {
        this.oldId = oldId;
    }
}
