// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.common.photonabsorption.model.atoms;

import java.awt.*;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.view.PhetColorScheme;


/**
 * Class that represents an atom of oxygen in the model.
 *
 * @author John Blanco
 */
public class OxygenAtom extends Atom {

    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------

    private static final Color REPRESENTATION_COLOR = PhetColorScheme.RED_COLORBLIND;
    public static final double MASS = 12.011;   // In atomic mass units (AMU).
    private static final double RADIUS = 73;     // In picometers.

    //------------------------------------------------------------------------
    // Constructor(s)
    //------------------------------------------------------------------------

    public OxygenAtom( Point2D position ) {
        super( REPRESENTATION_COLOR, RADIUS, MASS, position );
    }

    public OxygenAtom() {
        this( new Point2D.Double( 0, 0 ) );
    }
}
