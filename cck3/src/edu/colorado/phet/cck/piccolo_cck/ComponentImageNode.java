package edu.colorado.phet.cck.piccolo_cck;

import edu.colorado.phet.cck.ICCKModule;
import edu.colorado.phet.cck.model.CCKModel;
import edu.colorado.phet.cck.model.components.Branch;
import edu.colorado.phet.cck.model.components.CircuitComponent;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.view.util.RectangleUtils;
import edu.umd.cs.piccolo.nodes.PImage;

import javax.swing.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * User: Sam Reid
 * Date: Sep 17, 2006
 * Time: 7:49:22 PM
 * Copyright (c) Sep 17, 2006 by Sam Reid
 */

public abstract class ComponentImageNode extends ComponentNode {
    private PImage pImage;
    private FlameNode flameNode;

    public ComponentImageNode( final CCKModel model, final CircuitComponent circuitComponent, BufferedImage image, JComponent component, ICCKModule module ) {
        super( model, circuitComponent, component, module );
        pImage = new PImage( image );
        addChild( pImage );
        flameNode = new FlameNode( getBranch() );
        addChild( flameNode );
        getBranch().addFlameListener( new Branch.FlameListener() {
            public void flameStarted() {
                update();
            }

            public void flameFinished() {
                update();
            }
        } );
        update();
    }

    public PImage getpImage() {
        return pImage;
    }

    protected void update() {
        super.update();

        Rectangle2D aShape = new Rectangle2D.Double( 0, 0, pImage.getFullBounds().getWidth(), pImage.getFullBounds().getHeight() );
        aShape = RectangleUtils.expand( aShape, 2, 2 );
        getHighlightNode().setPathTo( aShape );
        getHighlightNode().setVisible( getBranch().isSelected() );

        double resistorLength = getBranch().getStartPoint().distance( getBranch().getEndPoint() );
        double imageLength = pImage.getFullBounds().getWidth();
        double sx = resistorLength / imageLength;
        double sy = getCircuitComponent().getHeight() / pImage.getFullBounds().getHeight();
        double angle = new Vector2D.Double( getBranch().getStartPoint(), getBranch().getEndPoint() ).getAngle();
        setTransform( new AffineTransform() );
        if( Math.abs( sx ) > 1E-4 ) {
            setScale( sx );
        }
        setOffset( getBranch().getStartPoint() );
        rotate( angle );
        translate( 0, -pImage.getFullBounds().getHeight() / 2 );

        flameNode.setOffset( 0, -flameNode.getFullBounds().getHeight() + pImage.getFullBounds().getHeight() / 2 );
    }

}
