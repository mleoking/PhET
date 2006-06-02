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

import edu.colorado.phet.common.util.EventChannel;

import java.util.EventListener;
import java.util.EventObject;

/**
 * Options
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Options {

    //--------------------------------------------------------------------------------------------------
    // Class fields and methods
    //--------------------------------------------------------------------------------------------------

    private static Options instance = new Options();

    public static Options instance() {
        return instance;
    }

    //--------------------------------------------------------------------------------------------------
    // Instance fields and methods
    //--------------------------------------------------------------------------------------------------

    private boolean showInstalledThumbnails = true;
    private boolean showUninstalledThumbnails = true;
    private boolean optionsChanged;
    private SimulationTable.SimulationComparator installedSimulationsSortType;

    /**
     * Private constructor to enforce singleton
     */
    private Options() {
    }

    public boolean isShowInstalledThumbnails() {
        return showInstalledThumbnails;
    }

    public void setShowInstalledThumbnails( boolean showInstalledThumbnails ) {
        setShowInstalledThumbnailsNoUpdate( showInstalledThumbnails );
        notifyListeners();
    }

    public void setShowInstalledThumbnailsNoUpdate( boolean showInstalledThumbnails ) {
        this.showInstalledThumbnails = showInstalledThumbnails;
        optionsChanged = true;
    }

    public boolean isShowUninstalledThumbnails() {
        return showUninstalledThumbnails;
    }

    public void setShowUninstalledThumbnails( boolean showUninstalledThumbnails ) {
        setShowUninstalledThumbnailsNoUpdate( showUninstalledThumbnails );
        notifyListeners();
    }

    public void setShowUninstalledThumbnailsNoUpdate( boolean showUninstalledThumbnails ) {
        this.showUninstalledThumbnails = showUninstalledThumbnails;
        optionsChanged = true;
    }

    public SimulationTable.SimulationComparator getInstalledSimulationsSortType() {
        return installedSimulationsSortType;
    }

    public void setInstalledSimulationsSortType( SimulationTable.SimulationComparator installedSimulationsSortType ) {
        this.installedSimulationsSortType = installedSimulationsSortType;
        optionsChanged = true;
        notifyListeners();
    }

    public void notifyListeners() {
        if( optionsChanged ) {
            changeListenerProxy.optionsChanged( new ChangeEvent( this ) );
            optionsChanged = false;
        }
    }


    //--------------------------------------------------------------------------------------------------
    // Events and listeners
    //--------------------------------------------------------------------------------------------------
    public static class ChangeEvent extends EventObject {
        public ChangeEvent( Object source ) {
            super( source );
        }

        public Options getOptions() {
            return (Options)getSource();
        }
    }

    public interface ChangeListener extends EventListener {
        void optionsChanged( ChangeEvent event );
    }

    private EventChannel changeEventChannel = new EventChannel( ChangeListener.class );
    private ChangeListener changeListenerProxy = (ChangeListener)changeEventChannel.getListenerProxy();

    public void addListener( ChangeListener listener ) {
        changeEventChannel.addListener( listener );
    }

    public void removeListener( ChangeListener listener ) {
        changeEventChannel.removeListener( listener );
    }
}
