package edu.colorado.phet.tomcattest;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SimulationSQL {
    private int simId;
    private String simName;
    private String simDirName;
    private String simFlavorName;
    private int simRating;
    private boolean simNoMac;
    private boolean simCrutch;
    private int simType;
    private int simSize;
    private String simLaunchUrl; // deprecated!!!
    private String simImageUrl;
    private String simDesc;
    private String simKeywords;
    private String simSystemReq;
    private String simTeachersGuideId;
    private String simMainTopics;
    private String simDesignTeam;
    private String simLibraries;
    private String simThanksTo;
    private String simSampleGoals;
    private String simSortingName;
    private String simAnimatedImageUrl;
    private boolean simIsReal;

    public SimulationSQL( ResultSet ret ) throws SQLException {
        simId = ret.getInt( "sim_id" );
        simName = ret.getString( "sim_name" );
        simDirName = ret.getString( "sim_dirname" );
        simFlavorName = ret.getString( "sim_flavorname" );
        simRating = ret.getInt( "sim_rating" );
        simNoMac = ret.getBoolean( "sim_no_mac" );
        simCrutch = ret.getBoolean( "sim_crutch" );
        simType = ret.getInt( "sim_type" );
        simSize = ret.getInt( "sim_size" );
        simLaunchUrl = ret.getString( "sim_launch_url" );
        simImageUrl = ret.getString( "sim_image_url" );
        simDesc = ret.getString( "sim_desc" );
        simKeywords = ret.getString( "sim_keywords" );
        simSystemReq = ret.getString( "sim_system_req" );
        simTeachersGuideId = ret.getString( "sim_teachers_guide_id" );
        simMainTopics = ret.getString( "sim_main_topics" );
        simDesignTeam = ret.getString( "sim_design_team" );
        simLibraries = ret.getString( "sim_libraries" );
        simThanksTo = ret.getString( "sim_thanks_to" );
        simSampleGoals = ret.getString( "sim_sample_goals" );
        simSortingName = ret.getString( "sim_sorting_name" );
        simAnimatedImageUrl = ret.getString( "sim_animated_image_url" );
        simIsReal = ret.getBoolean( "sim_is_real" );
    }

    public int getSimId() {
        return simId;
    }

    public void setSimId( int simId ) {
        this.simId = simId;
    }

    public String getSimName() {
        return simName;
    }

    public void setSimName( String simName ) {
        this.simName = simName;
    }

    public String getSimDirName() {
        return simDirName;
    }

    public void setSimDirName( String simDirName ) {
        this.simDirName = simDirName;
    }

    public String getSimFlavorName() {
        return simFlavorName;
    }

    public void setSimFlavorName( String simFlavorName ) {
        this.simFlavorName = simFlavorName;
    }

    public int getSimRating() {
        return simRating;
    }

    public void setSimRating( int simRating ) {
        this.simRating = simRating;
    }

    public boolean isSimNoMac() {
        return simNoMac;
    }

    public void setSimNoMac( boolean simNoMac ) {
        this.simNoMac = simNoMac;
    }

    public boolean isSimCrutch() {
        return simCrutch;
    }

    public void setSimCrutch( boolean simCrutch ) {
        this.simCrutch = simCrutch;
    }

    public int getSimType() {
        return simType;
    }

    public void setSimType( int simType ) {
        this.simType = simType;
    }

    public int getSimSize() {
        return simSize;
    }

    public void setSimSize( int simSize ) {
        this.simSize = simSize;
    }

    public String getSimLaunchUrl() {
        return simLaunchUrl;
    }

    public void setSimLaunchUrl( String simLaunchUrl ) {
        this.simLaunchUrl = simLaunchUrl;
    }

    public String getSimImageUrl() {
        return simImageUrl;
    }

    public void setSimImageUrl( String simImageUrl ) {
        this.simImageUrl = simImageUrl;
    }

    public String getSimDesc() {
        return simDesc;
    }

    public void setSimDesc( String simDesc ) {
        this.simDesc = simDesc;
    }

    public String getSimKeywords() {
        return simKeywords;
    }

    public void setSimKeywords( String simKeywords ) {
        this.simKeywords = simKeywords;
    }

    public String getSimSystemReq() {
        return simSystemReq;
    }

    public void setSimSystemReq( String simSystemReq ) {
        this.simSystemReq = simSystemReq;
    }

    public String getSimTeachersGuideId() {
        return simTeachersGuideId;
    }

    public void setSimTeachersGuideId( String simTeachersGuideId ) {
        this.simTeachersGuideId = simTeachersGuideId;
    }

    public String getSimMainTopics() {
        return simMainTopics;
    }

    public void setSimMainTopics( String simMainTopics ) {
        this.simMainTopics = simMainTopics;
    }

    public String getSimDesignTeam() {
        return simDesignTeam;
    }

    public void setSimDesignTeam( String simDesignTeam ) {
        this.simDesignTeam = simDesignTeam;
    }

    public String getSimLibraries() {
        return simLibraries;
    }

    public void setSimLibraries( String simLibraries ) {
        this.simLibraries = simLibraries;
    }

    public String getSimThanksTo() {
        return simThanksTo;
    }

    public void setSimThanksTo( String simThanksTo ) {
        this.simThanksTo = simThanksTo;
    }

    public String getSimSampleGoals() {
        return simSampleGoals;
    }

    public void setSimSampleGoals( String simSampleGoals ) {
        this.simSampleGoals = simSampleGoals;
    }

    public String getSimSortingName() {
        return simSortingName;
    }

    public void setSimSortingName( String simSortingName ) {
        this.simSortingName = simSortingName;
    }

    public String getSimAnimatedImageUrl() {
        return simAnimatedImageUrl;
    }

    public void setSimAnimatedImageUrl( String simAnimatedImageUrl ) {
        this.simAnimatedImageUrl = simAnimatedImageUrl;
    }

    public boolean isSimIsReal() {
        return simIsReal;
    }

    public void setSimIsReal( boolean simIsReal ) {
        this.simIsReal = simIsReal;
    }
}
