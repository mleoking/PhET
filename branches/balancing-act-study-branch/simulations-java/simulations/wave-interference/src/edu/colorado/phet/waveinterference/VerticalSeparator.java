// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.waveinterference;

import java.awt.*;

import javax.swing.*;

/**
 * User: Sam Reid
 * Date: Apr 14, 2006
 * Time: 10:35:28 AM
 */

public class VerticalSeparator extends JPanel {
    public VerticalSeparator() {
        this( 20 );
    }

    public VerticalSeparator( int h ) {
        add( Box.createRigidArea( new Dimension( h, h ) ) );
    }
}
