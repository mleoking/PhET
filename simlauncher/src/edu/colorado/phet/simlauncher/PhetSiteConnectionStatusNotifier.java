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

import edu.colorado.phet.simlauncher.model.PhetSiteConnection;

import javax.swing.*;
import java.awt.*;

/**
 * PhetSiteConnectionStatusNotifier
 * <p/>
 * Notifies the user when the status of the connection to the PhET site changes
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class PhetSiteConnectionStatusNotifier implements PhetSiteConnection.ChangeListener {
    boolean isConnected = false;
    private Component parent;

    public PhetSiteConnectionStatusNotifier( Component parent, PhetSiteConnection phetSiteConnection ) {
        this.parent = parent;
        phetSiteConnection.addChangeListener( this );
        isConnected = phetSiteConnection.isConnected();
    }

    public void connectionChanged( PhetSiteConnection.ChangeEvent event ) {
        boolean currentConnectionState = event.getPhetSiteConnection().isConnected();
        if( isConnected != currentConnectionState ) {
            isConnected = currentConnectionState;

            if( isConnected ) {
                displayReconnectedMessage();
            }
            else {
                displayDisconnectedMessage();
            }
        }
    }

    private void displayDisconnectedMessage() {
        JOptionPane.showMessageDialog( parent,
                                       "<html>Your connection to the PhET web site has been lost." +
                                       "<br><br>You will not be able to browse the online catalog or " +
                                       "<br>check for updates to your installed simulations." +
                                       "</html>" );
    }

    private void displayReconnectedMessage() {
        JOptionPane.showMessageDialog( parent,
                                       "<html>Your connection to the PhET web site has been re-established." +
                                       "<br><br>You will now be able to browse the online catalog" +
                                       "<br>check for updates to your installed simulations." +
                                       "</html>" );
    }


}
