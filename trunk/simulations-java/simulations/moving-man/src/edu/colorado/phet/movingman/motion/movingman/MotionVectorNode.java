package edu.colorado.phet.movingman.motion.movingman;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

import edu.colorado.phet.colorvision.phetcommon.view.graphics.shapes.Arrow;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;

/**
 * Created by: Sam
 * Dec 5, 2007 at 2:44:47 PM
 */
public class MotionVectorNode extends PhetPPath {
    public MotionVectorNode( Paint fill, Stroke stroke, Paint strokePaint ) {
        super( fill, stroke, strokePaint );
    }

    public void setVector( double x, int y, double dx, double dy ) {
        if ( Math.abs( dx ) < 1E-4 && Math.abs( dy ) < 1E-4 ) {
            getPathReference().reset();
            firePropertyChange( PROPERTY_CODE_PATH, PROPERTY_PATH, null, getPathReference() );
            updateBoundsFromPath();
            invalidatePaint();
        }
        else {
            Arrow arrow = new Arrow( new Point2D.Double( x, y ), new Point2D.Double( x + dx, y + dy ), 1, 1, 0.5, 0.5, false );
            final GeneralPath shape = arrow.getShape();
            setPathTo( shape );
        }
    }
}
