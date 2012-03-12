// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.reactantsproductsandleftovers;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.simsharing.components.SimSharingJCheckBoxMenuItem;
import edu.colorado.phet.common.phetcommon.view.menu.TeacherMenu;

/**
 * "Teacher" menu for this sim.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class RPALTeacherMenu extends TeacherMenu {

    public RPALTeacherMenu() {

        // Check box for changing to "worksheet" color scheme.
        final JCheckBoxMenuItem worksheetColorsMenuItem = new SimSharingJCheckBoxMenuItem( RPALSimSharing.UserComponents.worksheetColorsMenuItem, RPALStrings.MENU_WORKSHEET_COLORS );
        worksheetColorsMenuItem.setSelected( RPALColors.COLOR_SCHEME.get() == RPALColors.WORKSHEET_COLOR_SCHEME );
        worksheetColorsMenuItem.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                if ( worksheetColorsMenuItem.isSelected() ) {
                    RPALColors.COLOR_SCHEME.set( RPALColors.WORKSHEET_COLOR_SCHEME );
                }
                else {
                    RPALColors.COLOR_SCHEME.set( RPALColors.NORMAL_COLOR_SCHEME );
                }
            }
        } );
        add( worksheetColorsMenuItem );
    }
}
