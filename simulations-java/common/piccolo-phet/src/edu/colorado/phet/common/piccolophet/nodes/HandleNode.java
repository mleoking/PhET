// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.nodes;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * HandleNode draws a handle is somewhat C-shaped, sort of like this:
 * <p/>
 * <code>
 * --------+
 * /        |
 * |   /----+
 * |   |
 * |   |
 * |   \----+
 * \        |
 * --------+
 * </code>
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class HandleNode extends PhetPNode {

    // Defaults
    private static final double DEFAULT_THICKNESS_RATIO = 0.4;
    private static final double DEFAULT_CORNER_WIDTH_RATIO = 0.4;
    private static final Paint DEFAULT_STROKE_PAINT = Color.BLACK;
    private static final Stroke DEFAULT_STROKE = new BasicStroke();
    private PPath handleNode;

    /**
     * Simplified constructor, uses default values for many properties.
     *
     * @param width
     * @param height
     * @param fillPaint
     */
    public HandleNode( double width, double height, Paint fillPaint ) {
        this( width, height,
              DEFAULT_THICKNESS_RATIO * Math.min( width, height ),
              DEFAULT_CORNER_WIDTH_RATIO * Math.min( width, height ),
              fillPaint, DEFAULT_STROKE_PAINT, DEFAULT_STROKE );
    }

    /**
     * Fully-specified constructor.
     *
     * @param width
     * @param height
     * @param thickness
     * @param cornerWidth
     * @param fillPaint
     * @param strokePaint
     * @param stroke
     */
    public HandleNode( double width, double height, double thickness, double cornerWidth, Paint fillPaint, Paint strokePaint, Stroke stroke ) {
        super();

        // Validate arguments
        if ( width <= 0 ) {
            throw new IllegalArgumentException( "width must be > 0" );
        }
        if ( height <= 0 ) {
            throw new IllegalArgumentException( "height must be > 0" );
        }
        if ( thickness >= Math.min( width, height ) ) {
            throw new IllegalArgumentException( "thickness is too large: " + thickness );
        }
        if ( cornerWidth < 0 ) {
            throw new IllegalArgumentException( "cornerWidth must be >= 0" );
        }

        /*
        * Create the handle shape using constructive geometry.
        * To get rounded corners on the handle, create a rounded rectangle
        * that is twice as wide as needed, then cut it in half.
        */
        RoundRectangle2D outerRect = new RoundRectangle2D.Double( 0, 0, 2 * width, height, cornerWidth, cornerWidth );
        RoundRectangle2D innerRect = new RoundRectangle2D.Double( thickness, thickness, 2 * ( width - thickness ), height - ( 2 * thickness ), cornerWidth, cornerWidth );
        Rectangle2D boundsRect = new Rectangle2D.Double( 0, 0, width, height );
        Area handleArea = new Area( outerRect );
        handleArea.exclusiveOr( new Area( innerRect ) );
        handleArea.intersect( new Area( boundsRect ) );

        handleNode = new PPath( handleArea );
        handleNode.setPaint( fillPaint );
        handleNode.setStrokePaint( strokePaint );
        handleNode.setStroke( stroke );

        addChild( handleNode );
    }

    public void setStroke( Stroke stroke ) {
        handleNode.setStroke( stroke );
    }
}
