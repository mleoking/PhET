package edu.colorado.phet.rotation.view;

import java.awt.*;
import java.awt.geom.*;

import edu.colorado.phet.common.motion.model.ITemporalVariable;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.rotation.model.RotationPlatform;
import edu.umd.cs.piccolo.PNode;

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

    private double handleWidth = 10 * RotationPlayAreaNode.SCALE;
    private double handleHeight = 10 * RotationPlayAreaNode.SCALE;
    private PhetPPath handleNode;
    private PNode ringNodeLayer;

    public RotationPlatformNode( final RotationPlatform rotationPlatform ) {
        this.rotationPlatform = rotationPlatform;
        contentNode = new PNode();

        ringNodeLayer = new PNode();
        ringNodeLayer.addChild( createRingNode( 1.0, 0.75, new Color( 255, 215, 215 ), new Color( 255, 240, 240 ), true ) );
        ringNodeLayer.addChild( createRingNode( 0.75, 0.5, new Color( 140, 255, 140 ), new Color( 200, 255, 200 ), false ) );
        ringNodeLayer.addChild( createRingNode( 0.50, 0.25, new Color( 215, 215, 255 ), new Color( 240, 240, 255 ), false ) );
        ringNodeLayer.addChild( createRingNode( 0.25, 0.0, Color.white, Color.lightGray, false ) );
        contentNode.addChild( ringNodeLayer );

        verticalCrossHair = new PhetPPath( getVerticalCrossHairPath(), new BasicStroke( (float) ( 2 * RotationPlayAreaNode.SCALE ) ), Color.black );
        contentNode.addChild( verticalCrossHair );

        horizontalCrossHair = new PhetPPath( getHorizontalCrossHairPath(), new BasicStroke( (float) ( 2 * RotationPlayAreaNode.SCALE ) ), Color.black );
        contentNode.addChild( horizontalCrossHair );

        handleNode = new PhetPPath( createHandlePath(), Color.blue, new BasicStroke( (float) ( 1 * RotationPlayAreaNode.SCALE ) ), Color.black );
        contentNode.addChild( handleNode );

        addChild( contentNode );

        rotationPlatform.getPositionVariable().addListener( new ITemporalVariable.ListenerAdapter() {
            public void valueChanged() {
                doUpdateAngle();
            }
        } );
        rotationPlatform.addListener( new RotationPlatform.Adapter() {
            public void radiusChanged() {
                updateRadius();
            }

            public void innerRadiusChanged() {
                updateInnerRadius();
            }
        } );

        doUpdateAngle();
        updateRadius();
        updateInnerRadius();
    }

    private void doUpdateAngle() {
        setAngle( rotationPlatform.getPosition() );
    }

    public RotationPlatform getRotationPlatform() {
        return rotationPlatform;
    }

    private void updateInnerRadius() {
        double r = rotationPlatform.getInnerRadius();
        for ( int i = 0; i < ringNodeLayer.getChildrenCount(); i++ ) {
            RingNode node = (RingNode) ringNodeLayer.getChild( i );
            node.setVisible( node.getMaxOuterRadius() >= r );
            node.setInnerRadius( Math.max( node.getMinInnerRadius(), r ) );
        }
        verticalCrossHair.setPathTo( getVerticalCrossHairPath() );
        horizontalCrossHair.setPathTo( getHorizontalCrossHairPath() );
        handleNode.setPathTo( createHandlePath() );
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
        for ( int i = 0; i < ringNodeLayer.getChildrenCount(); i++ ) {
            RingNode node = (RingNode) ringNodeLayer.getChild( i );
            node.setRadius( Math.min( node.getMaxOuterRadius(), getRadius() ) );
        }
        verticalCrossHair.setPathTo( getVerticalCrossHairPath() );
        horizontalCrossHair.setPathTo( getHorizontalCrossHairPath() );
        handleNode.setPathTo( createHandlePath() );
        updateAngle();
    }

    public double getRadius() {
        return rotationPlatform.getRadius();
    }

    private RingNode createRingNode( double outerRadius, double innerRadius, Color color1, Color color2, boolean showBorder ) {
        return new RingNode( rotationPlatform.getCenter().getX(), rotationPlatform.getCenter().getY(), outerRadius * getRadius(), innerRadius * getRadius(), color1, color2, showBorder );
    }

    private void setAngle( double angle ) {
        if ( this.angle != angle ) {
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

    private static class RingNode extends PNode {

        private double x;
        private double y;
        private double radius;
        private double maxOuterRadius;
        private double innerRadius;

        private RingPath path1;
        private RingPath path2;
        private RingPath path3;
        private RingPath path4;
        private double minInnerRadius;

        public RingNode( double x, double y, double outerRadius, double innerRadius, Color color1, Color color2, boolean showBorder ) {
            this.x = x;
            this.y = y;
            this.radius = outerRadius;
            this.maxOuterRadius = outerRadius;
            this.innerRadius = innerRadius;
            this.minInnerRadius = innerRadius;
            path1 = addPath( color1, showBorder, 90 * 0, 90 * 1 );
            path2 = addPath( color2, showBorder, 90 * 1, 90 * 2 );
            path3 = addPath( color1, showBorder, 90 * 2, 90 * 3 );
            path4 = addPath( color2, showBorder, 90 * 3, 90 * 4 );

            update();
        }

        private RingPath addPath( Color color, boolean showBorder, double minArcDegrees, double maxArcDegrees ) {
            RingPath pPath = new RingPath( null, color, showBorder ? new BasicStroke( (float) ( 1 * RotationPlayAreaNode.SCALE ) ) : null, Color.black, minArcDegrees, maxArcDegrees );
            addChild( pPath );
            return pPath;
        }


        public double getMinInnerRadius() {
            return minInnerRadius;
        }

        public void setInnerRadius( double innerRadius ) {
            if ( this.innerRadius != innerRadius ) {
                this.innerRadius = innerRadius;
                update();
            }
        }

        private class RingPath extends PhetPPath {
            private double minArcDegrees;
            private double maxArcDegrees;

            public RingPath( Shape shape, Paint fill, Stroke stroke, Paint strokePaint, double minArcDegrees, double maxArcDegrees ) {
                super( shape, fill, stroke, strokePaint );
                this.minArcDegrees = minArcDegrees;
                this.maxArcDegrees = maxArcDegrees;
            }

            public void update() {
                Arc2D.Double outerBounds = new Arc2D.Double( getEllipseBounds(), minArcDegrees, maxArcDegrees - minArcDegrees, Arc2D.Double.PIE );
                Area area = new Area( outerBounds );
                area.subtract( new Area( getInnerBounds() ) );
                setPathTo( area );
            }
        }

        public double getMaxOuterRadius() {
            return maxOuterRadius;
        }

        public void setRadius( double radius ) {
            if ( this.radius != radius ) {
                this.radius = radius;
                update();
            }
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

        private Ellipse2D.Double getInnerBounds() {
            double r = innerRadius;
            return new Ellipse2D.Double( x - r, y - r, r * 2, r * 2 );
        }
    }

}
