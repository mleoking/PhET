/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
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
 * @version $Revision$
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
