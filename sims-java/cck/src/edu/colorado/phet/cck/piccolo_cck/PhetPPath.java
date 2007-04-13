package edu.colorado.phet.cck.piccolo_cck;

import edu.umd.cs.piccolo.nodes.PPath;

import java.awt.*;

/**
 * PhetPPath provides convenient constructors for setting up a PPath all at once.
 * <p/>
 * That is, you can do PPath=new PhetPPath(myShape,fillPaint,stroke,strokePaint);
 * instead of 4 lines of code (as supported by the piccolo API.
 *
 * @author Sam Reid
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

    public PhetPPath( Stroke stroke, Paint strokePaint ) {
        setStroke( stroke );
        setStrokePaint( strokePaint );
    }

    public PhetPPath( Shape shape, Paint fill, Stroke stroke, Paint strokePaint ) {
        super( shape );
        setPaint( fill );
        setStroke( stroke );
        setStrokePaint( strokePaint );
    }

    public PhetPPath( Paint fill, Stroke stroke, Paint strokePaint ) {
        setPaint( fill );
        setStroke( stroke );
        setStrokePaint( strokePaint );
    }
}
