// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.view;

import java.awt.Paint;
import java.awt.geom.Rectangle2D;

import edu.umd.cs.piccolo.nodes.PPath;

/**
 * Minus sign, created using PPath because PText("-") cannot be accurately centered.
 * Origin at geometric center.
 */
public class MinusNode extends PPath {

    public MinusNode( double width, double height, Paint paint ) {
        super( new Rectangle2D.Double( -width/2, -height/2, width, height ) );
        setStroke( null );
        setPaint( paint );
    }
}
