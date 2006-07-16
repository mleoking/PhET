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

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.EventListener;
import java.util.EventObject;

/**
 * TopLevelPane
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class TopLevelPane extends JTabbedPane {

    //--------------------------------------------------------------------------------------------------
    // Class fields and methods
    //--------------------------------------------------------------------------------------------------

    private static TopLevelPane instance;
    private SimContainer activeSimContainer;

    public static TopLevelPane getInstance() {
        if( instance == null ) {
            instance = new TopLevelPane();
            // The default pane is the one showing installed sims
            instance.setActivePane( TopLevelPane.INSTALLED_SIMS_PANE );
        }
        return instance;
    }

    // Enumeration of panes
    public static class PaneID {
        private PaneID() {
        }
    }

    public static PaneID INSTALLED_SIMS_PANE = new PaneID();
    public static PaneID ONLINE_SIMS_PANE = new PaneID();

    //--------------------------------------------------------------------------------------------------
    // Instance fields and methods
    //--------------------------------------------------------------------------------------------------

    private InstalledSimsPaneNew installedSimsPane;
//    private InstalledSimsPane installedSimsPane;
    private CatalogPane catalogPane;

    private TopLevelPane() {
        installedSimsPane = new InstalledSimsPaneNew();
        addTab( "Installed Simulations", installedSimsPane );

        // Listen for changes in the connection to the PhET site, to know whether the online
        // catalog should be displayed
        PhetSiteConnection.instance().addChangeListener( new PhetSiteConnection.ChangeListener() {
            public void connectionChanged( PhetSiteConnection.ChangeEvent event ) {
                enableDisableOnlineCatalog( event.getPhetSiteConnection() );
            }
        } );
        enableDisableOnlineCatalog( PhetSiteConnection.instance() );


        addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                setActiveSimContainer( (SimContainer)getSelectedComponent() );
                activePaneListenerProxy.activePaneChanged( new PaneChangeEvent( TopLevelPane.this ) );
            }
        } );
    }

    public void setSelectedComponent( Component c ) {
        super.setSelectedComponent( c );
                if( getSelectedComponent() == installedSimsPane ) {
                    setActivePane( INSTALLED_SIMS_PANE );
                    System.out.print( "TopLevelPane.setSelectedComponent AAAA" );
                }
                else if( getSelectedComponent()== catalogPane ) {
                    setActivePane( ONLINE_SIMS_PANE );
                    System.out.print( "TopLevelPane.setSelectedComponent AAAA" );
                }
                else {
                    System.out.println( "TopLevelPane.stateChanged" );
                }
    }

    /**
     * Manages the presence of the tabbed pane for the online catalog based on whether
     * we have a connection to the Phet site.
     * <p/>
     * Because this asks
     *
     * @param phetSiteConnection
     */
    private synchronized void enableDisableOnlineCatalog( PhetSiteConnection phetSiteConnection ) {
        if( phetSiteConnection.isConnected() ) {
            if( Catalog.instance().isRemoteAvailable() && catalogPane == null ) {
                catalogPane = new CatalogPane();
                addTab( "Catalog", catalogPane );
                Catalog.instance().removeChangeListener( installedSimsPane );
                Catalog.instance().addChangeListener( installedSimsPane );
            }
        }
        else {
            if( catalogPane != null ) {
                remove( catalogPane );
                catalogPane = null;
            }
        }
    }

    /**
     * Set the active pane
     */
    public void setActivePane( PaneID paneID ) {
        int index = 0;
        if( paneID == INSTALLED_SIMS_PANE ) {
            index = this.indexOfComponent( installedSimsPane );
            setActiveSimContainer( installedSimsPane );
        }
        if( paneID == ONLINE_SIMS_PANE ) {
            index = this.indexOfComponent( catalogPane );
            setActiveSimContainer( catalogPane );
        }
        super.setSelectedIndex( index );

        activePaneListenerProxy.activePaneChanged( new PaneChangeEvent( this) );
    }

    private void setActiveSimContainer( SimContainer simContainer ) {
        activeSimContainer = simContainer;
    }

    public InstalledSimsPaneNew getInstalledSimsPane() {
        return installedSimsPane;
    }

//    public InstalledSimsPane getInstalledSimsPane() {
//        return installedSimsPane;
//    }
//
    public CatalogPane getUninstalledSimsPane() {
        return catalogPane;
    }

    public SimContainer getActiveSimContainer() {
        return activeSimContainer;
    }



    //--------------------------------------------------------------------------------------------------
    // ChangeListener definition
    //--------------------------------------------------------------------------------------------------

    public interface ActivePaneListener extends EventListener {
        void activePaneChanged( PaneChangeEvent event );
    }

    EventChannel changeEventChannel = new EventChannel( ActivePaneListener.class );
    ActivePaneListener activePaneListenerProxy = (ActivePaneListener)changeEventChannel.getListenerProxy();

    public void addActivePaneListener( ActivePaneListener listener ) {
        changeEventChannel.addListener( listener );
    }

    public void removeActivePaneListener( ActivePaneListener listener ) {
        changeEventChannel.removeListener( listener );
    }

    public class PaneChangeEvent extends EventObject {
        public PaneChangeEvent( Object source ) {
            super( source );
        }

        public TopLevelPane get() {
            return (TopLevelPane)getSource();
        }
    }

}