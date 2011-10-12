// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.view.menu;

import javax.swing.JMenu;

import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyCheckBoxMenuItem;

/**
 * TeacherMenu is the "Teacher" menu that appears in the menu bar.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TeacherMenu extends JMenu {

    public TeacherMenu() {
        super( PhetCommonResources.getString( "Common.TeacherMenu" ) );
        setMnemonic( PhetCommonResources.getChar( "Common.TeacherMenu.mnemonic", 'T' ) );
    }

    /**
     * Adds a JCheckBoxMenu item that allows the user to select whether the sim should be shown for a projector.
     *
     * @param projectorMode the Property<Boolean> with which to synchronize.
     */
    public void addWhiteBackgroundMenuItem( SettableProperty<Boolean> projectorMode ) {
        add( new PropertyCheckBoxMenuItem( PhetCommonResources.getString( "Common.WhiteBackground" ), projectorMode ) {{
            setMnemonic( PhetCommonResources.getChar( "Common.WhiteBackground.mnemonic", 'W' ) );
        }} );
    }
}