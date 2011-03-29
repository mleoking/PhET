// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.shapes;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.capacitorlab.model.Battery;
import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
import edu.colorado.phet.capacitorlab.model.Polarity;
import edu.colorado.phet.common.phetcommon.math.Point3D;
import edu.colorado.phet.common.phetcommon.view.util.ShapeUtils;

/**
 * Creates 2D projections of shapes that are related to the 3D battery model.
 * Shapes are in the global view coordinate frame.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BatteryShapeFactory {

    private final Battery battery;
    private final CLModelViewTransform3D mvt;

    public BatteryShapeFactory( Battery battery, CLModelViewTransform3D mvt ) {
        this.battery = battery;
        this.mvt = mvt;
    }

    /**
     * Gets the shape of the battery's body in the world coordinate frame.
     * Origin at the center.
     * @return
     */
    public Shape createBodyShape() {
        final double width = battery.getBodySizeReference().getWidth();
        final double height = battery.getBodySizeReference().getHeight();
        final double x = battery.location.getX() - ( width / 2 );
        final double y = battery.location.getY() - ( height / 2 );
        Shape s = new Rectangle2D.Double( x, y, width, height );
        return mvt.modelToView( s );
    }

    /**
     * Creates the shape of the top terminal in the world coordinate frame.
     * Which terminal is on top depends on the polarity.
     */
    public Shape createTopTerminalShape() {
        if ( battery.getPolarity() == Polarity.POSITIVE ) {
            return createPositiveTerminalShape( battery.location );
        }
        else {
            return createNegativeTerminalShape( battery.location );
        }
    }

    /*
     * Creates the shape of the positive terminal relative to some specified origin.
     * The positive terminal is a cylinder, created using constructive area geometry.
     */
    private Shape createPositiveTerminalShape( Point3D origin ) {

        // top of the cylinder
        Shape topEllipse = null;
        {
            final double width = battery.getPositiveTerminalEllipseSize().getWidth();
            final double height = battery.getPositiveTerminalEllipseSize().getHeight();
            final double x = origin.getX() - ( width / 2 );
            final double y = origin.getY() + battery.getTopTerminalYOffset() - ( height / 2 );
            topEllipse = new Ellipse2D.Double( x, y, width, height );
        }

        // bottom of the cylinder
        Shape bottomEllipse = null;
        {
            final double width = battery.getPositiveTerminalEllipseSize().getWidth();
            final double height = battery.getPositiveTerminalEllipseSize().getHeight();
            final double x = origin.getX() - ( width / 2 );
            final double y = origin.getY() + battery.getTopTerminalYOffset() - ( height / 2 ) + battery.getPositiveTerminalCylinderHeight();
            bottomEllipse = new Ellipse2D.Double( x, y, width, height );
        }

        // wall of the cylinder
        Shape wallShape = null;
        {
            final double width = battery.getPositiveTerminalEllipseSize().getWidth();
            final double height = battery.getPositiveTerminalCylinderHeight();
            final double x = origin.getX() - ( width / 2 );
            final double y = origin.getY() + battery.getTopTerminalYOffset();
            wallShape = new Rectangle2D.Double( x, y, width, height );
        }

        Shape composite = ShapeUtils.add( topEllipse, wallShape, bottomEllipse );
        return mvt.modelToView( composite );
    }

    /*
     * Creates the shape of the negative terminal relative to some specified origin.
     */
    private Shape createNegativeTerminalShape( Point3D origin ) {
        final double width = battery.getNegativeTerminalSizeReference().getWidth();
        final double height = battery.getNegativeTerminalSizeReference().getHeight();
        final double x = origin.getX() - ( width / 2 );
        final double y = origin.getY() + battery.getTopTerminalYOffset() - ( height / 2 );
        Shape s = new Ellipse2D.Double( x, y, width, height );
        return mvt.modelToView( s );
    }
}
