package edu.colorado.phet.rotation.torque;

import java.awt.*;
import java.awt.geom.Arc2D;

import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.rotation.model.RotationPlatform;
import edu.colorado.phet.rotation.view.RotationPlatformNode;
import edu.colorado.phet.rotation.view.RotationPlayAreaNode;

/**
 * Created by: Sam
 * Dec 3, 2007 at 11:52:49 PM
 */
public class RotationPlatformNodeWithMassDisplay extends RotationPlatformNode {
    private EdgeGraphic edgeGraphic;

    public RotationPlatformNodeWithMassDisplay( RotationPlatform rotationPlatform ) {
        super( rotationPlatform );
        rotationPlatform.addListener( new RotationPlatform.Adapter() {
            public void massChanged() {
                update();
            }

            public void radiusChanged() {
                update();
            }

            public void innerRadiusChanged() {
                update();
            }
        } );

        edgeGraphic = new EdgeGraphic();
        addChild( 0, edgeGraphic );
//        addChild( edgeGraphic );
        update();
    }

    class EdgeGraphic extends PhetPPath {
        public EdgeGraphic() {
            super( Color.gray, new BasicStroke( (float) ( 1.0 * RotationPlayAreaNode.SCALE ) ), Color.darkGray );
        }
    }

    private void update() {
        Function.LinearFunction linearFunction = new Function.LinearFunction( RotationPlatform.MIN_MASS, RotationPlatform.MAX_MASS, 0, 0.15 );
        double dy = linearFunction.evaluate( getRotationPlatform().getMass() );
        DoubleGeneralPath doubleGeneralPath = new DoubleGeneralPath( getRotationPlatform().getCenter().getX() - getRadius(), getRotationPlatform().getCenter().getY() );
        doubleGeneralPath.lineToRelative( 0, -dy );
        Arc2D.Double arc = new Arc2D.Double( getRotationPlatform().getCenter().getX() - getRadius(), getRotationPlatform().getCenter().getY() - getRadius() - dy,
                                             getRadius() * 2, getRadius() * 2, 0, 180, Arc2D.Double.OPEN );
        doubleGeneralPath.append( arc, false );

        doubleGeneralPath.lineToRelative( 0, +dy );
        doubleGeneralPath.lineTo( getRotationPlatform().getCenter().getX() + getRadius(), getRotationPlatform().getCenter().getY() );
        doubleGeneralPath.closePath();
        edgeGraphic.setPathTo( doubleGeneralPath.getGeneralPath() );
    }
}
