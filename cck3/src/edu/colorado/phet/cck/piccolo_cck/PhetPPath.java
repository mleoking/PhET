package edu.colorado.phet.cck.piccolo_cck;

import edu.umd.cs.piccolo.nodes.PPath;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Sep 19, 2006
 * Time: 1:36:09 AM
 * Copyright (c) Sep 19, 2006 by Sam Reid
 */

public class PhetPPath extends PPath {
    public PhetPPath( Shape shape, Paint fill ) {
        super( shape );
        setStroke( null );
        setPaint( fill );
    }

    public PhetPPath( Shape shape, Stroke stroke, Paint strokePaint ) {
        super( shape );
        setStroke( stroke );
        setStrokePaint( strokePaint );
    }

    public PhetPPath( Paint fill ) {
        setStroke( null );
        setPaint( fill );
    }

    public PhetPPath( Stroke basicStroke, Paint strokePaint ) {
        setStroke( basicStroke );
        setStrokePaint( strokePaint );
    }

    public PhetPPath( Shape shape, Paint fill, Stroke stroke, Paint strokePaint ) {
        super( shape );
        setPaint( fill );
        setStroke( stroke );
        setStrokePaint( strokePaint );
    }
}
