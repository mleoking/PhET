/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.shapes;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import edu.colorado.phet.capacitorlab.model.Battery;
import edu.colorado.phet.capacitorlab.model.ModelViewTransform;
import edu.colorado.phet.capacitorlab.model.Polarity;
import edu.colorado.phet.common.phetcommon.math.Point3D;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Creates 2D projections of shapes that are related to the 3D battery model.
 * All Shapes are in the global view coordinate frame.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BatteryShapeFactory {
    
    /*
     * Sizes determined by visual inspection of the associated image files.
     * All units in meters.
     * To see the corresponding bounds, run the sim with -dev.
     */
    private static final PDimension BODY_SIZE = new PDimension( 0.0065, 0.01425 );
    private static final PDimension POSITIVE_TERMINAL_SIZE = new PDimension( 0.0025, 0.0014 );
    private static final double POSITIVE_TERMINAL_CORNER_RADIUS = 0.0005;
    private static final double POSITIVE_TERMINAL_Y_OFFSET = -( BODY_SIZE.getHeight() / 2 ) + 0.000505;
    private static final PDimension NEGATIVE_TERMINAL_SIZE = new PDimension( 0.0035, 0.0009 );
    private static final double NEGATIVE_TERMINAL_Y_OFFSET = -( BODY_SIZE.getHeight() / 2 ) + 0.00105;
    
    private final Battery battery;
    private final ModelViewTransform mvt;
    
    public BatteryShapeFactory( Battery battery, ModelViewTransform mvt ) {
        this.battery = battery;
        this.mvt = mvt;
    }
    
    public PDimension getBodySizeReference() {
        return BODY_SIZE;
    }
    
    public PDimension getPositiveProbeSizeReference() {
        return POSITIVE_TERMINAL_SIZE;
    }
    
    public double getTopTerminalYOffset() {
        if ( battery.getPolarity() == Polarity.POSITIVE ) {
            return POSITIVE_TERMINAL_Y_OFFSET;
        }
        else {
            return NEGATIVE_TERMINAL_Y_OFFSET;
        }
    }
    
    /**
     * Gets the shape of the battery's body in the world coordinate frame.
     * Origin at the center.
     * @return
     */
    public Shape createBodyShape() {
        double x = battery.getLocationReference().getX() - ( BODY_SIZE.getWidth() / 2 );
        double y = battery.getLocationReference().getY() - ( BODY_SIZE.getHeight() / 2 );
        Shape s = new Rectangle2D.Double( x, y, BODY_SIZE.getWidth(), BODY_SIZE.getHeight() );
        return mvt.modelToView( s );
    }

    /**
     * Creates the shape of the top terminal in the world coordinate frame.
     * Which terminal is on top depends on the polarity.
     */
    public Shape createTopTerminalShape() {
        if ( battery.getPolarity() == Polarity.POSITIVE ) {
            return createPositiveTerminalShape( battery.getLocationReference() );
        }
        else {
            return createNegativeTerminalShape( battery.getLocationReference() );
        }
    }
    
    /*
     * Creates the shape of the positive terminal relative to some specified origin.
     */
    private Shape createPositiveTerminalShape( Point3D origin ) {
        final double terminalWidth = POSITIVE_TERMINAL_SIZE.getWidth();
        final double terminalHeight = POSITIVE_TERMINAL_SIZE.getHeight();
        double x = origin.getX() - ( terminalWidth / 2 );
        double y = origin.getY() - ( BODY_SIZE.getHeight() / 2 ) + 0.0002;
        Shape s = new RoundRectangle2D.Double( x, y, terminalWidth, terminalHeight, POSITIVE_TERMINAL_CORNER_RADIUS, POSITIVE_TERMINAL_CORNER_RADIUS );
        return mvt.modelToView( s );
    }
    
    /*
     * Creates the shape of the negative terminal relative to some specified origin.
     */
    private Shape createNegativeTerminalShape( Point3D origin ) {
        final double terminalWidth = NEGATIVE_TERMINAL_SIZE.getWidth();
        final double terminalHeight = NEGATIVE_TERMINAL_SIZE.getHeight();
        double x = origin.getX() - ( terminalWidth / 2 );
        double y = origin.getY() - ( BODY_SIZE.getHeight() / 2 ) + 0.0006;
        Shape s = new Ellipse2D.Double( x, y, terminalWidth, terminalHeight );
        return mvt.modelToView( s );
    }
}
