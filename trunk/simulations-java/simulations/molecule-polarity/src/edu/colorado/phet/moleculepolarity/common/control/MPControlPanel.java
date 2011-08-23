// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.control;

import java.awt.Font;

import javax.swing.Box;
import javax.swing.JLabel;

import edu.colorado.phet.common.phetcommon.view.util.GridPanel;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;

/**
 * Base class for control panels.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class MPControlPanel extends GridPanel {

    public MPControlPanel( String title ) {
        setGridX( 0 ); // vertical
        setAnchor( Anchor.WEST ); // left justified

        // title
        add( new JLabel( title ) {{
            setFont( new PhetFont( Font.BOLD, 16 ) );
        }} );

        // space between title and controls below it
        add( Box.createVerticalStrut( 5 ) );
    }
}
