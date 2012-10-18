// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.common.view;

import java.awt.Paint;
import java.awt.geom.Rectangle2D;

import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Minus sign, created using PPath because PText("-") looks awful on Windows and cannot be accurately centered.
 * Origin at upper left.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MinusNode extends PPath {

    public MinusNode( PDimension size, Paint paint ) {
        this( size.getWidth(), size.getHeight(), paint );
    }

    private MinusNode( double width, double height, Paint paint ) {
        super( new Rectangle2D.Double( 0, 0, width, height ) );
        assert ( width > height );
        setStroke( null );
        setPaint( paint );
    }
}
