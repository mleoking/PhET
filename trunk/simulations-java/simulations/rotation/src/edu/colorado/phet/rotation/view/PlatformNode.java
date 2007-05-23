package edu.colorado.phet.rotation.view;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.rotation.model.RotationPlatform;
import edu.colorado.phet.rotation.util.MathUtil;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * User: Sam Reid
 * Date: Dec 28, 2006
 * Time: 2:44:08 PM
 */

public class PlatformNode extends PNode {
    private double ringRadius;
    private PNode contentNode;
    private double angle = 0.0;
    private RotationPlatform rotationPlatform;

    public interface RotationPlatformEnvironment {
        void setPositionDriven();
    }

    public PlatformNode( final RotationPlatformEnvironment environment, final RotationPlatform rotationPlatform ) {
        this.rotationPlatform = rotationPlatform;
        ringRadius = rotationPlatform.getRadius();
        contentNode = new PNode();
        addRingNode( ringRadius * 2.0 / 2.0, Color.green );
        addRingNode( ringRadius * 1.5 / 2.0, Color.yellow );
        addRingNode( ringRadius * 1.0 / 2.0, Color.magenta );
        addRingNode( ringRadius * 0.5 / 2.0, Color.white );
        addRingNode( ringRadius * 0.01 / 2.0, Color.white );

        PhetPPath verticalCrossHair = new PhetPPath( new Line2D.Double( ringRadius, 0, ringRadius, ringRadius * 2 ), new BasicStroke( 2 ), Color.black );
        contentNode.addChild( verticalCrossHair );

        PhetPPath horizontalCrossHair = new PhetPPath( new Line2D.Double( 0, ringRadius, ringRadius * 2, ringRadius ), new BasicStroke( 2 ), Color.black );
        contentNode.addChild( horizontalCrossHair );

        double handleWidth = 10;
        double handleHeight = 10;
        PhetPPath handleNode = new PhetPPath( new Rectangle2D.Double( ringRadius * 2, ringRadius - handleHeight / 2, handleWidth, handleHeight ), Color.blue, new BasicStroke( 1 ), Color.black );
        contentNode.addChild( handleNode );

        addChild( contentNode );

        addInputEventListener( new PBasicInputEventHandler() {
            double initAngle;
            public Point2D initLoc;

            public void mousePressed( PInputEvent event ) {
                resetDrag( angle, event );
                environment.setPositionDriven();
            }

            public void mouseReleased( PInputEvent event ) {
            }

            public void mouseDragged( PInputEvent event ) {
                Point2D loc = event.getPositionRelativeTo( PlatformNode.this );
                Point2D center = rotationPlatform.getCenter();
                Vector2D.Double a = new Vector2D.Double( center, initLoc );
                Vector2D.Double b = new Vector2D.Double( center, loc );
                double angleDiff = b.getAngle() - a.getAngle();
//                System.out.println( "a=" + a + ", b=" + b + ", center=" + center + ", angleDiff = " + angleDiff );

                angleDiff = MathUtil.clampAngle( angleDiff, -Math.PI, Math.PI );

                double angle = initAngle + angleDiff;
//                System.out.println( "angleDiff=" + angleDiff + ", angle=" + angle );
                rotationPlatform.setPosition( angle );
                resetDrag( angle, event );//have to reset drag in order to keep track of the winding number
            }

            private void resetDrag( double angle, PInputEvent event ) {
                initAngle = angle;
                initLoc = event.getPositionRelativeTo( PlatformNode.this );
            }
        } );
        addInputEventListener( new CursorHandler() );
        rotationPlatform.addListener( new RotationPlatform.Listener() {
            public void angleChanged( double dtheta ) {
                setAngle( rotationPlatform.getPosition() );
            }
        } );
        setAngle( rotationPlatform.getPosition() );
    }

    private void addRingNode( double radius, Color color ) {
        RingNode ringNode = new RingNode( ringRadius, ringRadius, radius, color );
        contentNode.addChild( ringNode );
    }

    private void setAngle( double angle ) {
        if( this.angle != angle ) {
            this.angle = angle;
            contentNode.setRotation( 0 );
            contentNode.setOffset( 0, 0 );
            contentNode.translate( rotationPlatform.getCenter().getX() - ringRadius, rotationPlatform.getCenter().getY() - ringRadius );
            contentNode.rotateAboutPoint( angle, rotationPlatform.getCenter().getX(), rotationPlatform.getCenter().getY() );
        }
    }

    public double getAngle() {
        return angle;
    }

    class RingNode extends PNode {
        public RingNode( double x, double y, double radius, Color color ) {
            PhetPPath path = new PhetPPath( new Ellipse2D.Double( x - radius, y - radius, radius * 2, radius * 2 ), color, new BasicStroke( 1 ), Color.black );
            addChild( path );
        }
    }
}
