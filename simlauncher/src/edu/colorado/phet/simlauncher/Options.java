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
import java.beans.XMLEncoder;
import java.beans.XMLDecoder;
import java.io.*;

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
    private static Options instance;
    public static Options instance() {
        return instance;
    }

    SimTable.SimComparator DEFAULT_INSTALLED_SIMULATIONS_SORT_TYPE = SimTable.NAME_SORT;

    /**
     * Initialize the Options singleton at class load time
     */
    static {
        // If there is a stored set of options, create the singleton from it. Otherwise, just
        // create a new instance
        if( Configuration.instance().getOptionsFile().exists() ) {
            try {
                XMLDecoder decoder = new XMLDecoder( new BufferedInputStream(
                        new FileInputStream( Configuration.instance().getOptionsFile() ) ) );

                // MyClass is declared in e7 Serializing a Bean to XML
                instance = (Options)decoder.readObject();
                decoder.close();
            }
            catch( FileNotFoundException e ) {
            }
            catch( Exception e ){
            }
        }
        else {
            instance = new Options();
        }
    }

    //--------------------------------------------------------------------------------------------------
    // Instance fields and methods
    //--------------------------------------------------------------------------------------------------
    private boolean showInstalledThumbnails = true;
    private boolean showUninstalledThumbnails = true;
    private boolean optionsChanged;
    private SimTable.SimComparator installedSimulationsSortType = DEFAULT_INSTALLED_SIMULATIONS_SORT_TYPE;

    /**
     * Private constructor to enforce singleton
     */
    public Options() {
    }

    public void save() {
        try {
            // Serialize object into XML
            XMLEncoder encoder = new XMLEncoder( new BufferedOutputStream(
                    new FileOutputStream( Configuration.instance().getOptionsFile() ) ) );
            encoder.writeObject( this );
            encoder.close();
        }
        catch( FileNotFoundException e ) {
        }
        catch( Exception e ){
        }
    }

    public boolean isShowInstalledThumbnails() {
        return showInstalledThumbnails;
    }

    public void setShowInstalledThumbnails( boolean showInstalledThumbnails ) {
        setShowInstalledThumbnailsNoUpdate( showInstalledThumbnails );
        notifyListeners();

        // todo: ?Options changed?
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

    public SimTable.SimComparator getInstalledSimulationsSortType() {
        return installedSimulationsSortType;
    }

    public void setInstalledSimulationsSortType( SimTable.SimComparator installedSimulationsSortType ) {
        this.installedSimulationsSortType = installedSimulationsSortType;
        optionsChanged = true;
        notifyListeners();
    }

    public void notifyListeners() {
        if( optionsChanged ) {
            save();
            changeListenerProxy.optionsChanged( new ChangeEvent( this ) );
            optionsChanged = false;
        }
    }

    //--------------------------------------------------------------------------------------------------
    // Events and listeners
    //--------------------------------------------------------------------------------------------------
    public static class ChangeEvent extends EventObject {
        public ChangeEvent( Options source ) {
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
