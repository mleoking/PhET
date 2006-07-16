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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.EventListener;
import java.util.EventObject;
import java.util.Properties;

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

    // Keys for the properties file
    private static String KEY_SHOW_INSTALLED_THUMBNAILS = "showInstalledThumbnails";
    private static String KEY_SHOW_CATALOG_THUMBNAILS = "showCatalogThumbnails";
    private static String KEY_CHECK_FOR_UPDATES_ON_STARTUP = "checkForUpdatesOnStartup";

    public static Options instance() {
        if( instance == null ) {
            instance = new Options();
        }
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
            instance = new Options();
            instance.load();
        }
    }


    //--------------------------------------------------------------------------------------------------
    // Instance fields and methods
    //--------------------------------------------------------------------------------------------------


    private boolean showInstalledThumbnails = true;
    private boolean showCatalogThumbnails = false;

    private boolean checkForUpdatesOnStartup = true;
    private boolean optionsChanged;
    private SimTable.SimComparator installedSimulationsSortType = DEFAULT_INSTALLED_SIMULATIONS_SORT_TYPE;

    /**
     * Private constructor to enforce singleton
     */
    private Options() {
    }

    public void save() {
        Properties properties = new Properties();
        properties.setProperty( KEY_SHOW_INSTALLED_THUMBNAILS, Boolean.toString( showInstalledThumbnails ) );
        properties.setProperty( KEY_SHOW_CATALOG_THUMBNAILS, Boolean.toString( showCatalogThumbnails ) );
        properties.setProperty( KEY_CHECK_FOR_UPDATES_ON_STARTUP, Boolean.toString( checkForUpdatesOnStartup ) );
        try {
            properties.store( new FileOutputStream( Configuration.instance().getOptionsFile() ), null );
        }
        catch( IOException e ) {
        }
    }

    public void load() {
        Properties properties = new Properties();
        try {
            properties.load( new FileInputStream( Configuration.instance().getOptionsFile() ) );
        }
        catch( IOException e ) {
        }
        showInstalledThumbnails = new Boolean((String)properties.get( KEY_SHOW_INSTALLED_THUMBNAILS )).booleanValue();
        showCatalogThumbnails  = new Boolean((String)properties.get( KEY_SHOW_CATALOG_THUMBNAILS )).booleanValue();
        checkForUpdatesOnStartup  = new Boolean((String)properties.get( KEY_CHECK_FOR_UPDATES_ON_STARTUP )).booleanValue();
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

    public boolean isShowCatalogThumbnails() {
        return showCatalogThumbnails;
    }

    public void setShowCatalogThumbnails( boolean showCatalogThumbnails ) {
        setShowUninstalledThumbnailsNoUpdate( showCatalogThumbnails );
        notifyListeners();
    }

    public void setShowUninstalledThumbnailsNoUpdate( boolean showUninstalledThumbnails ) {
        this.showCatalogThumbnails = showUninstalledThumbnails;
        optionsChanged = true;
    }

    public SimTable.SimComparator getInstalledSimulationsSortType() {
        return installedSimulationsSortType;
    }

    public void setInstalledSimSortType( SimTable.SimComparator installedSimulationsSortType ) {
        this.installedSimulationsSortType = installedSimulationsSortType;
        optionsChanged = true;
        notifyListeners();
    }

    public boolean isCheckForUpdatesOnStartup() {
        return checkForUpdatesOnStartup;
    }

    public void setCheckForUpdatesOnStartup( boolean checkForUpdatesOnStartup ) {
        this.checkForUpdatesOnStartup = checkForUpdatesOnStartup;
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