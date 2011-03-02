// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.control;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.balancingchemicalequations.BCEGlobalProperties;
import edu.colorado.phet.balancingchemicalequations.BCEStrings;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.menu.OptionsMenu;

/**
 * Options menu.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BCEOptionMenu extends OptionsMenu {

    public BCEOptionMenu( final BCEGlobalProperties globalProperties ) {

        // Show molecules (check box)
        final JCheckBoxMenuItem hideMoleculesMenuItem = new JCheckBoxMenuItem( BCEStrings.HIDE_MOLECULES, globalProperties.moleculesVisible.getValue() );
        add( hideMoleculesMenuItem );
        hideMoleculesMenuItem.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                globalProperties.moleculesVisible.setValue( !hideMoleculesMenuItem.isSelected() );
            }
        } );
        globalProperties.moleculesVisible.addObserver( new SimpleObserver() {
            public void update() {
                hideMoleculesMenuItem.setSelected( !globalProperties.moleculesVisible.getValue() );
            }
        } );
    }
}
