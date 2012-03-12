// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.reactantsproductsandleftovers.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;

import edu.colorado.phet.reactantsproductsandleftovers.RPALColors;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * A fancy plus sign, for use in formulas.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PlusNode extends PPath {
    
    private static final Stroke STROKE = new BasicStroke( 1f );
    private static final Color STROKE_PAINT = Color.BLACK;
    private static final Color FILL_PAINT = RPALColors.PLUS_SIGN_COLOR;
    
    public PlusNode() {
        this( 30, 30, 10 );
    }

    public PlusNode( double width, double height, double thickness ) {
        super();
        setStroke( STROKE );
        setStrokePaint( STROKE_PAINT );
        setPaint( FILL_PAINT );
        
        // coordinate, clockwise from upper left
        float x1 = 0f;
        float x2 = (float) ( width / 2 - thickness / 2 );
        float x3 = (float) ( width / 2 + thickness / 2 );
        float x4 = (float) width;
        float y1 = 0f;
        float y2 = (float) ( height / 2 - thickness / 2 );
        float y3 = (float) ( height / 2 + thickness / 2 );
        float y4 = (float) height;
        
        // clockwise, from upper left
        GeneralPath path = new GeneralPath();
        path.moveTo( x2, y1 );
        path.lineTo( x3, y1 );
        path.lineTo( x3, y2 );
        path.lineTo( x4, y2 );
        path.lineTo( x4, y3 );
        path.lineTo( x3, y3 );
        path.lineTo( x3, y4 );
        path.lineTo( x2, y4 );
        path.lineTo( x2, y3 );
        path.lineTo( x1, y3 );
        path.lineTo( x1, y2 );
        path.lineTo( x2, y2 );
        path.closePath();
        
        setPathTo( path );
    }
}
