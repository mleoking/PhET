// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.shapes;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
import edu.colorado.phet.capacitorlab.model.meter.Voltmeter;
import edu.colorado.phet.common.phetcommon.math.Point3D;

/**
 * Creates 2D projections of shapes that are related to the 3D voltmeter model.
 * Shapes are in the global view coordinate frame.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class VoltmeterShapeFactory {

    private final Voltmeter voltmeter;
    private final CLModelViewTransform3D mvt;

    public VoltmeterShapeFactory( Voltmeter voltmeter, CLModelViewTransform3D mvt ) {
        this.voltmeter = voltmeter;
        this.mvt = mvt;
    }

    /**
     * Gets the shape of the positive probe's tip in the world coordinate frame.
     *
     * @return
     */
    public Shape getPositiveProbeTipShape() {
        return getProbeTipShape( voltmeter.positiveProbeLocationProperty.get(), -mvt.getYaw() );
    }

    /**
     * Gets the shape of the negative probe's tip in the world coordinate frame.
     *
     * @return
     */
    public Shape getNegativeProbeTipShape() {
        return getProbeTipShape( voltmeter.negativeProbeLocationProperty.get(), -mvt.getYaw() );
    }

    // Gets the shape of a probe tip relative to some specified origin.
    private Shape getProbeTipShape( Point3D origin, double theta ) {
        final double width = voltmeter.getProbeTipSizeReference().getWidth();
        final double height = voltmeter.getProbeTipSizeReference().getHeight();
        final double x = origin.getX() - ( width / 2 );
        final double y = origin.getY();
        Rectangle2D r = new Rectangle2D.Double( x, y, width, height );
        AffineTransform t = AffineTransform.getRotateInstance( theta, origin.getX(), origin.getY() );
        Shape s = t.createTransformedShape( r );
        return mvt.modelToView( s );
    }
}
