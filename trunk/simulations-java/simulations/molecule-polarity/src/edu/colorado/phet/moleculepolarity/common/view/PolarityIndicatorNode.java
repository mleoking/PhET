// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.view;

import java.awt.BasicStroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;

import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Indicator of polarity (positive or negative).
 * Origin at upper-left corner of bounding rectangle.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PolarityIndicatorNode extends PComposite {

    private static final double DEFAULT_DIAMETER = 40;

    public PolarityIndicatorNode( boolean positive ) {
        this( positive, DEFAULT_DIAMETER );
    }

    public PolarityIndicatorNode( boolean positive, double diameter ) {
        final float strokeWidth = (float) ( 0.1 * diameter );
        // circle
        addChild( new PPath( new Ellipse2D.Double( 0, 0, diameter, diameter ) ) {{
            setStroke( new BasicStroke( strokeWidth ) );
        }} );
        // horizontal bar for plus or minus sign
        addChild( new PPath( new Line2D.Double( 0.25 * diameter, 0.5 * diameter, 0.75 * diameter, 0.5 * diameter ) ) {{
            setStroke( new BasicStroke( strokeWidth ) );
        }} );
        // vertical bar for plus sign
        if ( positive ) {
            addChild( new PPath( new Line2D.Double( 0.5 * diameter, 0.25 * diameter, 0.5 * diameter, 0.75 * diameter ) ) {{
                setStroke( new BasicStroke( strokeWidth ) );
            }} );
        }
        // vertical connecting bar
        addChild( new PPath( new Line2D.Double( 0.5 * diameter, diameter, 0.5 * diameter, 1.5 * diameter ) ) {{
            setStroke( new BasicStroke( strokeWidth ) );
        }} );
    }
}
