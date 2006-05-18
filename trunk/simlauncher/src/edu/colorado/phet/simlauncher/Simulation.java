/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.simlauncher;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.util.*;
import java.net.URL;

/**
 * Simulation
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Simulation {

    //--------------------------------------------------------------------------------------------------
    // Class fields and methods
    //--------------------------------------------------------------------------------------------------

    private static List simulations;
    private static List uninstalledSims;
    private static List installedSims;
    private static ArrayList listeners = new ArrayList();
    private static HashMap namesToSims;

    static {
        namesToSims = new HashMap( );
        simulations = new SimulationFactory().getSimulations( "simulations.xml" );
        uninstalledSims = new ArrayList( simulations );
        installedSims = new ArrayList();
    }

    public static List getAllInstances() {
        return simulations;
    }

    public static List getUninstalledSims() {
        return uninstalledSims;
    }

    public static List getInstalledSims() {
        return installedSims;
    }

    public static Simulation getInstanceForName( String name ) {
        Simulation sim = (Simulation)namesToSims.get( name );
        if( sim == null ) {
            throw new IllegalArgumentException( "name not recognized");
        }
        return sim;
    }

    //--------------------------------------------------------------------------------------------------
    // Event/Listener mechanism for class-level state
    //--------------------------------------------------------------------------------------------------
    
    public static void addListener( ChangeListener listener ) {
        listeners.add( listener );
    }

    public static void removeListener( ChangeListener listener ) {
        listeners.remove( listener );
    }

    public static interface ChangeListener extends EventListener {
        void instancesChanged();
    }

    private static void notifyListeners() {
        for( int i = 0; i < listeners.size(); i++ ) {
            ChangeListener changeListener = (ChangeListener)listeners.get( i );
            changeListener.instancesChanged();
        }
    }


    //--------------------------------------------------------------------------------------------------
    // Instance fields and methods
    //--------------------------------------------------------------------------------------------------

    private String name;
    private String description;
    private ImageIcon thumbnail;
    private URL jnlpUrl;

    /**
     * Constructor
     *
     * @param name
     * @param description
     * @param thumbnail
     * @param jnlpUrl
     */
    public Simulation( String name, String description, ImageIcon thumbnail, URL jnlpUrl ) {
        this.name = name;
        this.description = description;
        this.thumbnail = thumbnail;
        this.jnlpUrl = jnlpUrl;
        namesToSims.put( name, this );
    }

    public String toString() {
        return name;
    }

    /**
     *
     */
    public void install() {
        uninstalledSims.remove( this );
        installedSims.add( this );
        notifyListeners();
    }

    public void uninstall() {
        installedSims.remove( this );
        uninstalledSims.add( this );
        notifyListeners();
    }

    /**
     * Launches the simulation
     */
    public void launch() {
    }

    /**
     * Tells if this instance is current with the version on the PhET web site
     * @return true if the local version is current
     */
    public boolean isCurrent() {
        return true;
    }

    /**
     * Updates the local version of the simulation with the one on the PhET web site
     */
    public void update() {
    }

    //--------------------------------------------------------------------------------------------------
    // Setters and getters
    //--------------------------------------------------------------------------------------------------

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public ImageIcon getThumbnail() {
        return thumbnail;
    }
}
