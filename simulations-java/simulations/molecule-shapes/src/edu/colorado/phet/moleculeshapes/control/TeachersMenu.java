// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.moleculeshapes.MoleculeShapesColor;

/**
 * Displays a "Teacher" menu that allows the user to select between normal colors and a "White Background" mode
 */
public class TeachersMenu extends JMenu {
    public TeachersMenu() {
        super( PhetCommonResources.getString( "Common.TeacherMenu" ) );
        setMnemonic( PhetCommonResources.getChar( "Common.TeacherMenu.mnemonic", 'T' ) );
        add( new JCheckBoxMenuItem( PhetCommonResources.getString( "Common.WhiteBackground" ) ) {{
            setSelected( false );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    if ( isSelected() ) {
                        MoleculeShapesColor.PROJECTOR.apply( MoleculeShapesColor.handler );
                    }
                    else {
                        MoleculeShapesColor.DEFAULT.apply( MoleculeShapesColor.handler );
                    }
                }
            } );
            setMnemonic( PhetCommonResources.getChar( "Common.WhiteBackground.mnemonic", 'W' ) );
        }} );
    }
}
