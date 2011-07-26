// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.teetertotter.model;

import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;

/**
 * Fulcrum (for lack of a better word) that has a pivot point that is above
 * the plank.  This looks sort of like a swingset, with angled legs that go
 * from the ground up to apex in a sort of A-frame arrangement.
 *
 * @author John Blanco
 */
public class FulcrumAbovePlank extends ShapeModelElement {
    //Distance from the base of the fulcrum to its top
    private static final double LEG_THICKNESS_FACTOR = 0.08;

    private final double height;

    public FulcrumAbovePlank( double width, double height ) {
        super( generateShape( width, height ) );
        this.height = height;
    }

    public double getHeight() {
        return height;
    }

    private static Shape generateShape( final double width, final double height ) {
        final double legThickness = LEG_THICKNESS_FACTOR * width;

        //Create a V shape with a stroke of the legThickness
        Shape shape = new BasicStroke( (float) legThickness ).createStrokedShape( new DoubleGeneralPath( -width / 2, 0 ) {{
            lineTo( 0, height + legThickness );
            lineTo( width / 2, 0 );
        }}.getGeneralPath() );

        //Subtract out the part where the earth meets the base of the support, so that it looks flush with the flat earth surface
        //Also, subtract a clip from the top so the V isn't pointy
        return new Area( shape ) {{
            subtract( new Area( new Rectangle2D.Double( -width, -legThickness, width * 2, legThickness ) ) );
            subtract( new Area( new Rectangle2D.Double( -width, height + legThickness / 2, width * 2, height ) ) );
        }};
    }
}
