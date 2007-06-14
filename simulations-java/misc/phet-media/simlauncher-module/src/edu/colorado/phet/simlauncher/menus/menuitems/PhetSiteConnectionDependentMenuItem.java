/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: /cvsroot/phet/simlauncher/src/edu/colorado/phet/simlauncher/menus/menuitems/PhetSiteConnectionDependentMenuItem.java,v $
 * Branch : $Name:  $
 * Modified by : $Author: ronlemaster $
 * Revision : $Revision: 1.2 $
 * Date modified : $Date: 2006/07/25 18:00:17 $
 */
package edu.colorado.phet.simlauncher.menus.menuitems;

import edu.colorado.phet.simlauncher.model.PhetSiteConnection;

import javax.swing.*;

/**
 * PhetSiteConnectionDependentMenuItem
 * <p/>
 * A menu item whose enabled/disabled state is dependent on both conventiona setEnabled() calls made to it
 * and the state of a PhetSietConnection.
 *
 * @author Ron LeMaster
 * @version $Revision: 1.2 $
 */
public class PhetSiteConnectionDependentMenuItem extends JMenuItem implements PhetSiteConnection.ChangeListener {
    private boolean enabledInternal = true;
    private boolean connected;

    public PhetSiteConnectionDependentMenuItem( String text, PhetSiteConnection phetSiteConnection ) {
        super( text );
        phetSiteConnection.addChangeListener( this );
        connected = phetSiteConnection.isConnected();
        setEnabledInternal();
    }

    public void connectionChanged( PhetSiteConnection.ChangeEvent event ) {
        connected = event.getPhetSiteConnection().isConnected();
        setEnabledInternal();
    }

    public void setEnabled( boolean b ) {
        enabledInternal = b;
        setEnabledInternal();
    }

    private void setEnabledInternal() {
        super.setEnabled( enabledInternal && connected );
        ;
    }
}
