/* Copyright 2008, University of Colorado */

package edu.colorado.phet.common.piccolophet.swing;

import java.awt.Color;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;

import edu.colorado.phet.common.phetcommon.util.PhetUtilities;

/**
 * Workaround for problems occurring on Mac when embedded in PSwing, see Unfuddle #705
 */
public class PhetJComboBox extends JComboBox {
    public PhetJComboBox( ComboBoxModel aModel ) {
        super( aModel );
        applyMacWorkaround( this );
    }

    public PhetJComboBox( Object items[] ) {
        super( items );
        applyMacWorkaround( this );
    }

    public PhetJComboBox( Vector items ) {
        super( items );
        applyMacWorkaround( this );
    }

    public PhetJComboBox() {
        super();
        applyMacWorkaround( this );
    }

    public static void applyMacWorkaround( JComboBox comboBox ) {
        if ( PhetUtilities.isMacintosh() ) {
            // Mac has a transparent background, with no border
            comboBox.setBackground( Color.WHITE );
            comboBox.setBorder( BorderFactory.createLineBorder( Color.BLACK ) );
        }
    }
}
