/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.view;

import java.awt.Color;

/**
 * Visual pseudo-3D representation of a capacitor dielectric.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DielectricNode extends BoxNode {
    
    public DielectricNode( Color color ) {
        super( color );
    }
    
    public void setColor( Color color ) {
        setTopPaint( color );
        setFrontPaint( color.darker() );
        setSidePaint( color.darker().darker() );
    }
}
