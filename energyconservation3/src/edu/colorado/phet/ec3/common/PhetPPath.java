package edu.colorado.phet.ec3.common;

import edu.umd.cs.piccolo.nodes.PPath;

import java.awt.*;

/**
 * PhetPPath provides convenient constructors for setting up a PPath.
 * <p/>
 * That is, you can do PPath=new PhetPPath(myShape,fillPaint,stroke,strokePaint);
 * instead of 4 lines of code (as supported by the piccolo API).
 *
 * @author Sam Reid
 */

public class PhetPPath extends PPath {
    /**
     * Creates a PhetPPath with the specified fill paint and no stroke.
     *
     * @param fill the paint for fill
     */
    public PhetPPath( Paint fill ) {
        setStroke( null );
        setPaint( fill );
    }

    /**
     * Constructs a PhetPPath with the specified stroke and stroke paint, but no fill paint.
     *
     * @param stroke
     * @param strokePaint
     */
    public PhetPPath( Stroke stroke, Paint strokePaint ) {
        setStroke( stroke );
        setStrokePaint( strokePaint );
    }

    /**
     * Constructs a PhetPPath with the specified shape and fill paint, with no stroke.
     *
     * @param shape
     * @param fill
     */
    public PhetPPath( Shape shape, Paint fill ) {
        super( shape );
        setStroke( null );
        setPaint( fill );
    }

    /**
     * Constructs a PhetPPath with the specified shape, stroke and stroke paint, but no fill paint.
     *
     * @param shape
     * @param stroke
     * @param strokePaint
     */
    public PhetPPath( Shape shape, Stroke stroke, Paint strokePaint ) {
        super( shape );
        setStroke( stroke );
        setStrokePaint( strokePaint );
    }

    /**
     * Constructs a PhetPPath with the specified shape, fill paint, stroke and stroke paint.
     *
     * @param shape
     * @param fill
     * @param stroke
     * @param strokePaint
     */
    public PhetPPath( Shape shape, Paint fill, Stroke stroke, Paint strokePaint ) {
        super( shape );
        setPaint( fill );
        setStroke( stroke );
        setStrokePaint( strokePaint );
    }

    /**
     * Constructs a PhetPPath with the specified fill paint, stroke and stroke paint.
     *
     * @param fill
     * @param stroke
     * @param strokePaint
     */
    public PhetPPath( Paint fill, Stroke stroke, Paint strokePaint ) {
        setPaint( fill );
        setStroke( stroke );
        setStrokePaint( strokePaint );
    }
}
