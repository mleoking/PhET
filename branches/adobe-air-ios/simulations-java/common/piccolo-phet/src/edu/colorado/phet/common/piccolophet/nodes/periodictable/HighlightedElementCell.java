// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.nodes.periodictable;

import java.awt.BasicStroke;
import java.awt.Color;

import javax.swing.SwingUtilities;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;

import static edu.colorado.phet.common.phetcommon.view.PhetColorScheme.RED_COLORBLIND;

/**
 * Cell that watches the atom and highlights itself if the atomic number
 * matches its configuration.
 *
 * @author Sam Reid
 * @author John Blanco
 */
public class HighlightedElementCell extends BasicElementCell {

    public HighlightedElementCell( final int atomicNumber, final Color backgroundColor ) {
        super( atomicNumber, backgroundColor );
        getText().setFont( new PhetFont( PhetFont.getDefaultFontSize(), true ) );
        getBox().setStroke( new BasicStroke( 2 ) );
        getBox().setStrokePaint( RED_COLORBLIND );
        getBox().setPaint( Color.white );
    }

    //Wait until others are added so that moving to front will actually work, otherwise 2 sides would be clipped by nodes added after this
    @Override public void tableInitComplete() {
        super.tableInitComplete();

        //For unknown reasons, some nodes (Oxygen in sodium nitrate in sugar-and-salt solutions) get clipped if you don't schedule this for later
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                moveToFront();
            }
        } );
    }
}
