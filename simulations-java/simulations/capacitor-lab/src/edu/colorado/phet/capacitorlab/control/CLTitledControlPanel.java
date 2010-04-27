/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.control;

import java.awt.Font;

import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;

/**
 * Base class for control panels with titled borders.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class CLTitledControlPanel extends JPanel {
    
    public static final Font TITLE_FONT = new PhetFont( Font.BOLD, 18 );

    public CLTitledControlPanel( String title ) {
        TitledBorder border = new TitledBorder( title );
        border.setTitleFont( TITLE_FONT );
        setBorder( border );
    }
}
