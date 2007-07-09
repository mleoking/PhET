package edu.colorado.phet.rotation.view;

import edu.colorado.phet.common.motion.model.IPositionDriven;
import edu.colorado.phet.common.motion.model.MotionBodyState;
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

public class RotationPlatformNode extends PNode {
    private PNode contentNode;
    private double angle = 0.0;
    private RotationPlatform rotationPlatform;

    public RotationPlatformNode( final IPositionDriven environment, final RotationPlatform rotationPlatform ) {
        this.rotationPlatform = rotationPlatform;
        contentNode = new PNode();

        addRingNode( getRadius() * 2.0 / 2.0, Color.green );
        addRingNode( getRadius() * 1.5 / 2.0, Color.yellow );
        addRingNode( getRadius() * 1.0 / 2.0, Color.magenta );
        addRingNode( getRadius() * 0.5 / 2.0, Color.white );
        addRingNode( getRadius() * 0.01 / 2.0, Color.white );

        PhetPPath verticalCrossHair = new PhetPPath( new Line2D.Double( getRadius(), 0, getRadius(), getRadius() * 2 ), new BasicStroke( 2 ), Color.black );
        contentNode.addChild( verticalCrossHair );

        PhetPPath horizontalCrossHair = new PhetPPath( new Line2D.Double( 0, getRadius(), getRadius() * 2, getRadius() ), new BasicStroke( 2 ), Color.black );
        contentNode.addChild( horizontalCrossHair );

        double handleWidth = 10;
        double handleHeight = 10;
        PhetPPath handleNode = new PhetPPath( new Rectangle2D.Double( getRadius() * 2, getRadius() - handleHeight / 2, handleWidth, handleHeight ), Color.blue, new BasicStroke( 1 ), Color.black );
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
                Point2D loc = event.getPositionRelativeTo( RotationPlatformNode.this );
                Point2D center = rotationPlatform.getCenter();
                Vector2D.Double a = new Vector2D.Double( center, initLoc );
                Vector2D.Double b = new Vector2D.Double( center, loc );
                double angleDiff = b.getAngle() - a.getAngle();
//                System.out.println( "a=" + a + ", b=" + b + ", center=" + center + ", angleDiff = " + angleDiff );

                angleDiff = MathUtil.clampAngle( angleDiff, -Math.PI, Math.PI );

                double angle = initAngle + angleDiff;
//                System.out.println( "angleDiff=" + angleDiff + ", angle=" + angle );
                rotationPlatform.setAngle( angle );
                resetDrag( angle, event );//have to reset drag in order to keep track of the winding number
            }

            private void resetDrag( double angle, PInputEvent event ) {
                initAngle = angle;
                initLoc = event.getPositionRelativeTo( RotationPlatformNode.this );
            }
        } );
        addInputEventListener( new CursorHandler() );
        rotationPlatform.getMotionBodyState().addListener( new MotionBodyState.Adapter() {
            public void positionChanged( double dtheta ) {
                setAngle( rotationPlatform.getPosition() );
            }
        } );
        setAngle( rotationPlatform.getPosition() );
        rotationPlatform.addListener( new RotationPlatform.Listener() {
            public void radiusChanged() {
                updateRadius();
            }
        } );
    }

    private void updateRadius() {
        for( int i = 0; i < contentNode.getChildrenCount(); i++ ) {
            PNode child = contentNode.getChild( i );
            if( child instanceof RingNode ) {
                RingNode node = (RingNode)child;
                node.setRadius( rotationPlatform.getRadius() );
            }
        }
    }

    private double getRadius() {
        return rotationPlatform.getRadius();
    }

    private void addRingNode( double radius, Color color ) {
        contentNode.addChild( new RingNode( getRadius(), getRadius(), radius, color ) );
    }

    private void setAngle( double angle ) {
        if( this.angle != angle ) {
            this.angle = angle;
            contentNode.setRotation( 0 );
            contentNode.setOffset( 0, 0 );
            contentNode.translate( rotationPlatform.getCenter().getX() - getRadius(), rotationPlatform.getCenter().getY() - getRadius() );
            contentNode.rotateAboutPoint( angle, rotationPlatform.getCenter().getX(), rotationPlatform.getCenter().getY() );
        }
    }

    public double getAngle() {
        return angle;
    }

    class RingNode extends PNode {
        private PhetPPath path;
        private double x;
        private double y;
        private double radius;

        public RingNode( double x, double y, double radius, Color color ) {
            this.x = x;
            this.y = y;
            this.radius = radius;
            path = new PhetPPath( createPath(), color, new BasicStroke( 1 ), Color.black );
            addChild( path );
        }

        private Ellipse2D.Double createPath() {
            return new Ellipse2D.Double( x - radius, y - radius, radius * 2, radius * 2 );
        }

        public void setRadius( double radius ) {
            this.radius = radius;
            path.setPathTo( createPath() );
        }
    }
}
