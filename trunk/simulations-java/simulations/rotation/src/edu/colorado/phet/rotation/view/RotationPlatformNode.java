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
    private PhetPPath verticalCrossHair;
    private PhetPPath horizontalCrossHair;
    private PhetPPath innerRadiusNode;//cover up with background color for simplicity

    private double handleWidth = 10;
    private double handleHeight = 10;
    private PhetPPath handleNode;

    public RotationPlatformNode( final IPositionDriven environment, final RotationPlatform rotationPlatform ) {
        this.rotationPlatform = rotationPlatform;
        contentNode = new PNode();

        addRingNode( 1.0, Color.green );
        addRingNode( 0.75, Color.yellow );
        addRingNode( 0.50, Color.magenta );
        addRingNode( 0.25, Color.white );
        addRingNode( 0.005, Color.white );


        verticalCrossHair = new PhetPPath( getVerticalCrossHairPath(), new BasicStroke( 2 ), Color.black );
        contentNode.addChild( verticalCrossHair );

        horizontalCrossHair = new PhetPPath( getHorizontalCrossHairPath(), new BasicStroke( 2 ), Color.black );
        contentNode.addChild( horizontalCrossHair );

        innerRadiusNode = new PhetPPath( new RotationLookAndFeel().getBackgroundColor(), new BasicStroke( 1 ), Color.black );
        contentNode.addChild( innerRadiusNode );

        handleNode = new PhetPPath( createHandlePath(), Color.blue, new BasicStroke( 1 ), Color.black );
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

            public void innerRadiusChanged() {
                updateInnerRadius();
            }
        } );
        updateRadius();
        updateInnerRadius();
    }

    private void updateInnerRadius() {
        double r = rotationPlatform.getInnerRadius();
        innerRadiusNode.setPathTo( new Ellipse2D.Double( rotationPlatform.getCenter().getX() - r, rotationPlatform.getCenter().getY() - r, r * 2, r * 2 ) );
        updateAngle();
    }

    private Rectangle2D.Double createHandlePath() {
        return new Rectangle2D.Double( rotationPlatform.getCenter().getX() + getRadius(), rotationPlatform.getCenter().getY() - handleHeight / 2, handleWidth, handleHeight );
    }

    private Line2D.Double getHorizontalCrossHairPath() {
        return new Line2D.Double( rotationPlatform.getCenter().getX() - getRadius(), rotationPlatform.getCenter().getY(),
                                  rotationPlatform.getCenter().getX() + getRadius(), rotationPlatform.getCenter().getY() );
    }

    private Line2D.Double getVerticalCrossHairPath() {
        return new Line2D.Double( rotationPlatform.getCenter().getX(), rotationPlatform.getCenter().getY() - getRadius(),
                                  rotationPlatform.getCenter().getX(), rotationPlatform.getCenter().getY() + getRadius() );
    }

    private void updateRadius() {
        for( int i = 0; i < contentNode.getChildrenCount(); i++ ) {
            PNode child = contentNode.getChild( i );
            if( child instanceof RingNode ) {
                RingNode node = (RingNode)child;
                node.setRadius( Math.min( node.getMaxRadius(), getRadius() ) );
            }
        }
        verticalCrossHair.setPathTo( getVerticalCrossHairPath() );
        horizontalCrossHair.setPathTo( getHorizontalCrossHairPath() );
        handleNode.setPathTo( createHandlePath() );
        updateAngle();
    }

    private double getRadius() {
        return rotationPlatform.getRadius();
    }

    private void addRingNode( double fractionalRadius, Color color ) {
        contentNode.addChild( new RingNode( rotationPlatform.getCenter().getX(), rotationPlatform.getCenter().getY(), fractionalRadius * getRadius(), color ) );
    }

    private void setAngle( double angle ) {
        if( this.angle != angle ) {
            this.angle = angle;
            updateAngle();
        }
    }

    private void updateAngle() {
        contentNode.setRotation( 0 );
        contentNode.setOffset( 0, 0 );
        contentNode.rotateAboutPoint( angle, rotationPlatform.getCenter().getX(), rotationPlatform.getCenter().getY() );
    }

    public double getAngle() {
        return angle;
    }

    class RingNode extends PNode {
        private PhetPPath path;
        private double x;
        private double y;
        private double radius;
        private double maxRadius;

        public RingNode( double x, double y, double radius, Color color ) {
            this.x = x;
            this.y = y;
            this.radius = radius;
            this.maxRadius = radius;
            path = new PhetPPath( null, color, new BasicStroke( 1 ), Color.black );
            addChild( path );
            updatePath();
        }

        private Ellipse2D.Double createPath() {
            double r = radius;
            return new Ellipse2D.Double( x - r, y - r, r * 2, r * 2 );
        }

        public double getMaxRadius() {
            return maxRadius;
        }

        public void setRadius( double radius ) {
            this.radius = radius;
            updatePath();
        }

        private void updatePath() {
            path.setPathTo( createPath() );
        }
    }
}
