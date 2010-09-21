package edu.colorado.phet.rotation.view;

import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.motion.model.ITemporalVariable;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.piccolophet.nodes.HandleNode;
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
    private PhetPPath horizontalCrossHairLeft;
    private PhetPPath horizontalCrossHairRight;

    private PNode ringNodeLayer;
    private PhetPPath outerBorder;
    private PhetPPath innerBorder;

    public RotationPlatformNode( final RotationPlatform rotationPlatform ) {
        this.rotationPlatform = rotationPlatform;
        contentNode = new PNode();

        ringNodeLayer = new PNode();
        ringNodeLayer.addChild( createRingNode( 1.0, 0.75, new Color( 255, 215, 215 ), new Color( 255, 240, 240 ), false, false ) );
        ringNodeLayer.addChild( createRingNode( 0.75, 0.5, new Color( 140, 255, 140 ), new Color( 200, 255, 200 ), false, false ) );
        ringNodeLayer.addChild( createRingNode( 0.50, 0.25, new Color( 215, 215, 255 ), new Color( 240, 240, 255 ), false, false ) );
        ringNodeLayer.addChild( createRingNode( 0.25, 0.0, Color.white, Color.lightGray, false, false ) );
        contentNode.addChild( ringNodeLayer );

        verticalCrossHair = new PhetPPath( getVerticalCrossHairPath(), new BasicStroke( (float) ( 2 * RotationPlayAreaNode.SCALE ) ), Color.gray );
        contentNode.addChild( verticalCrossHair );

//        horizontalCrossHairLeft = new PhetPPath( getHorizontalCrossHairPathLeft(), new BasicStroke( (float) ( 3 * RotationPlayAreaNode.SCALE ), BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1f, new float[]{(float) ( 10 * RotationPlayAreaNode.SCALE ), (float) ( 6 * RotationPlayAreaNode.SCALE )}, 0 ), Color.black );
        horizontalCrossHairLeft = new PhetPPath( getHorizontalCrossHairPathLeft(), new BasicStroke( (float) ( 3 * RotationPlayAreaNode.SCALE ) ), Color.black );
        contentNode.addChild( horizontalCrossHairLeft );

        horizontalCrossHairRight = new PhetPPath( getHorizontalCrossHairPathRight(), new BasicStroke( (float) ( 2 * RotationPlayAreaNode.SCALE ) ), Color.gray );
        contentNode.addChild( horizontalCrossHairRight );

        outerBorder = new PhetPPath( getBorderStroke(), Color.black );
        innerBorder = new PhetPPath( getBorderStroke(), Color.black );
        contentNode.addChild( outerBorder );
        contentNode.addChild( innerBorder );

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

    private Stroke getBorderStroke() {
        return new BasicStroke( (float) ( 1 * RotationPlayAreaNode.SCALE ) );
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
        horizontalCrossHairLeft.setPathTo( getHorizontalCrossHairPathLeft() );
        horizontalCrossHairRight.setPathTo( getHorizontalCrossHairPathRight() );

//        handleNode.setPathTo( createHandlePath() );
        innerBorder.setPathTo( new Ellipse2D.Double( getRotationPlatform().getCenter().getX() - getInnerRadius(), getRotationPlatform().getCenter().getY() - getInnerRadius(), getInnerRadius() * 2, getInnerRadius() * 2 ) );
        innerBorder.setVisible( getInnerRadius() > 0 );
        updateAngle();
    }

    private double getInnerRadius() {
        return rotationPlatform.getInnerRadius();
    }

    private Shape getHorizontalCrossHairPathLeft() {
        return getHorizontalCrossHairPath( +1 );
    }

    private Shape getHorizontalCrossHairPath( double sign ) {
        DoubleGeneralPath path = new DoubleGeneralPath();
        path.moveTo( rotationPlatform.getCenter().getX() + sign * getRadius(), rotationPlatform.getCenter().getY() );
        path.lineTo( rotationPlatform.getCenter().getX() + sign * rotationPlatform.getInnerRadius(), rotationPlatform.getCenter().getY() );
        return path.getGeneralPath();
    }

    private Shape getHorizontalCrossHairPathRight() {
        return getHorizontalCrossHairPath( -1 );
    }

    private Shape getVerticalCrossHairPath() {
        DoubleGeneralPath path = new DoubleGeneralPath();
        path.moveTo( rotationPlatform.getCenter().getX(), rotationPlatform.getCenter().getY() - getRadius() );
        path.lineTo( rotationPlatform.getCenter().getX(), rotationPlatform.getCenter().getY() - rotationPlatform.getInnerRadius() );
        path.moveTo( rotationPlatform.getCenter().getX(), rotationPlatform.getCenter().getY() + getRadius() );
        path.lineTo( rotationPlatform.getCenter().getX(), rotationPlatform.getCenter().getY() + rotationPlatform.getInnerRadius() );
        return path.getGeneralPath();
    }

    private void updateRadius() {
        for ( int i = 0; i < ringNodeLayer.getChildrenCount(); i++ ) {
            RingNode node = (RingNode) ringNodeLayer.getChild( i );
            node.setRadius( Math.min( node.getMaxOuterRadius(), getRadius() ) );
        }
        verticalCrossHair.setPathTo( getVerticalCrossHairPath() );
        horizontalCrossHairLeft.setPathTo( getHorizontalCrossHairPathLeft() );
        horizontalCrossHairRight.setPathTo( getHorizontalCrossHairPathRight() );
//        handleNode.setPathTo( createHandlePath() );

        outerBorder.setPathTo( new Ellipse2D.Double( getRotationPlatform().getCenter().getX() - getRadius(), getRotationPlatform().getCenter().getY() - getRadius(), getRadius() * 2, getRadius() * 2 ) );
        updateAngle();
    }

    public double getRadius() {
        return rotationPlatform.getRadius();
    }

    private RingNode createRingNode( double outerRadius, double innerRadius, Color color1, Color color2, boolean showInnerBorder, boolean showOuterBorder ) {
        return new RingNode( rotationPlatform.getCenter().getX(), rotationPlatform.getCenter().getY(), outerRadius * getRadius(), innerRadius * getRadius(), color1, color2, showInnerBorder, showOuterBorder );
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

    protected void addContentNode( HandleNode handleNode2 ) {
        contentNode.addChild( handleNode2 );
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
        private PhetPPath innerBorderPath;
        private PhetPPath outerBorderPath;

        public RingNode( double x, double y, double outerRadius, double innerRadius, Color color1, Color color2, boolean showInnerBorder, boolean showOuterBorder ) {
            this.x = x;
            this.y = y;
            this.radius = outerRadius;
            this.maxOuterRadius = outerRadius;
            this.innerRadius = innerRadius;
            this.minInnerRadius = innerRadius;
            path1 = addPath( color1, false, 90 * 0, 90 * 1 );
            path2 = addPath( color2, false, 90 * 1, 90 * 2 );
            path3 = addPath( color1, false, 90 * 2, 90 * 3 );
            path4 = addPath( color2, false, 90 * 3, 90 * 4 );

            if ( showInnerBorder ) {
                innerBorderPath = new PhetPPath( getStrokePath(), Color.black );
                addChild( innerBorderPath );
            }
            if ( showOuterBorder ) {
                outerBorderPath = new PhetPPath( getStrokePath(), Color.black );
                addChild( outerBorderPath );
            }
            update();
        }

        private RingPath addPath( Color color, boolean showBorder, double minArcDegrees, double maxArcDegrees ) {
            RingPath pPath = new RingPath( null, color, showBorder ? getStrokePath() : null, Color.black, minArcDegrees, maxArcDegrees );
            addChild( pPath );
            return pPath;
        }

        private BasicStroke getStrokePath() {
            return new BasicStroke( (float) ( 1 * RotationPlayAreaNode.SCALE ) );
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
            if ( innerBorderPath != null ) {
                innerBorderPath.setPathTo( getInnerBounds() );
            }
            if ( outerBorderPath != null ) {
                outerBorderPath.setPathTo( getOuterBounds() );
            }
        }

        private Ellipse2D.Double getOuterBounds() {
            double r = radius;
            return new Ellipse2D.Double( x - r, y - r, r * 2, r * 2 );
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
