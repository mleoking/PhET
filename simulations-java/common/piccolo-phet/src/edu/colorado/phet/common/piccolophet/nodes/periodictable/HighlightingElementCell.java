// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.nodes.periodictable;

import java.awt.BasicStroke;
import java.awt.Color;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;

import static edu.colorado.phet.common.phetcommon.view.PhetColorScheme.RED_COLORBLIND;
import static edu.colorado.phet.common.phetcommon.view.util.PhetFont.getDefaultFontSize;
import static java.awt.Color.BLACK;
import static java.awt.Color.white;

/**
 * Cell that watches the atom and highlights itself if the atomic number matches its configuration.
 *
 * @author Sam Reid
 * @author John Blanco
 */
public class HighlightingElementCell extends BasicElementCell {
    public HighlightingElementCell( final PeriodicTableAtom atom, final int atomicNumber, final Color backgroundColor ) {
        super( atomicNumber, backgroundColor );
        atom.addAtomListener( new VoidFunction0() {
            public void apply() {
                boolean match = atom.getNumProtons() == atomicNumber;
                getText().setFont( new PhetFont( getDefaultFontSize(), match ) );
                if ( match ) {
                    getBox().setStroke( new BasicStroke( 2 ) );
                    getBox().setStrokePaint( RED_COLORBLIND );
                    getBox().setPaint( white );
                    HighlightingElementCell.this.moveToFront();
                }
                else {
                    getText().setTextPaint( BLACK );
                    getBox().setStrokePaint( BLACK );
                    getBox().setPaint( backgroundColor );
                    getBox().setStroke( new BasicStroke( 1 ) );
                }
            }
        } );
    }
}
