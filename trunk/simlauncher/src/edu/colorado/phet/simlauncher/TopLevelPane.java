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

    public static TopLevelPane getInstance() {
        if( instance == null ) {
            instance = new TopLevelPane();
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

    private InstalledSimsPane installedSimsPane;
    private CatalogPane catalogPane;

    private TopLevelPane() {
        installedSimsPane = new InstalledSimsPane();
        addTab( "Installed Simulations", installedSimsPane );

        // Listen for changes in the connection to the PhET site, to know whether the online
        // catalog should be displayed
        PhetSiteConnection.instance().addChangeListener( new PhetSiteConnection.ChangeListener() {
            public void connectionChanged( PhetSiteConnection.ChangeEvent event ) {
                enableDisableOnlineCatalog( event.getPhetSiteConnection() );
            }
        } );
        enableDisableOnlineCatalog( PhetSiteConnection.instance() );

        // Set the size of things when we first appear
//        addComponentListener( new ComponentAdapter() {
//            public void componentResized( ComponentEvent e ) {
//                setPreferredSize( new Dimension( Math.max( 400, (int)getSize().getWidth() ),
//                                                 Math.max( 300, (int)getSize().getHeight() ) ) );
//                ( (JFrame)SwingUtilities.getRoot( TopLevelPane.this ) ).pack();
//            }
//        } );
    }

    /**
     * Manages the presence of the tabbed pane for the online catalog based on whether
     * we have a connection to the Phet site.
     *
     * Because this asks
     *
     * @param phetSiteConnection
     */
    private synchronized void enableDisableOnlineCatalog( PhetSiteConnection phetSiteConnection ) {
        if( phetSiteConnection.isConnected() ) {
            if( Catalog.instance().isRemoteAvailable() && catalogPane == null ) {
                catalogPane = new CatalogPane();
                addTab( "Catalog", catalogPane );
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
        }
        if( paneID == ONLINE_SIMS_PANE ) {
            index = this.indexOfComponent( catalogPane );
        }
        super.setSelectedIndex( index );
    }


    public InstalledSimsPane getInstalledSimsPane() {
        return installedSimsPane;
    }

    public CatalogPane getUninstalledSimsPane() {
        return catalogPane;
    }
}