package edu.colorado.phet.rotation.view;

import edu.colorado.phet.common.motion.model.IPositionDriven;
import edu.colorado.phet.common.motion.model.MotionBody;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.rotation.model.RotationPlatform;
import edu.umd.cs.piccolo.PNode;

import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
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

//    private final float STROKE_SCALE = 1.0f / 200.0f;

    private double handleWidth = 10 * RotationPlayAreaNode.SCALE;
    private double handleHeight = 10 * RotationPlayAreaNode.SCALE;
    private PhetPPath handleNode;


    public RotationPlatformNode( final IPositionDriven environment, final RotationPlatform rotationPlatform ) {
        this.rotationPlatform = rotationPlatform;
        contentNode = new PNode();

//        addRingNode( 1.0, Color.green.brighter(), true );
        addRingNode( 1.0, new Color( 255, 215, 215 ), new Color( 255, 240, 240 ), true );
        addRingNode( 0.75, new Color( 140, 255, 140 ), new Color( 200, 255, 200 ), false );
        addRingNode( 0.50, new Color( 215, 215, 255 ), new Color( 240, 240, 255 ), false );
        addRingNode( 0.25, Color.white, Color.lightGray, false );
//        addRingNode( 0.005, Color.white );


        verticalCrossHair = new PhetPPath( getVerticalCrossHairPath(), new BasicStroke( (float)( 2 * RotationPlayAreaNode.SCALE ) ), Color.black );
        contentNode.addChild( verticalCrossHair );

        horizontalCrossHair = new PhetPPath( getHorizontalCrossHairPath(), new BasicStroke( (float)( 2 * RotationPlayAreaNode.SCALE ) ), Color.black );
        contentNode.addChild( horizontalCrossHair );

        innerRadiusNode = new PhetPPath( new RotationLookAndFeel().getBackgroundColor(), new BasicStroke( (float)( 1 * RotationPlayAreaNode.SCALE ) ), Color.black );
        contentNode.addChild( innerRadiusNode );

        handleNode = new PhetPPath( createHandlePath(), Color.blue, new BasicStroke( (float)( 1 * RotationPlayAreaNode.SCALE ) ), Color.black );
        contentNode.addChild( handleNode );

        addChild( contentNode );

        rotationPlatform.addListener( new MotionBody.MBAdapter() {
            public void positionChanged( double dtheta ) {
                setAngle( rotationPlatform.getPosition() );
            }
        } );
        setAngle( rotationPlatform.getPosition() );
        rotationPlatform.addListener( new RotationPlatform.Adapter() {
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

    public RotationPlatform getRotationPlatform() {
        return rotationPlatform;
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

    public double getRadius() {
        return rotationPlatform.getRadius();
    }

    private void addRingNode( double fractionalRadius, Color color1, Color color2, boolean showBorder ) {
        contentNode.addChild( new RingNode( rotationPlatform.getCenter().getX(), rotationPlatform.getCenter().getY(), fractionalRadius * getRadius(), color1, color2, showBorder ) );
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

        private double x;
        private double y;
        private double radius;
        private double maxRadius;

        private RingPath path1;
        private RingPath path2;
        private RingPath path3;
        private RingPath path4;

        public RingNode( double x, double y, double radius, Color color1, Color color2, boolean showBorder ) {
            this.x = x;
            this.y = y;
            this.radius = radius;
            this.maxRadius = radius;
            path1 = addPath( color1, showBorder, 90 * 0, 90 * 1 );
            path2 = addPath( color2, showBorder, 90 * 1, 90 * 2 );
            path3 = addPath( color1, showBorder, 90 * 2, 90 * 3 );
            path4 = addPath( color2, showBorder, 90 * 3, 90 * 4 );

            update();
        }

        private RingPath addPath( Color color, boolean showBorder, double minArcDegrees, double maxArcDegrees ) {
            RingPath pPath = new RingPath( null, color, showBorder ? new BasicStroke( (float)( 1 * RotationPlayAreaNode.SCALE ) ) : null, Color.black, minArcDegrees, maxArcDegrees );
            addChild( pPath );
            return pPath;
        }

        class RingPath extends PhetPPath {
            private double minArcDegrees;
            private double maxArcDegrees;

            public RingPath( Shape shape, Paint fill, Stroke stroke, Paint strokePaint, double minArcDegrees, double maxArcDegrees ) {
                super( shape, fill, stroke, strokePaint );
                this.minArcDegrees = minArcDegrees;
                this.maxArcDegrees = maxArcDegrees;
            }

            public void update() {
                Arc2D.Double arc = new Arc2D.Double( getEllipseBounds(), minArcDegrees, maxArcDegrees - minArcDegrees, Arc2D.Double.PIE );
                setPathTo( arc );
            }
        }

        public double getMaxRadius() {
            return maxRadius;
        }

        public void setRadius( double radius ) {
            this.radius = radius;
            update();
        }

        private void update() {
            path1.update();
            path2.update();
            path3.update();
            path4.update();
        }

        private Rectangle2D.Double getEllipseBounds() {
            double r = radius;
            return new Rectangle2D.Double( x - r, y - r, r * 2, r * 2 );
        }
    }

}
