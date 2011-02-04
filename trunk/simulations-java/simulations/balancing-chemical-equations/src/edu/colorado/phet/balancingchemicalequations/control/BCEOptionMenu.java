// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.control;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.balancingchemicalequations.BCEGlobalProperties;
import edu.colorado.phet.balancingchemicalequations.BCEStrings;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.menu.OptionsMenu;

/**
 * Options menu.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BCEOptionMenu extends OptionsMenu {

    public BCEOptionMenu( BCEGlobalProperties globalProperties ) {

        // Show molecules (check box)
        final Property<Boolean> moleculesVisibleProperty = globalProperties.getMoleculesVisibleProperty();
        final JCheckBoxMenuItem showMoleculesMenuItem = new JCheckBoxMenuItem( BCEStrings.SHOW_MOLECULES, moleculesVisibleProperty.getValue() );
        add( showMoleculesMenuItem );
        showMoleculesMenuItem.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                moleculesVisibleProperty.setValue( showMoleculesMenuItem.isSelected() );
            }
        } );
        moleculesVisibleProperty.addObserver( new SimpleObserver() {
            public void update() {
                showMoleculesMenuItem.setSelected( moleculesVisibleProperty.getValue() );
            }
        } );
    }
}
