// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing.components;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import edu.colorado.phet.common.phetcommon.view.PhetFrame;

/**
 * If sim sharing is on, show a menu for isolating and identifying sessions, see #3145
 *
 * @author Sam Reid
 */
public class ResearchMenu extends SimSharingJMenuItem {
    SimSharingLogDialog simSharingDialog = null;

    public ResearchMenu( final PhetFrame parent ) {

        //Call it research and put it in a menu so it is easy to find and not intimidating
        super( "Research..." );
        addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if ( simSharingDialog == null ) {
                    simSharingDialog = new SimSharingLogDialog( parent );
                }
                simSharingDialog.setVisible( true );
            }
        } );
    }
}