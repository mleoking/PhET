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
import java.util.List;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.EventObject;

/**
 * Simulation
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Simulation {

    private static List simulations = new SimulationFactory().getSimulations( "simulations.xml" );
    private static List uninstalledSims = new ArrayList( simulations );
    private static List installedSims = new ArrayList();
    private static ArrayList listeners = new ArrayList();

    public static void addListener( ChangeListener listener ) {
        listeners.add( listener );
    }

    public static void removeListener( ChangeListener listener ) {
        listeners.remove( listener );
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

    public static interface ChangeListener extends EventListener {
        void instancesChanged();
    }

    private static void notifyListeners() {
        for( int i = 0; i < listeners.size(); i++ ) {
            ChangeListener changeListener = (ChangeListener)listeners.get( i );
            changeListener.instancesChanged();
        }
    }


    String name;
    String description;
    ImageIcon thumbnail;

    public Simulation( String name, String description, ImageIcon thumbnail ) {
        this.name = name;
        this.description = description;
        this.thumbnail = thumbnail;
    }

    public String toString() {
        return name;
    }

    public void install() {
        uninstalledSims.remove( this );
        installedSims.add( this );
        notifyListeners();
    }

    public void uninstall() {
        installedSims.remove( this );
        uninstalledSims.add( this );
    }

    public void launch() {

    }
}
