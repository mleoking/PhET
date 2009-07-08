/* Copyright 2009, University of Colorado */

package edu.colorado.phet.naturalselection.persistence;

import edu.colorado.phet.common.phetcommon.util.IProguardKeepClass;

public class NaturalSelectionConfig implements IProguardKeepClass {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // Global config
    private String versionString;
    private String versionMajor;
    private String versionMinor;
    private String versionDev;
    private String versionRevision;


    // model config
    private double clockDt;
    private boolean clockPaused;
    private double clockTime;
    private int climate;
    private int selectionFactor;
    private double time;
    private double lastYearTick;
    private double lastEventTick;
    private int generation;
    private boolean gameEnded;
    private int lastFrenziedGeneration;
    private boolean friendAdded;

    private int rootFatherId;
    private int rootMotherId;

    private boolean colorRegularDominant;
    private boolean teethRegularDominant;
    private boolean tailRegularDominant;

    private boolean colorMutated;
    private boolean teethMutated;
    private boolean tailMutated;


    private BunnyConfig[] bunnies;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Zero-argument constructor for Java Bean compliance, required by XMLEncoder.
     */
    public NaturalSelectionConfig() {

    }

    //----------------------------------------------------------------------------
    // Accessors for global information
    //----------------------------------------------------------------------------

    public String getVersionString() {
        return versionString;
    }

    public void setVersionString( String versionString ) {
        this.versionString = versionString;
    }

    public String getVersionMajor() {
        return versionMajor;
    }

    public void setVersionMajor( String versionMajor ) {
        this.versionMajor = versionMajor;
    }

    public String getVersionMinor() {
        return versionMinor;
    }

    public void setVersionMinor( String versionMinor ) {
        this.versionMinor = versionMinor;
    }

    public String getVersionDev() {
        return versionDev;
    }

    public void setVersionDev( String versionDev ) {
        this.versionDev = versionDev;
    }

    public String getVersionRevision() {
        return versionRevision;
    }

    public void setVersionRevision( String versionRevision ) {
        this.versionRevision = versionRevision;
    }

    public BunnyConfig[] getBunnies() {
        return bunnies;
    }

    public void setBunnies( BunnyConfig[] bunnies ) {
        this.bunnies = bunnies;
    }

    public double getClockDt() {
        return clockDt;
    }

    public void setClockDt( double clockDt ) {
        this.clockDt = clockDt;
    }

    public boolean isClockPaused() {
        return clockPaused;
    }

    public void setClockPaused( boolean clockPaused ) {
        this.clockPaused = clockPaused;
    }

    public double getClockTime() {
        return clockTime;
    }

    public void setClockTime( double clockTime ) {
        this.clockTime = clockTime;
    }

    public int getClimate() {
        return climate;
    }

    public void setClimate( int climate ) {
        this.climate = climate;
    }

    public int getSelectionFactor() {
        return selectionFactor;
    }

    public void setSelectionFactor( int selectionFactor ) {
        this.selectionFactor = selectionFactor;
    }

    public double getTime() {
        return time;
    }

    public void setTime( double time ) {
        this.time = time;
    }

    public double getLastYearTick() {
        return lastYearTick;
    }

    public void setLastYearTick( double lastYearTick ) {
        this.lastYearTick = lastYearTick;
    }

    public double getLastEventTick() {
        return lastEventTick;
    }

    public void setLastEventTick( double lastEventTick ) {
        this.lastEventTick = lastEventTick;
    }

    public int getGeneration() {
        return generation;
    }

    public void setGeneration( int generation ) {
        this.generation = generation;
    }

    public boolean isGameEnded() {
        return gameEnded;
    }

    public void setGameEnded( boolean gameEnded ) {
        this.gameEnded = gameEnded;
    }

    public int getLastFrenziedGeneration() {
        return lastFrenziedGeneration;
    }

    public void setLastFrenziedGeneration( int lastFrenziedGeneration ) {
        this.lastFrenziedGeneration = lastFrenziedGeneration;
    }

    public boolean isFriendAdded() {
        return friendAdded;
    }

    public void setFriendAdded( boolean friendAdded ) {
        this.friendAdded = friendAdded;
    }

    public int getRootFatherId() {
        return rootFatherId;
    }

    public void setRootFatherId( int rootFatherId ) {
        this.rootFatherId = rootFatherId;
    }

    public int getRootMotherId() {
        return rootMotherId;
    }

    public void setRootMotherId( int rootMotherId ) {
        this.rootMotherId = rootMotherId;
    }

    public boolean isColorRegularDominant() {
        return colorRegularDominant;
    }

    public void setColorRegularDominant( boolean colorRegularDominant ) {
        this.colorRegularDominant = colorRegularDominant;
    }

    public boolean isTeethRegularDominant() {
        return teethRegularDominant;
    }

    public void setTeethRegularDominant( boolean teethRegularDominant ) {
        this.teethRegularDominant = teethRegularDominant;
    }

    public boolean isTailRegularDominant() {
        return tailRegularDominant;
    }

    public void setTailRegularDominant( boolean tailRegularDominant ) {
        this.tailRegularDominant = tailRegularDominant;
    }

    public boolean isColorMutated() {
        return colorMutated;
    }

    public void setColorMutated( boolean colorMutated ) {
        this.colorMutated = colorMutated;
    }

    public boolean isTeethMutated() {
        return teethMutated;
    }

    public void setTeethMutated( boolean teethMutated ) {
        this.teethMutated = teethMutated;
    }

    public boolean isTailMutated() {
        return tailMutated;
    }

    public void setTailMutated( boolean tailMutated ) {
        this.tailMutated = tailMutated;
    }
}
