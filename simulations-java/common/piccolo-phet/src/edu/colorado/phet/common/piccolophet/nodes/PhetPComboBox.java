/* Copyright 2008, University of Colorado */

package edu.colorado.phet.common.piccolophet.nodes;

import java.util.Vector;

import javax.swing.ComboBoxModel;

import edu.colorado.phet.common.piccolophet.swing.PhetJComboBox;
import edu.umd.cs.piccolox.pswing.PComboBox;

/**
 * Workaround for problems occurring on Mac, see Unfuddle #705 
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PhetPComboBox extends PComboBox {

    public PhetPComboBox( ComboBoxModel model ) {
        super( model );
        PhetJComboBox.applyMacWorkaround( this );
    }
    
    public PhetPComboBox( final Object items[] ) {
        super( items );
        PhetJComboBox.applyMacWorkaround( this );
    }
    
    public PhetPComboBox( Vector items ) {
        super( items );
        PhetJComboBox.applyMacWorkaround( this );
    }
    
    public PhetPComboBox() {
        super();
        PhetJComboBox.applyMacWorkaround( this );
    }
}
