package edu.colorado.phet.common.piccolophet.swing;

import java.awt.*;
import java.util.Vector;

import javax.swing.*;

/**
 * Workaround for problems occurring on Mac when embedded in PSwing, see Unfuddle #705
 */
public class PhetJComboBox extends JComboBox {
    public PhetJComboBox( ComboBoxModel aModel ) {
        super( aModel );
        initSelf();
    }

    public PhetJComboBox( Object items[] ) {
        super( items );
        initSelf();
    }

    public PhetJComboBox( Vector items ) {
        super( items );
        initSelf();
    }

    public PhetJComboBox() {
        initSelf();
    }

    private void initSelf() {
        setBorder( BorderFactory.createLineBorder( Color.BLACK ) );
        setBackground( Color.WHITE );
    }

}
