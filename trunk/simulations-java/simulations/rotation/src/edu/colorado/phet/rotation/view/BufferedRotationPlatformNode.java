// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.rotation.view;

import java.awt.*;
import java.awt.geom.Ellipse2D;

import edu.colorado.phet.common.motion.model.ITemporalVariable;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.piccolophet.nodes.HandleNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.rotation.model.RotationPlatform;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * User: Sam Reid
 * Date: Dec 28, 2006
 * Time: 2:44:08 PM
 */

public class BufferedRotationPlatformNode extends PNode {
    private PNode contentNode;
    private double angle = 0.0;
    private RotationPlatform rotationPlatform;
    private PhetPPath horizontalCrossHair;

    private PhetPPath outerBorder;

    public BufferedRotationPlatformNode( final RotationPlatform rotationPlatform ) {
        this.rotationPlatform = rotationPlatform;
        contentNode = new PNode();

        horizontalCrossHair = new PhetPPath( getHorizontalCrossHairPath(), new BasicStroke( (float) ( 2 * RotationPlayAreaNode.SCALE ) ), Color.black );
        contentNode.addChild( horizontalCrossHair );

        outerBorder = new PhetPPath( getBorderStroke(), Color.black );
        contentNode.addChild( outerBorder );

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
            }
        } );

        RotationPlatformNode rotationPlatformNode = new RotationPlatformNode( rotationPlatform );
        Image im = rotationPlatformNode.toImage( 500, 500, Color.white );
//        PImage image = new PImage( new BufferedImage( 100, 100, BufferedImage.TYPE_INT_RGB ) );
        PImage image = new PImage( im );

        image.scale( RotationPlayAreaNode.SCALE );
        image.translate( -image.getFullBounds().getWidth() / 2 / RotationPlayAreaNode.SCALE, -image.getFullBounds().getWidth() / 2 / RotationPlayAreaNode.SCALE );

//        contentNode.addChild( image );

        doUpdateAngle();
        updateRadius();
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

    private Shape getHorizontalCrossHairPath() {
        DoubleGeneralPath path = new DoubleGeneralPath();
        path.moveTo( rotationPlatform.getCenter().getX() - getRadius(), rotationPlatform.getCenter().getY() );
        path.lineTo( rotationPlatform.getCenter().getX() - rotationPlatform.getInnerRadius(), rotationPlatform.getCenter().getY() );
        path.moveTo( rotationPlatform.getCenter().getX() + getRadius(), rotationPlatform.getCenter().getY() );
        path.lineTo( rotationPlatform.getCenter().getX() + rotationPlatform.getInnerRadius(), rotationPlatform.getCenter().getY() );
        return path.getGeneralPath();
    }

    private void updateRadius() {
        horizontalCrossHair.setPathTo( getHorizontalCrossHairPath() );
//        handleNode.setPathTo( createHandlePath() );

        outerBorder.setPathTo( new Ellipse2D.Double( getRotationPlatform().getCenter().getX() - getRadius(), getRotationPlatform().getCenter().getY() - getRadius(), getRadius() * 2, getRadius() * 2 ) );
        updateAngle();
    }

    public double getRadius() {
        return rotationPlatform.getRadius();
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

}
